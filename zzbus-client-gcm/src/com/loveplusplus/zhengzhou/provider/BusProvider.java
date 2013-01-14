package com.loveplusplus.zhengzhou.provider;

import java.util.Arrays;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.loveplusplus.zhengzhou.provider.BusContract.Bus;
import com.loveplusplus.zhengzhou.provider.BusContract.Favorite;
import com.loveplusplus.zhengzhou.provider.BusDatabase.Tables;
import com.loveplusplus.zhengzhou.util.SelectionBuilder;

public class BusProvider extends ContentProvider {
	private String TAG = "BusProvider";

	private BusDatabase mOpenHelper;
	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private static final int BUS_SEARCH_SUGGEST = 101;// 搜索建议

	private static final int BUS_LIST = 102;// 公交列表
	private static final int BUS_DETAIL = 103;// 公交详细信息

	private static final int FAVORITE_LIST = 201;
	private static final int FAVORITE = 202;

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = BusContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, BusContract.PATH_FAVORITE, FAVORITE_LIST);
		matcher.addURI(authority, BusContract.PATH_FAVORITE + "/*", FAVORITE);

		matcher.addURI(authority,
				"bus/" + SearchManager.SUGGEST_URI_PATH_QUERY,
				BUS_SEARCH_SUGGEST);

		matcher.addURI(authority, "bus/" + SearchManager.SUGGEST_URI_PATH_QUERY
				+ "/*", BUS_SEARCH_SUGGEST);

		matcher.addURI(authority, BusContract.PATH_BUS, BUS_LIST);
		matcher.addURI(authority, BusContract.PATH_BUS + "/*", BUS_DETAIL);
		return matcher;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new BusDatabase(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Log.d(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection)
				+ ")");
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case BUS_SEARCH_SUGGEST:
			return getBusSuggestions(selectionArgs[0]);
		case FAVORITE_LIST:
			return getFavoriteList();
		case BUS_LIST:
			return getBusList(selectionArgs[0]);
		case BUS_DETAIL:
			String lineName = uri.getLastPathSegment();
			return getBusDetail(selectionArgs[0], lineName);
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);

		}
	}

	private Cursor getBusList(String query) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT _id,");
		sb.append("line_name AS  suggest_text_1,");
		sb.append("'开往'||station_name AS  suggest_text_2");
		sb.append(" FROM bus");
		sb.append(" WHERE ");
		sb.append(" is_up_down=0 AND ");
		sb.append(" line_name LIKE ? OR ");
		sb.append(" line_name LIKE ? OR ");
		sb.append(" line_name LIKE ? ");
		sb.append(" GROUP BY line_name ");
		sb.append(" order by _id desc");
		return queryBySQL(sb.toString(), new String[] { "B" + query + "%",
				"Y" + query + "%", query + "%" });
	}

	/**
	 * 获取站点信息
	 * 
	 * @param direct
	 *            0上行 1下行
	 * @param lineName
	 *            线路名称
	 * @return
	 */
	private Cursor getBusDetail(String direct, String lineName) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT _id,is_up_down,line_name,station_name,label_no ");
		sb.append(" FROM bus");
		sb.append(" WHERE ");
		sb.append(" is_up_down=? AND ");
		sb.append(" line_name=? order by label_no asc");
		return queryBySQL(sb.toString(), new String[] { direct, lineName });
	}

	private Cursor getFavoriteList() {
		final SelectionBuilder builder = new SelectionBuilder();
		SelectionBuilder sb = builder.table(Tables.FAVORITE)
				.mapToTable(Favorite._ID, Tables.FAVORITE)
				.mapToTable(Favorite.BUS_NAME, Tables.FAVORITE)
				.mapToTable(Favorite.DIRECT, Tables.FAVORITE)
				.mapToTable(Favorite.SNO, Tables.FAVORITE)
				.mapToTable(Favorite.STATION_NAME, Tables.FAVORITE);
		return sb.query(mOpenHelper.getReadableDatabase(), null, null, null,
				null, null);
	}

	/**
	 * 搜索建议
	 * 
	 * @param query
	 * @return
	 */
	private Cursor getBusSuggestions(String query) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT _id,");
		sb.append("line_name AS suggest_intent_data_id,");
		sb.append("line_name AS  suggest_text_1,");
		sb.append("'开往'||station_name AS  suggest_text_2");
		sb.append(" FROM bus");
		sb.append(" WHERE ");
		sb.append(" is_up_down=0 AND ");
		sb.append(" line_name LIKE ? OR ");
		sb.append(" line_name LIKE ? OR ");
		sb.append(" line_name LIKE ? ");
		sb.append(" GROUP BY line_name ");
		sb.append(" order by _id desc");
		return queryBySQL(sb.toString(), new String[] { "B" + query + "%",
				"Y" + query + "%", query + "%" });
	}

	private Cursor queryBySQL(String sql, String[] selectionArgs) {

		Log.d(TAG, sql);
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();

		Cursor cursor = db.rawQuery(sql, selectionArgs);
		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case FAVORITE:
			return Favorite.CONTENT_ITEM_TYPE;
		case FAVORITE_LIST:
			return Favorite.CONTENT_TYPE;
		case BUS_LIST:
			return Bus.CONTENT_TYPE;
		case BUS_DETAIL:
			return Bus.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d(TAG, "insert(uri=" + uri);
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case FAVORITE_LIST:
			db.insertOrThrow(Tables.FAVORITE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Favorite.buildFavoriteUri(values
					.getAsString(BaseColumns._ID));
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.d(TAG, "delete(uri=" + uri);
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case FAVORITE:
			int count = db.delete(Tables.FAVORITE, selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

}
