package com.loveplusplus.zhengzhou.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.loveplusplus.zhengzhou.provider.BusContract.BusColumns;
import com.loveplusplus.zhengzhou.provider.BusContract.FavoriteColumns;
import com.loveplusplus.zhengzhou.provider.BusContract.LineColumns;
import com.loveplusplus.zhengzhou.provider.BusContract.StationColumns;

public class BusDatabase extends SQLiteOpenHelper {

	private static final String TAG = "BusDatabase";
	private static final String DATABASE_NAME = "bus.db";
	private static final int DATABASE_VERSION = 6;

	private Context context;

	interface Tables {
		String BUS = "bus";
		String STATION = "station";
		String LINE = "line";
		String FAVORITE = "favorite";
	}

	public BusDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	//
	// /*
	// * SELECT bus.id,bus.name,line.direct,line.sno ,station.id,station.name
	// from
	// * line JOIN station ON(line.station_id=station.id) JOIN bus
	// * on(line.bus_id=bus.id) where line.bus_id=2
	// */
	// public Cursor getLine(String busId, String[] columns) {
	// String selection = "line.bus_id = ?";
	// String[] selectionArgs = new String[] { busId };
	//
	// SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
	// builder.setTables("line join station on(line.station_id=station._id) join bus on(line.bus_id=bus._id)");
	//
	// HashMap<String, String> map = new HashMap<String, String>();
	//
	// map.put(LineColumns.DIRECT, LineColumns.DIRECT);
	// map.put(LineColumns.SNO, LineColumns.SNO);
	// map.put(StationColumns._ID, "station._id AS " + StationColumns._ID);
	// map.put(StationColumns.NAME, "station.name AS " + StationColumns.NAME);
	// builder.setProjectionMap(map);
	//
	// Cursor cursor = builder.query(getReadableDatabase(), columns,
	// selection, selectionArgs, null, null, null);
	//
	// if (cursor == null) {
	// return null;
	// } else if (!cursor.moveToFirst()) {
	// cursor.close();
	// return null;
	// }
	// Log.d(TAG, "" + cursor.getCount());
	// Log.d(TAG, cursor.getString(1));
	// return cursor;
	// }
	//
	// /**
	// * Returns a Cursor over all words that match the given query
	// *
	// * @param query
	// * The string to search for
	// * @param columns
	// * The columns to include, if null then all are included
	// * @return Cursor over all words that match, or null if none found.
	// */
	// public Cursor getWordMatches(String query, String[] columns) {
	// String selection = BusColumns.NAME + " like ?";
	// String[] selectionArgs = new String[] { query + "%" };
	//
	// return query(selection, selectionArgs, columns);
	// }
	//
	// public Cursor getBusMatches(String query, String[] columns) {
	// String selection = BusColumns.NAME + " like ?";
	// String[] selectionArgs = new String[] { "%" + query + "%" };
	//
	// SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
	// builder.setTables(Tables.BUS);
	// HashMap<String, String> map = new HashMap<String, String>();
	// map.put(BusColumns._ID, BusColumns._ID);
	// map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "_id AS "
	// + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
	// map.put(SearchManager.SUGGEST_COLUMN_TEXT_1, "name AS "
	// + SearchManager.SUGGEST_COLUMN_TEXT_1);
	// map.put(SearchManager.SUGGEST_COLUMN_TEXT_2, "definition AS "
	// + SearchManager.SUGGEST_COLUMN_TEXT_2);
	//
	// builder.setProjectionMap(map);
	//
	// Cursor cursor = builder.query(getReadableDatabase(), columns,
	// selection, selectionArgs, null, null, null);
	//
	// if (cursor == null) {
	// return null;
	// } else if (!cursor.moveToFirst()) {
	// cursor.close();
	// return null;
	// }
	// Log.d(TAG, "----->" + cursor.getCount());
	// return cursor;
	//
	// }
	//
	// public Cursor getBusByName(String query, String[] columns) {
	// String selection = BusColumns.NAME + " like ?";
	// String[] selectionArgs = new String[] { query + "%" };
	//
	// SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
	// builder.setTables(Tables.BUS);
	//
	// HashMap<String, String> map = new HashMap<String, String>();
	//
	// map.put(BusColumns.NAME, BusColumns.NAME);
	// map.put(BusColumns.DEFINITION, BusColumns.DEFINITION);
	// map.put(BusColumns._ID, BusColumns._ID);
	//
	// builder.setProjectionMap(map);
	//
	// Cursor cursor = builder.query(getReadableDatabase(), columns,
	// selection, selectionArgs, null, null, null);
	//
	// if (cursor == null) {
	// return null;
	// } else if (!cursor.moveToFirst()) {
	// cursor.close();
	// return null;
	// }
	// return cursor;
	//
	// }
	//
	// private Cursor query(String selection, String[] selectionArgs,
	// String[] columns) {
	//
	// SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
	// builder.setTables(Tables.BUS);
	// HashMap<String, String> map = new HashMap<String, String>();
	// map.put(BusColumns.NAME, BusColumns.NAME);
	// map.put(BusColumns.DEFINITION, BusColumns.DEFINITION);
	// map.put(BusColumns._ID, BusColumns._ID);
	// map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "_id AS "
	// + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
	//
	// builder.setProjectionMap(map);
	//
	// Cursor cursor = builder.query(getReadableDatabase(), columns,
	// selection, selectionArgs, null, null, null);
	//
	// if (cursor == null) {
	// return null;
	// } else if (!cursor.moveToFirst()) {
	// cursor.close();
	// return null;
	// }
	// Log.d(TAG, "----->" + cursor.getCount());
	// return cursor;
	// }
	//
	// public Cursor getFavoriteBus(String[] columns) {
	// SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
	// builder.setTables(Tables.FAVORITE);
	// Cursor cursor = builder.query(getReadableDatabase(), columns, null,
	// null, null, null, null);
	// if (cursor == null) {
	// return null;
	// } else if (!cursor.moveToFirst()) {
	// cursor.close();
	// return null;
	// }
	// return cursor;
	// }
	//
	// public void saveFavoriteBus(String hczd, String sno, String ud, String
	// xl) {
	// SQLiteDatabase db = getWritableDatabase();
	// ContentValues initialValues = new ContentValues();
	// initialValues.put(FavoriteColumns.STATION_NAME, hczd);
	// initialValues.put(FavoriteColumns.SNO, sno);
	// initialValues.put(FavoriteColumns.DIRECT, ud);
	// initialValues.put(FavoriteColumns.BUS_NAME, xl);
	// db.insert(Tables.FAVORITE, null, initialValues);
	// db.close();
	// }

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Tables.BUS + "(" 
				+BaseColumns._ID+ "  INTEGER PRIMARY KEY AUTOINCREMENT,"
				+BusColumns.NAME+ "  TEXT NOT NULL,"
				+BusColumns.START_TIME + "  TEXT,"
				+BusColumns.END_TIME + "  TEXT,"
				+BusColumns.PRICE+ "  TEXT,"
				+BusColumns.CARD + "  TEXT,"
				+BusColumns.DEFINITION + "  TEXT,"
				+BusColumns.COMPANY+ "  TEXT)");

		db.execSQL("CREATE TABLE " + Tables.STATION + "(" 
				+ BaseColumns._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ StationColumns.NAME+ " TEXT NOT NULL )");

		db.execSQL("CREATE TABLE " + Tables.LINE + "(" 
				+ BaseColumns._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ LineColumns.BUS_ID+ "  INTEGER," 
				+ LineColumns.STATION_ID + "  INTEGER,"
				+ LineColumns.DIRECT + "  INTEGER," 
				+ LineColumns.SNO+ "  INTEGER," 
				+ "FOREIGN KEY (" + LineColumns.BUS_ID+ ") REFERENCES " + Tables.BUS + "(" + BaseColumns._ID + "),"
				+ "FOREIGN KEY (" + LineColumns.STATION_ID + ") REFERENCES "+ Tables.STATION + " (" + BaseColumns._ID + ")," 
				+ "UNIQUE ("+ LineColumns.BUS_ID + " , " + LineColumns.STATION_ID + " , "+ LineColumns.DIRECT + " , " + LineColumns.SNO+ ") ON CONFLICT REPLACE)");

		Log.d(TAG, "创建数据库 favorite");
		db.execSQL("CREATE  TABLE " + Tables.FAVORITE + "(" 
				+ BaseColumns._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ FavoriteColumns.STATION_NAME + " TEXT," 
				+ FavoriteColumns.SNO+ " INTEGER," 
				+ FavoriteColumns.DIRECT + " INTEGER,"
				+ FavoriteColumns.BUS_NAME + " TEXT)");

	//	loadBusData(db);

	}

//	private void loadBusData(SQLiteDatabase db) {
//		try {
//			Log.d(TAG, "加载数据");
//			AssetManager assetManager = context.getResources().getAssets();
//
//			InputStream is1 = assetManager.open("db/bus.xml");
//			List<Bus> buses = XmlUtil.loadBus(is1);
//			saveBus(db, buses);
//
//			InputStream is2 = assetManager.open("db/station.xml");
//			List<Station> stations = XmlUtil.loadStation(is2);
//			saveStation(db, stations);
//
//			InputStream is3 = assetManager.open("db/line.xml");
//			List<LineTemp> lines = XmlUtil.loadLine(is3);
//			saveLine(db, lines);
//			Log.d(TAG, "加载数据");
//		} catch (IOException e) {
//			Log.e(TAG, " 加载数据异常" + e.getMessage());
//		} catch (NumberFormatException e) {
//			Log.e(TAG, " 加载数据异常" + e.getMessage());
//		} catch (XmlPullParserException e) {
//			Log.e(TAG, "读取xml数据异常" + e.getMessage());
//		}
//	}
//
//	private void saveLine(SQLiteDatabase db, List<LineTemp> lines) {
//		Log.d(TAG, " 开始加载line:" + lines.size());
//		db.beginTransaction(); // 手动设置开始事务
//		// 数据插入操作循环
//		for (LineTemp l : lines) {
//			ContentValues values = new ContentValues();
//			values.put(BaseColumns._ID, l.getId());
//			values.put(LineColumns.BUS_ID, l.getBusId());
//			values.put(LineColumns.DIRECT, l.getDirect());
//			values.put(LineColumns.SNO, l.getSno());
//			values.put(LineColumns.STATION_ID, l.getStationId());
//			db.insert(Tables.LINE, null, values);
//		}
//		db.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
//		db.endTransaction(); // 处理完成
//	}
//
//	private void saveStation(SQLiteDatabase db, List<Station> stations) {
//		Log.d(TAG, " 开始加载station:" + stations.size());
//		db.beginTransaction(); // 手动设置开始事务
//		// 数据插入操作循环
//		for (Station l : stations) {
//			ContentValues values = new ContentValues();
//			values.put(BaseColumns._ID, l.getId());
//			values.put(StationColumns.NAME, l.getName());
//			db.insert(Tables.STATION, null, values);
//		}
//		db.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
//		db.endTransaction(); // 处理完成
//	}
//
//	private void saveBus(SQLiteDatabase db, List<Bus> buses) {
//		Log.d(TAG, " 开始加载bus:" + buses.size());
//		db.beginTransaction(); // 手动设置开始事务
//		// 数据插入操作循环
//		for (Bus l : buses) {
//			ContentValues values = new ContentValues();
//			values.put(BaseColumns._ID, l.getId());
//			values.put(BusColumns.CARD, l.getCard());
//			values.put(BusColumns.COMPANY, l.getCompany());
//			values.put(BusColumns.END_TIME, l.getEndTime());
//			values.put(BusColumns.NAME, l.getName());
//			values.put(BusColumns.PRICE, l.getPrice());
//			values.put(BusColumns.START_TIME, l.getStartTime());
//			db.insert(Tables.BUS, null, values);
//		}
//		db.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
//		db.endTransaction(); // 处理完成
//	}

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
