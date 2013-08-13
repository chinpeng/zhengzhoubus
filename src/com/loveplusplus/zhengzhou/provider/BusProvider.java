package com.loveplusplus.zhengzhou.provider;

import static com.loveplusplus.zhengzhou.util.LogUtils.LOGV;
import static com.loveplusplus.zhengzhou.util.LogUtils.makeLogTag;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.loveplusplus.zhengzhou.provider.BusContract.BusLine;
import com.loveplusplus.zhengzhou.provider.BusContract.BusLineStation;
import com.loveplusplus.zhengzhou.provider.BusContract.Favorite;
import com.loveplusplus.zhengzhou.provider.BusDatabase.Tables;
import com.loveplusplus.zhengzhou.util.SelectionBuilder;

public class BusProvider extends ContentProvider {

	private BusDatabase mOpenHelper;
	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private static final int BUS_SEARCH_SUGGEST = 101;// 搜索建议

	private static final int LINES = 102;// 公交列表
	private static final int LINES_ID = 103;// 公交详细信息

	private static final int FAVORITES = 201;
	private static final int FAVORITES_id = 202;

	private static final int STATIONS = 203;
	private static final int STATIONS_ID = 204;

	private static final String TAG = makeLogTag(BusProvider.class);

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = BusContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, Tables.FAVORITE, FAVORITES);
		matcher.addURI(authority, Tables.FAVORITE + "/*", FAVORITES_id);

		matcher.addURI(authority, SearchManager.SUGGEST_URI_PATH_QUERY,
				BUS_SEARCH_SUGGEST);

		// matcher.addURI(authority,SearchManager.SUGGEST_URI_PATH_QUERY+ "/*",
		// BUS_SEARCH_SUGGEST);

		matcher.addURI(authority, Tables.BUS_LINE, LINES);
		matcher.addURI(authority, Tables.BUS_LINE + "/*", LINES_ID);

		matcher.addURI(authority, Tables.BUS_LINE_STATION, STATIONS);
		matcher.addURI(authority, Tables.BUS_LINE_STATION + "/*", STATIONS_ID);

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
		LOGV(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection)
				+ ")");
		final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

		final int match = sUriMatcher.match(uri);

		switch (match) {
		default: {
			// Most cases are handled with simple SelectionBuilder
			final SelectionBuilder builder = buildSimpleSelection(uri);
			return builder.where(selection, selectionArgs).query(db,
					projection, sortOrder);
		}
		case BUS_SEARCH_SUGGEST: {
			final SelectionBuilder builder = new SelectionBuilder();

			selectionArgs[0] = selectionArgs[0] + "%";

			// sb.append("line_name AS suggest_intent_data_id,");
			// sb.append("line_name AS  suggest_text_1,");
			// sb.append("'开往'||station_name AS  suggest_text_2");

			builder.table(Tables.BUS_LINE);
			builder.where(selection, selectionArgs);
			builder.map(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
					BusLine.LINE_NAME);
			builder.map(SearchManager.SUGGEST_COLUMN_TEXT_1, BusLine.LINE_NAME);
			builder.map(SearchManager.SUGGEST_COLUMN_TEXT_2,
					"'从'||start_station||'开往'||end_station");

			projection = new String[] { BaseColumns._ID,
					SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
					SearchManager.SUGGEST_COLUMN_TEXT_1,
					SearchManager.SUGGEST_COLUMN_TEXT_2 };

			final String limit = uri
					.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT);
			return builder.query(db, projection, null, null, null, limit);
		}
		case LINES: {
			final SelectionBuilder builder = new SelectionBuilder();
			builder.table(Tables.BUS_LINE);
			builder.where(selection, selectionArgs);
			
			builder.map("to_station","'从'||start_station||'开往'||end_station");
			
			projection = new String[] { BaseColumns._ID,
					BusLine.LINE_NAME,
					"to_station" };
			
			return builder.query(db, projection, null);
		}
		}
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

		case FAVORITES:
			return Favorite.CONTENT_TYPE;
		case FAVORITES_id:
			return Favorite.CONTENT_ITEM_TYPE;
		case LINES:
			return BusLine.CONTENT_TYPE;
		case LINES_ID:
			return BusLine.CONTENT_ITEM_TYPE;
		case STATIONS:
			return BusLineStation.CONTENT_TYPE;
		case STATIONS_ID:
			return BusLineStation.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case FAVORITES:
			db.insertOrThrow(Tables.FAVORITE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Favorite.buildUri(values.getAsString(BaseColumns._ID));
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		LOGV(TAG, "delete(uri=" + uri + ")");

		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final SelectionBuilder builder = buildSimpleSelection(uri);
		int retVal = builder.where(selection, selectionArgs).delete(db);
		getContext().getContentResolver().notifyChange(uri, null, false);
		return retVal;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		LOGV(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		final SelectionBuilder builder = buildSimpleSelection(uri);
		int retVal = builder.where(selection, selectionArgs).update(db, values);
		getContext().getContentResolver().notifyChange(uri, null, false);
		return retVal;
	}

	private SelectionBuilder buildSimpleSelection(Uri uri) {
		final SelectionBuilder builder = new SelectionBuilder();
		final int match = sUriMatcher.match(uri);
		switch (match) {

		case LINES_ID: {
			final String id = BusLine.getId(uri);
			return builder.table(Tables.BUS_LINE).where(
					BusLine.LINE_NAME + "=?", id);
		}
		case FAVORITES: {
			return builder.table(Tables.FAVORITE);
		}
		case FAVORITES_id: {
			final String id = Favorite.getId(uri);
			return builder.table(Tables.FAVORITE)
					.where(Favorite._ID + "=?", id);
		}
		case STATIONS: {
			return builder.table(Tables.BUS_LINE_STATION);
		}
		case STATIONS_ID: {
			final String id = BusLineStation.getId(uri);
			return builder.table(Tables.BUS_LINE_STATION).where(
					BusLineStation.LINE_NAME + "=?", id);
		}

		default: {
			throw new UnsupportedOperationException("Unknown uri for " + match
					+ ": " + uri);
		}
		}
	}

	@Override
	public ContentProviderResult[] applyBatch(
			ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			final int numOperations = operations.size();
			final ContentProviderResult[] results = new ContentProviderResult[numOperations];
			for (int i = 0; i < numOperations; i++) {
				results[i] = operations.get(i).apply(this, results, i);
			}
			db.setTransactionSuccessful();
			return results;
		} finally {
			db.endTransaction();
		}
	}

}
