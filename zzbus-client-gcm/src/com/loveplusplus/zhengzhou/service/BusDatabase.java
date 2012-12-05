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

package com.loveplusplus.zhengzhou.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Contains logic to return specific words from the dictionary, and load the
 * dictionary table when it needs to be created.
 */
public class BusDatabase extends SQLiteOpenHelper {
	
	//String sql = "select id,name,card,company,start_time,end_time,price from bus where name=?";
	//String sql = "SELECT l.direct,l.sno,s.id,s.name FROM line l ,station s where l.bus_id=? and l.station_id=s.id order by l.sno ";
	private static final String TAG = "BusDatabase";
	private static final String DATABASE_NAME = "bus";
	private static final int DATABASE_VERSION = 1;

	private Context context;
	private static final HashMap<String, String> mColumnMap = buildColumnMap();

	public interface Tables {
		String BUS = "bus";
		String STATION = "station";
		String LINE = "line";
		String FAVORITE = "favorite";
	}

	public interface BusColumns {
		String _ID = "_id";
		String NAME = "name";
		String START_TIME = "start_time";
		String END_TIME = "end_time";
		String PRICE = "price";
		String CARD = "card";
		String COMPANY = "company";
		String DEFINITION = "definition";
		
	}

	public interface LineColumns {
		String _ID = "_id";
		String BUS_ID = "bus_id";
		String STATION_ID = "station_id";
		String DIRECT = "direct";
		String SNO = "son";
		
	}

	public interface StationColumns {
		String _ID="_id";
		String NAME = "name";
	}

	public interface FavoriteColumns {
		String _ID = "_id";
		String BUS_NAME = "bus_name";
		String DIRECT = "direct";
		String SNO = "sno";
		String STATION_NAME = "station_name";
	}

	public BusDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context=context;
	}

	/**
	 * Builds a map for all columns that may be requested, which will be given
	 * to the SQLiteQueryBuilder. This is a good way to define aliases for
	 * column names, but must include all columns, even if the value is the key.
	 * This allows the ContentProvider to request columns w/o the need to know
	 * real column names and create the alias itself.
	 */
	private static HashMap<String, String> buildColumnMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(BusColumns.NAME, BusColumns.NAME);
		map.put(BusColumns.DEFINITION, BusColumns.DEFINITION);
		map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS "
				+ SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS "
				+ SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
		return map;
	}

	/**
	 * Returns a Cursor positioned at the word specified by rowId
	 * 
	 * @param rowId
	 *            id of word to retrieve
	 * @param columns
	 *            The columns to include, if null then all are included
	 * @return Cursor positioned to matching word, or null if not found.
	 */
	public Cursor getWord(String rowId, String[] columns) {
		String selection = "rowid = ?";
		String[] selectionArgs = new String[] { rowId };

		return query(selection, selectionArgs, columns);

		/*
		 * This builds a query that looks like: SELECT <columns> FROM <table>
		 * WHERE rowid = <rowId>
		 */
	}

	/**
	 * Returns a Cursor over all words that match the given query
	 * 
	 * @param query
	 *            The string to search for
	 * @param columns
	 *            The columns to include, if null then all are included
	 * @return Cursor over all words that match, or null if none found.
	 */
	public Cursor getWordMatches(String query, String[] columns) {
		String selection = BusColumns.NAME + " MATCH ?";
		String[] selectionArgs = new String[] { query + "*" };

		return query(selection, selectionArgs, columns);

		/*
		 * This builds a query that looks like: SELECT <columns> FROM <table>
		 * WHERE <KEY_WORD> MATCH 'query*' which is an FTS3 search for the query
		 * text (plus a wildcard) inside the word column.
		 * 
		 * - "rowid" is the unique id for all rows but we need this value for
		 * the "_id" column in order for the Adapters to work, so the columns
		 * need to make "_id" an alias for "rowid" - "rowid" also needs to be
		 * used by the SUGGEST_COLUMN_INTENT_DATA alias in order for suggestions
		 * to carry the proper intent data. These aliases are defined in the
		 * DictionaryProvider when queries are made. - This can be revised to
		 * also search the definition text with FTS3 by changing the selection
		 * clause to use FTS_VIRTUAL_TABLE instead of KEY_WORD (to search across
		 * the entire table, but sorting the relevance could be difficult.
		 */
	}

	/**
	 * Performs a database query.
	 * 
	 * @param selection
	 *            The selection clause
	 * @param selectionArgs
	 *            Selection arguments for "?" components in the selection
	 * @param columns
	 *            The columns to return
	 * @return A Cursor over all rows matching the query
	 */
	private Cursor query(String selection, String[] selectionArgs,
			String[] columns) {
		/*
		 * The SQLiteBuilder provides a map for all possible columns requested
		 * to actual columns in the database, creating a simple column alias
		 * mechanism by which the ContentProvider does not need to know the real
		 * column names
		 */
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(Tables.BUS);
		builder.setProjectionMap(mColumnMap);

		Cursor cursor = builder.query(getReadableDatabase(), columns, selection,
				selectionArgs, null, null, null);

		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		return cursor;
	}

	public Cursor getFavoriteBus(String[] columns) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(Tables.FAVORITE);
		Cursor cursor = builder.query(getReadableDatabase(), columns, null, null,
				null, null, null);
		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		return cursor;
	}

	public void saveFavoriteBus(String hczd, String sno, String ud, String xl) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(FavoriteColumns.STATION_NAME, hczd);
		initialValues.put(FavoriteColumns.SNO, sno);
		initialValues.put(FavoriteColumns.DIRECT, ud);
		initialValues.put(FavoriteColumns.BUS_NAME, xl);
		db.insert(Tables.FAVORITE, null, initialValues);
		db.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "+Tables.BUS+"("
				+BusColumns._ID+"  INTEGER PRIMARY KEY AUTOINCREMENT,"
				+BusColumns.NAME+"  TEXT NOT NULL,"
				+BusColumns.START_TIME+"  TEXT,"
				+BusColumns.END_TIME+"  TEXT,"
				+BusColumns.PRICE+"  TEXT,"
				+BusColumns.CARD+"  TEXT,"
				+BusColumns.DEFINITION+"  TEXT,"
				+BusColumns.COMPANY+"  TEXT)");
		
		db.execSQL("CREATE TABLE "+Tables.STATION+" ("
				+ StationColumns._ID+  " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+StationColumns.NAME+ " TEXT NOT NULL )");
		
		db.execSQL("CREATE TABLE "+Tables.LINE+"("
				+LineColumns._ID  +" INTEGER PRIMARY KEY AUTOINCREMENT,"
				+LineColumns.BUS_ID+"  INTEGER,"
				+LineColumns.STATION_ID+"  INTEGER,"
				+LineColumns.DIRECT+"  INTEGER,"
				+LineColumns.SNO+"  INTEGER,"
				+"FOREIGN KEY ("+LineColumns.BUS_ID+") REFERENCES "+Tables.BUS+"("+BusColumns._ID+"),"
				+"FOREIGN KEY ("+LineColumns.STATION_ID+") REFERENCES "+Tables.STATION+" ("+StationColumns._ID+"),"
				+"UNIQUE ("+LineColumns.BUS_ID+" , "+LineColumns.STATION_ID+" , "+LineColumns.DIRECT+" , "+LineColumns.SNO+" ) ON CONFLICT REPLACE)");

		db.execSQL("CREATE  TABLE " + Tables.FAVORITE + "(" 
				+ FavoriteColumns._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ FavoriteColumns.STATION_NAME+ " TEXT,"
				+ FavoriteColumns.SNO + " INTEGER,"
				+ FavoriteColumns.DIRECT + " INTEGER," 
				+ FavoriteColumns.BUS_NAME + " TEXT)");
		
		loadBusData();

	}

	private void loadBusData() {
//		try {
//			AssetManager assetManager = context.getResources().getAssets();
//			
//			//InputStream is1 = assetManager.open("/db/bus.xml");
//			//InputStream is2 = assetManager.open("/db/station.xml");
//			//InputStream is3 = assetManager.open("/db/line.xml");
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + Tables.FAVORITE);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.LINE);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.STATION);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.BUS);
		onCreate(db);
	}

}
