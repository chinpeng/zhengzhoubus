/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loveplusplus.zhengzhou.provider;

import java.util.Arrays;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.loveplusplus.zhengzhou.provider.BusContract.Favorite;
import com.loveplusplus.zhengzhou.provider.BusDatabase.Tables;
import com.loveplusplus.zhengzhou.util.SelectionBuilder;

/**
 * Provides access to the dictionary database.
 */
public class BusProvider extends ContentProvider {
	private String TAG = "BusProvider";

	private BusDatabase mOpenHelper;
	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private static final int FAVORITE_LIST = 101;
	private static final int FAVORITE = 102;
	private static final int SEARCH_SUGGEST = 2;

	/**
	 * Builds up a UriMatcher for search suggestion and shortcut refresh
	 * queries.
	 */
	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = BusContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, BusContract.PATH_FAVORITE, FAVORITE_LIST);
		matcher.addURI(authority, BusContract.PATH_FAVORITE + "/*", FAVORITE);

		matcher.addURI(authority, SearchManager.SUGGEST_URI_PATH_QUERY,
				SEARCH_SUGGEST);
		matcher.addURI(authority, SearchManager.SUGGEST_URI_PATH_QUERY + "/*",
				SEARCH_SUGGEST);

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
		default:
			final SelectionBuilder builder = buildExpandedSelection(uri, match);
			return builder.where(selection, selectionArgs).query(db,
					projection, sortOrder);
		}
	}

	// private Cursor getFavoriteBus(Uri uri) {
	// String[] columns = new String[] { BusDatabase.FavoriteColumns._ID,
	// BusDatabase.FavoriteColumns.BUS_NAME,
	// BusDatabase.FavoriteColumns.DIRECT,
	// BusDatabase.FavoriteColumns.SNO,
	// BusDatabase.FavoriteColumns.STATION_NAME };
	//
	// return mOpenHelper.getFavoriteBus(columns);
	// }

	// private Cursor getSuggestions(String query) {
	// Log.d(TAG, query);
	//
	// String[] columns = new String[] { BusColumns._ID,
	// SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
	// SearchManager.SUGGEST_COLUMN_TEXT_1,
	// SearchManager.SUGGEST_COLUMN_TEXT_2 };
	//
	// return mOpenHelper.getBusMatches(query, columns);
	// }

	
	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case FAVORITE:
			return Favorite.CONTENT_ITEM_TYPE;
		case FAVORITE_LIST:
			return Favorite.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	// Other required implementations...

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
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

		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}
}
