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
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

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

import com.loveplusplus.zhengzhou.bean.Bus;
import com.loveplusplus.zhengzhou.bean.LineTemp;
import com.loveplusplus.zhengzhou.bean.Station;
import com.loveplusplus.zhengzhou.util.XmlUtil;

/**
 * Contains logic to return specific words from the dictionary, and load the
 * dictionary table when it needs to be created.
 */
public class BusDatabase extends SQLiteOpenHelper {
	
	//String sql = "select id,name,card,company,start_time,end_time,price from bus where name=?";
	//String sql = "SELECT l.direct,l.sno,s.id,s.name FROM line l ,station s where l.bus_id=? and l.station_id=s.id order by l.sno ";
	private static final String TAG = "BusDatabase";
	private static final String DATABASE_NAME = "bus";
	private static final int DATABASE_VERSION = 4;

	private Context context;

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
	public Cursor getLine(String busId, String[] columns) {
		String selection = "line.bus_id = ?";
		String[] selectionArgs = new String[] { busId };
		
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(Tables.LINE+" inner join "+Tables.STATION+" on(line._id=station._id) ");
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put(LineColumns.DIRECT, LineColumns.DIRECT);
		map.put(LineColumns.SNO, LineColumns.SNO);
		map.put(StationColumns._ID, "station._id AS "+StationColumns._ID);
		map.put(StationColumns.NAME, "station.name AS "+StationColumns.NAME);
		builder.setProjectionMap(map);

		Cursor cursor = builder.query(getReadableDatabase(), columns, selection,
				selectionArgs, null, null, null);

		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		Log.d(TAG, ""+cursor.getCount());
		Log.d(TAG, cursor.getString(1));
		return cursor;
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
		String selection = BusColumns.NAME + " like ?";
		String[] selectionArgs = new String[] { query + "%" };

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
	public Cursor getBusMatches(String query, String[] columns) {
		String selection = BusColumns.NAME + " like ?";
		String[] selectionArgs = new String[] { query + "%" };
		
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(Tables.BUS);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(BusColumns.NAME, BusColumns.NAME);
		map.put(BusColumns.DEFINITION, BusColumns.DEFINITION);
		map.put(BusColumns.START_TIME, BusColumns.START_TIME);
		map.put(BusColumns.END_TIME, BusColumns.END_TIME);
		map.put(BusColumns.PRICE, BusColumns.PRICE);
		
		map.put(BusColumns._ID, BusColumns._ID);
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "_id AS "+ SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		
		builder.setProjectionMap(map);

		Cursor cursor = builder.query(getReadableDatabase(), columns, selection,
				selectionArgs, null, null, null);

		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		Log.d(TAG, "----->"+cursor.getCount());
		return cursor;
		
	}
	public Cursor getBusByName(String query, String[] columns) {
		String selection = BusColumns.NAME + " like ?";
		String[] selectionArgs = new String[] { query + "%" };
		
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(Tables.BUS);
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put(BusColumns.NAME, BusColumns.NAME);
		map.put(BusColumns.DEFINITION, BusColumns.DEFINITION);
		map.put(BusColumns._ID,  BusColumns._ID);
		
		builder.setProjectionMap(map);

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


	private Cursor query(String selection, String[] selectionArgs,
			String[] columns) {
		
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(Tables.BUS);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(BusColumns.NAME, BusColumns.NAME);
		map.put(BusColumns.DEFINITION, BusColumns.DEFINITION);
		map.put(BusColumns._ID, BusColumns._ID);
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "_id AS "+ SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		
		builder.setProjectionMap(map);

		Cursor cursor = builder.query(getReadableDatabase(), columns, selection,
				selectionArgs, null, null, null);

		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		Log.d(TAG, "----->"+cursor.getCount());
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
		Log.d(TAG, "创建数据库 bus");
		db.execSQL("CREATE TABLE "+Tables.BUS+"("
				+BusColumns._ID+"  INTEGER PRIMARY KEY AUTOINCREMENT,"
				+BusColumns.NAME+"  TEXT NOT NULL,"
				+BusColumns.START_TIME+"  TEXT,"
				+BusColumns.END_TIME+"  TEXT,"
				+BusColumns.PRICE+"  TEXT,"
				+BusColumns.CARD+"  TEXT,"
				+BusColumns.DEFINITION+"  TEXT,"
				+BusColumns.COMPANY+"  TEXT)");
		Log.d(TAG, "创建数据库 station");
		db.execSQL("CREATE TABLE "+Tables.STATION+" ("
				+ StationColumns._ID+  " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+StationColumns.NAME+ " TEXT NOT NULL )");
		Log.d(TAG, "创建数据库 line");
		db.execSQL("CREATE TABLE "+Tables.LINE+"("
				+LineColumns._ID  +" INTEGER PRIMARY KEY AUTOINCREMENT,"
				+LineColumns.BUS_ID+"  INTEGER,"
				+LineColumns.STATION_ID+"  INTEGER,"
				+LineColumns.DIRECT+"  INTEGER,"
				+LineColumns.SNO+"  INTEGER,"
				+"FOREIGN KEY ("+LineColumns.BUS_ID+") REFERENCES "+Tables.BUS+"("+BusColumns._ID+"),"
				+"FOREIGN KEY ("+LineColumns.STATION_ID+") REFERENCES "+Tables.STATION+" ("+StationColumns._ID+"),"
				+"UNIQUE ("+LineColumns.BUS_ID+" , "+LineColumns.STATION_ID+" , "+LineColumns.DIRECT+" , "+LineColumns.SNO+" ) ON CONFLICT REPLACE)");

		Log.d(TAG, "创建数据库 favorite");
		db.execSQL("CREATE  TABLE " + Tables.FAVORITE + "(" 
				+ FavoriteColumns._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ FavoriteColumns.STATION_NAME+ " TEXT,"
				+ FavoriteColumns.SNO + " INTEGER,"
				+ FavoriteColumns.DIRECT + " INTEGER," 
				+ FavoriteColumns.BUS_NAME + " TEXT)");
		
		loadBusData(db);

	}

	private void loadBusData(SQLiteDatabase db) {
		try {
			Log.d(TAG, "加载数据");
			AssetManager assetManager = context.getResources().getAssets();
			
			InputStream is1 = assetManager.open("db/bus.xml");
			List<Bus> buses=XmlUtil.loadBus(is1);
			saveBus(db,buses);
			
			InputStream is2 = assetManager.open("db/station.xml");
			List<Station> stations=XmlUtil.loadStation(is2);
			saveStation(db,stations);
			
			InputStream is3 = assetManager.open("db/line.xml");
			List<LineTemp> lines=XmlUtil.loadLine(is3);
			saveLine(db,lines);
			Log.d(TAG, "加载数据");
		} catch (IOException e) {
			Log.e(TAG, " 加载数据异常"+e.getMessage());
		} catch (NumberFormatException e) {
			Log.e(TAG, " 加载数据异常"+e.getMessage());
		} catch (XmlPullParserException e) {
			Log.e(TAG, "读取xml数据异常"+e.getMessage());
		}
	}

	private void saveLine(SQLiteDatabase db, List<LineTemp> lines) {
		Log.d(TAG, " 开始加载line:"+lines.size());
		db.beginTransaction();        //手动设置开始事务
		//数据插入操作循环
		for(LineTemp l:lines){
			ContentValues values=new ContentValues();
			values.put(LineColumns._ID, l.getId());
			values.put(LineColumns.BUS_ID, l.getBusId());
			values.put(LineColumns.DIRECT, l.getDirect());
			values.put(LineColumns.SNO, l.getSno());
			values.put(LineColumns.STATION_ID, l.getStationId());
			db.insert(Tables.LINE, null, values);
		}
		db.setTransactionSuccessful();        //设置事务处理成功，不设置会自动回滚不提交
		db.endTransaction();        //处理完成
	}

	private void saveStation(SQLiteDatabase db, List<Station> stations) {
		Log.d(TAG, " 开始加载station:"+stations.size());
		db.beginTransaction();        //手动设置开始事务
		//数据插入操作循环
		for(Station l:stations){
			ContentValues values=new ContentValues();
			values.put(StationColumns._ID, l.getId());
			values.put(StationColumns.NAME, l.getName());
			db.insert(Tables.STATION, null, values);
		}
		db.setTransactionSuccessful();        //设置事务处理成功，不设置会自动回滚不提交
		db.endTransaction();        //处理完成
	}

	private void saveBus(SQLiteDatabase db, List<Bus> buses) {
		Log.d(TAG, " 开始加载bus:"+buses.size());
		db.beginTransaction();        //手动设置开始事务
		//数据插入操作循环
		for(Bus l:buses){
			ContentValues values=new ContentValues();
			values.put(BusColumns._ID, l.getId());
			values.put(BusColumns.CARD, l.getCard());
			values.put(BusColumns.COMPANY, l.getCompany());
			values.put(BusColumns.END_TIME, l.getEndTime());
			values.put(BusColumns.NAME, l.getName());
			values.put(BusColumns.PRICE, l.getPrice());
			values.put(BusColumns.START_TIME, l.getStartTime());
			db.insert(Tables.BUS, null, values);
		}
		db.setTransactionSuccessful();        //设置事务处理成功，不设置会自动回滚不提交
		db.endTransaction();        //处理完成
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
