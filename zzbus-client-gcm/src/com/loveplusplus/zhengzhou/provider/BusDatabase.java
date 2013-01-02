package com.loveplusplus.zhengzhou.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.provider.BusContract.BusColumns;
import com.loveplusplus.zhengzhou.provider.BusContract.FavoriteColumns;
import com.loveplusplus.zhengzhou.provider.BusContract.LineColumns;
import com.loveplusplus.zhengzhou.provider.BusContract.StationColumns;

public class BusDatabase extends SQLiteOpenHelper {

	private static final String TAG = "BusDatabase";
	private static final String DATABASE_NAME = "bus.db";
	private static final int DATABASE_VERSION = 13;

	interface Tables {
		String BUS = "bus";
		String STATION = "station";
		String LINE = "line";
		String FAVORITE = "favorite";
	}

	private Context context;

	public BusDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	public Cursor getBusMatches(String query, String[] columns) {
		String selection = BusColumns.NAME + " like ?";
		String[] selectionArgs = new String[] { query + "%" };

		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(Tables.BUS);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(BaseColumns._ID, BaseColumns._ID);
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, BaseColumns._ID
				+ " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		map.put(SearchManager.SUGGEST_COLUMN_TEXT_1, BusColumns.NAME + " AS "
				+ SearchManager.SUGGEST_COLUMN_TEXT_1);
		map.put(SearchManager.SUGGEST_COLUMN_TEXT_2, BusColumns.DEFINITION
				+ " AS " + SearchManager.SUGGEST_COLUMN_TEXT_2);
		builder.setProjectionMap(map);
		Cursor cursor = builder.query(getReadableDatabase(), columns,
				selection, selectionArgs, null, null, null);

		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		return cursor;

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Tables.BUS + "(" + BaseColumns._ID
				+ "  INTEGER PRIMARY KEY AUTOINCREMENT," + BusColumns.NAME
				+ "  TEXT NOT NULL," + BusColumns.START_TIME + "  TEXT,"
				+ BusColumns.END_TIME + "  TEXT," + BusColumns.PRICE
				+ "  TEXT," + BusColumns.CARD + "  TEXT," + BusColumns.ALIAS
				+ "  TEXT," + BusColumns.FROM + "  TEXT," + BusColumns.TO
				+ "  TEXT," + BusColumns.DEFINITION + "  TEXT,"
				+ BusColumns.COMPANY + "  TEXT)");

		db.execSQL("CREATE TABLE " + Tables.STATION + "(" + BaseColumns._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ StationColumns.LATITUDE + " TEXT,"
				+ StationColumns.LONGTITUDE + " TEXT," + StationColumns.NAME
				+ " TEXT NOT NULL )");

		db.execSQL("CREATE TABLE " + Tables.LINE + "(" + BaseColumns._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + LineColumns.BUS_ID
				+ "  INTEGER," + LineColumns.STATION_ID + "  INTEGER,"
				+ LineColumns.DIRECT + "  INTEGER," + LineColumns.SNO
				+ "  INTEGER," + "FOREIGN KEY (" + LineColumns.BUS_ID
				+ ") REFERENCES " + Tables.BUS + "(" + BaseColumns._ID + "),"
				+ "FOREIGN KEY (" + LineColumns.STATION_ID + ") REFERENCES "
				+ Tables.STATION + " (" + BaseColumns._ID + ")," + "UNIQUE ("
				+ LineColumns.BUS_ID + " , " + LineColumns.STATION_ID + " , "
				+ LineColumns.DIRECT + " , " + LineColumns.SNO
				+ ") ON CONFLICT REPLACE)");

		Log.d(TAG, "创建数据库 favorite");
		db.execSQL("CREATE  TABLE " + Tables.FAVORITE + "(" + BaseColumns._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ FavoriteColumns.STATION_NAME + " TEXT," + FavoriteColumns.SNO
				+ " INTEGER," + FavoriteColumns.DIRECT + " INTEGER,"
				+ FavoriteColumns.BUS_NAME + " TEXT)");

		loadBusData(db);

	}

	private void loadBusData(final SQLiteDatabase db) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Log.d(TAG, "加载数据");
					saveBus(db);
					saveStation(db);
					saveLine(db);
					Log.d(TAG, "加载数据");
				} catch (IOException e) {
					Log.e(TAG, " 加载数据异常" + e.getMessage());
				} catch (JSONException e) {
					Log.e(TAG, " 加载数据异常" + e.getMessage());
				}
			}
		}).start();

	}

	private void saveLine(SQLiteDatabase db) throws IOException, JSONException {
		List<ContentValues> list = new ArrayList<ContentValues>();

		//String json = ServerUtilities.post(Constants.LINE_URL, null);

		InputStream is = this.context.getResources().openRawResource(R.raw.line);
		byte[] data = new byte[is.available()];
		is.read(data);
		is.close();
		String json = new String(data);
		
		JSONArray array = new JSONArray(json);
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = (JSONObject) array.get(i);
			ContentValues values = new ContentValues();
			values.put(BaseColumns._ID, obj.getInt("id"));
			values.put(LineColumns.BUS_ID, obj.getInt("bus_id"));
			values.put(LineColumns.DIRECT, obj.getInt("direct"));
			values.put(LineColumns.SNO, obj.getInt("sno"));
			values.put(LineColumns.STATION_ID, obj.getInt("station_id"));
			list.add(values);
		}

		insertDb(db, list, Tables.LINE);
	}

	private void saveStation(SQLiteDatabase db) throws IOException,
			JSONException {
		List<ContentValues> list = new ArrayList<ContentValues>();

		//String json = ServerUtilities.post(Constants.STATION_URL, null);

		InputStream is = this.context.getResources().openRawResource(R.raw.station);
		byte[] data = new byte[is.available()];
		is.read(data);
		is.close();
		String json = new String(data);
		
		JSONArray array = new JSONArray(json);
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = (JSONObject) array.get(i);

			ContentValues values = new ContentValues();
			values.put(BaseColumns._ID, obj.getInt("id"));
			values.put(StationColumns.NAME, obj.getString("name"));
			list.add(values);
		}

		insertDb(db, list, Tables.STATION);
	}

	private void saveBus(SQLiteDatabase db) throws IOException, JSONException {
		List<ContentValues> list = new ArrayList<ContentValues>();

		// String json = ServerUtilities.post(Constants.BUS_URL, null);
		
		InputStream is = this.context.getResources().openRawResource(R.raw.bus);
		byte[] data = new byte[is.available()];
		is.read(data);
		is.close();
		String json = new String(data);
		
		JSONArray array = new JSONArray(json);
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = (JSONObject) array.get(i);
			ContentValues values = new ContentValues();
			values.put(BaseColumns._ID, obj.getInt("id"));
			values.put(BusColumns.CARD, obj.getString("card"));
			values.put(BusColumns.COMPANY, obj.getString("company"));
			values.put(BusColumns.END_TIME, obj.getString("end_time"));
			values.put(BusColumns.NAME, obj.getString("name"));
			values.put(BusColumns.PRICE, obj.getString("price"));
			values.put(BusColumns.START_TIME, obj.getString("start_time"));
			values.put(BusColumns.DEFINITION, obj.getString("definition"));

			list.add(values);
		}

		insertDb(db, list, Tables.BUS);
	}

	/**
	 * 批量保存数据到数据库
	 * 
	 * @param db
	 * @param list
	 * @param tableName
	 */
	private void insertDb(SQLiteDatabase db, List<ContentValues> list,
			String tableName) {
		db.beginTransaction(); // 手动设置开始事务
		for (ContentValues v : list) {
			db.insert(tableName, null, v);
		}
		db.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
		db.endTransaction(); // 处理完成
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
