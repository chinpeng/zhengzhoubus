package com.loveplusplus.zhengzhou.provider;

import java.io.IOException;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.io.LineHandler;
import com.loveplusplus.zhengzhou.io.model.Line;
import com.loveplusplus.zhengzhou.io.model.Station;
import com.loveplusplus.zhengzhou.provider.BusContract.BusLine;
import com.loveplusplus.zhengzhou.provider.BusContract.BusLineStation;
import com.loveplusplus.zhengzhou.provider.BusContract.FavoriteColumns;
import com.loveplusplus.zhengzhou.util.LogUtils;

public class BusDatabase extends SQLiteOpenHelper {

	private static final String TAG = "BusDatabase";
	private static final String DATABASE_NAME = "bus.db";
	private static final int DATABASE_VERSION = 20;

	interface Tables {
		String FAVORITE = "favorite";
		String BUS_LINE = "bus_line";
		String BUS_LINE_STATION = "bus_line_station";
	}

	private Context context;

	public BusDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	String createTableLine = "CREATE TABLE " + Tables.BUS_LINE + "("
			+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ BusLine.CARFARE + " TEXT," + BusLine.DEPT_NAME + " TEXT,"
			+ BusLine.FIRST_TIME + " TEXT," + BusLine.END_STATION + " TEXT,"
			+ BusLine.START_STATION + " TEXT," + BusLine.LINE_NAME
			+ " TEXT NOT NULL UNIQUE)";

	String createTableStation = "CREATE TABLE " + Tables.BUS_LINE_STATION + "("
			+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ BusLineStation.DIRECT + " INTEGER," + BusLineStation.GPS_LAT
			+ " TEXT," + BusLineStation.GPS_LNG + " TEXT,"
			+ BusLineStation.LINE_NAME + " TEXT NOT NULL," + BusLineStation.SNO
			+ " INTEGER," + BusLineStation.STATION_NAME + " TEXT NOT NULL)";

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL(createTableLine);
		db.execSQL(createTableStation);

		db.execSQL("CREATE  TABLE " + Tables.FAVORITE + "(" + BaseColumns._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ FavoriteColumns.STATION_NAME + " TEXT," + FavoriteColumns.SNO
				+ " INTEGER," + FavoriteColumns.DIRECT + " INTEGER,"
				+ FavoriteColumns.BUS_NAME + " TEXT)");

		batchInsert(db);

	}

	private void batchInsert(final SQLiteDatabase db) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				LogUtils.LOGD(TAG, "开始批量插入数据");
				long startTime = SystemClock.currentThreadTimeMillis();

				StringBuilder sql1 = new StringBuilder();
				sql1.append("insert into ");
				sql1.append(Tables.BUS_LINE);
				sql1.append("(");
				sql1.append(BusLine.CARFARE);
				sql1.append(",");
				sql1.append(BusLine.DEPT_NAME);
				sql1.append(",");
				sql1.append(BusLine.END_STATION);
				sql1.append(",");
				sql1.append(BusLine.FIRST_TIME);
				sql1.append(",");
				sql1.append(BusLine.LINE_NAME);
				sql1.append(",");
				sql1.append(BusLine.START_STATION);
				sql1.append(")");
				sql1.append(" values(?,?,?,?,?,?)");

				StringBuilder sql2 = new StringBuilder();
				sql2.append("insert into ");
				sql2.append(Tables.BUS_LINE_STATION);
				sql2.append("(");
				sql2.append(BusLineStation.DIRECT);
				sql2.append(",");
				sql2.append(BusLineStation.LINE_NAME);
				sql2.append(",");
				sql2.append(BusLineStation.SNO);
				sql2.append(",");
				sql2.append(BusLineStation.STATION_NAME);
				sql2.append(")");
				sql2.append(" values(?,?,?,?)");

				try {
					save1(db, sql1);
					save2(db, sql2);
				} catch (IOException e) {
					e.printStackTrace();
				}

				long time = SystemClock.currentThreadTimeMillis() - startTime;
				LogUtils.LOGD(TAG, "执行时间：" + time);
				LogUtils.LOGD(TAG, "执行时间：" + time / 1000 + "秒");
			}

			private void save1(final SQLiteDatabase db, StringBuilder sql1)
					throws IOException {

				String json = LineHandler.parseResource(context, R.raw.lines);
				
				List<Line> busLines = new Gson().fromJson(json,
						new TypeToken<List<Line>>() {
						}.getType());
				
				SQLiteStatement stat = db.compileStatement(sql1
						.toString());
				db.beginTransaction();
				for (Line line : busLines) {
					stat.bindString(1, line.carfare==null?"":line.carfare);
					stat.bindString(2, line.deptName==null?"":line.deptName);
					stat.bindString(3, line.endStation==null?"":line.endStation);
					stat.bindString(4, line.firstTime==null?"":line.firstTime);
					stat.bindString(5, line.lineName);
					stat.bindString(6, line.startStation==null?"":line.startStation);
					stat.executeInsert();
				}
				db.setTransactionSuccessful();
				db.endTransaction();
			}

			private void save2(final SQLiteDatabase db, StringBuilder sql2)
					throws IOException {
				String json = LineHandler
						.parseResource(context, R.raw.stations);
				List<Station> busLines = new Gson().fromJson(json,
						new TypeToken<List<Station>>() {
						}.getType());
				SQLiteStatement stat = db.compileStatement(sql2
						.toString());
				db.beginTransaction();
				for (Station line : busLines) {
					stat.bindLong(1, line.direct);
					stat.bindString(2, line.lineName);
					stat.bindLong(3, line.sno);
					stat.bindString(4, line.stationName);
					stat.executeInsert();
				}
				db.setTransactionSuccessful();
				db.endTransaction();
			}
		}).start();

	}

	/*
	 * private void saveBus(SQLiteDatabase db) throws IOException, JSONException
	 * {
	 * 
	 * // 获取所有的公交线路名称 List<String> busNameList =
	 * AssetsUtil.loadBusList(context);
	 * 
	 * List<ContentValues> list = null; for (String name : busNameList) {
	 * 
	 * list = new ArrayList<ContentValues>(); //
	 * 根据公交线路名称，获取该线路的json数据，解析json,并插入数据库 String json =
	 * AssetsUtil.loadJson(name, context);
	 * 
	 * JSONArray array = new JSONArray(json); for (int i = 0; i <
	 * array.length(); i++) { JSONObject obj = (JSONObject) array.get(i);
	 * ContentValues values = new ContentValues();
	 * values.put(BusColumns.CARFARE, obj.getString("carfare"));
	 * values.put(BusColumns.DEPT_NAME, obj.getString("dept_name"));
	 * values.put(BusColumns.FIRST_TIME, obj.getString("first_time"));
	 * values.put(BusColumns.IS_UP_DOWN, obj.getString("is_up_down"));
	 * values.put(BusColumns.LABEL_NO, obj.getString("label_no"));
	 * values.put(BusColumns.LATITUDE, obj.getString("lat"));
	 * values.put(BusColumns.LINE_NAME, obj.getString("line_name"));
	 * values.put(BusColumns.LONGITUDE, obj.getString("lng"));
	 * values.put(BusColumns.STATION_NAME, obj.getString("station_name"));
	 * values.put(BusColumns.YN_USE_IC_A, obj.getString("yn_use_ic_a"));
	 * values.put(BusColumns.YN_USE_IC_B, obj.getString("yn_use_ic_b"));
	 * values.put(BusColumns.YN_USE_IC_C, obj.getString("yn_use_ic_c"));
	 * values.put(BusColumns.YN_USE_IC_D, obj.getString("yn_use_ic_d"));
	 * 
	 * list.add(values); }
	 * 
	 * insertDb(db, list, Tables.BUS); } }
	 */
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
		// db.execSQL("DROP TABLE IF EXISTS " + Tables.FAVORITE);
		// drop old table
		db.execSQL("DROP TABLE IF EXISTS bus");
		db.execSQL(createTableLine);
		db.execSQL(createTableStation);
		batchInsert(db);
	}

}
