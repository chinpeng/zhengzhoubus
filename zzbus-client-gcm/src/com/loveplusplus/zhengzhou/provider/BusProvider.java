package com.loveplusplus.zhengzhou.provider;

import java.util.Arrays;
import java.util.HashMap;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.loveplusplus.zhengzhou.provider.BusContract.Bus;
import com.loveplusplus.zhengzhou.provider.BusContract.BusColumns;
import com.loveplusplus.zhengzhou.provider.BusContract.Favorite;
import com.loveplusplus.zhengzhou.provider.BusContract.LineColumns;
import com.loveplusplus.zhengzhou.provider.BusContract.StationColumns;
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
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case BUS_SEARCH_SUGGEST:
			if (selectionArgs == null) {
				throw new IllegalArgumentException(
						"selectionArgs must be provided for the Uri: " + uri);
			}

			return getBusSuggestions(selectionArgs[0]);
		case BUS_DETAIL:
			SQLiteQueryBuilder builder1 = new SQLiteQueryBuilder();
			builder1.setTables("line join station on(line_station_id=station._id) join bus on(line_bus_id=bus._id)");

			HashMap<String, String> map = new HashMap<String, String>();

			map.put(BusColumns.NAME, BusColumns.NAME);

			map.put(LineColumns.DIRECT, LineColumns.DIRECT);
			map.put(LineColumns.SNO, LineColumns.SNO);
			map.put(BaseColumns._ID, "station._id AS " + BaseColumns._ID);
			map.put(StationColumns.NAME, "station_name AS "
					+ StationColumns.NAME);
			builder1.setProjectionMap(map);

			return builder1
					.query(db, null, "line_bus_id=? and line_direct=?",
							new String[] { uri.getLastPathSegment(),
									selectionArgs[0] }, null, null, " line_sno");

		default:
			final SelectionBuilder builder = buildExpandedSelection(uri, match);
			return builder.where(selection, selectionArgs).query(db,
					projection, sortOrder);
		}
	}

	private Cursor getBusSuggestions(String query) {
		String[] columns = new String[] { BaseColumns._ID,
				SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
				SearchManager.SUGGEST_COLUMN_TEXT_1,
				SearchManager.SUGGEST_COLUMN_TEXT_2 };

		return mOpenHelper.getBusMatches(query, columns);

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
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
		final SelectionBuilder builder = new SelectionBuilder();
		switch (match) {
		case FAVORITE:
			return builder.table(Tables.FAVORITE)
					.mapToTable(Favorite._ID, Tables.FAVORITE)
					.mapToTable(Favorite.BUS_NAME, Tables.FAVORITE)
					.mapToTable(Favorite.DIRECT, Tables.FAVORITE)
					.mapToTable(Favorite.SNO, Tables.FAVORITE)
					.mapToTable(Favorite.STATION_NAME, Tables.FAVORITE);
		case FAVORITE_LIST:
			return builder.table(Tables.FAVORITE)
					.mapToTable(Favorite._ID, Tables.FAVORITE)
					.mapToTable(Favorite.BUS_NAME, Tables.FAVORITE)
					.mapToTable(Favorite.DIRECT, Tables.FAVORITE)
					.mapToTable(Favorite.SNO, Tables.FAVORITE)
					.mapToTable(Favorite.STATION_NAME, Tables.FAVORITE);

		case BUS_LIST:
			return builder.table(Tables.BUS).mapToTable(Bus._ID, Tables.BUS)
					.mapToTable(Bus.NAME, Tables.BUS)
					.mapToTable(Bus.CARD, Tables.BUS)
					.mapToTable(Bus.COMPANY, Tables.BUS)
					.mapToTable(Bus.START_TIME, Tables.BUS)
					.mapToTable(Bus.DEFINITION, Tables.BUS)
					.mapToTable(Bus.END_TIME, Tables.BUS);
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}
}
