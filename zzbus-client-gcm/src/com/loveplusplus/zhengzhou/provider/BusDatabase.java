package com.loveplusplus.zhengzhou.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.loveplusplus.zhengzhou.provider.BusContract.Bus;
import com.loveplusplus.zhengzhou.provider.BusContract.BusColumns;
import com.loveplusplus.zhengzhou.provider.BusContract.FavoriteColumns;
import com.loveplusplus.zhengzhou.util.AssetsUtil;

public class BusDatabase extends SQLiteOpenHelper {

	private static final String TAG = "BusDatabase";
	private static final String DATABASE_NAME = "bus.db";
	private static final int DATABASE_VERSION = 15;

	interface Tables {
		String BUS = "bus";
		String FAVORITE = "favorite";
	}

	private Context context;

	public BusDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Tables.BUS + "(" + BaseColumns._ID
				+ "  INTEGER PRIMARY KEY AUTOINCREMENT," + Bus.CARFARE
				+ " TEXT," + Bus.DEPT_NAME + " TEXT," + Bus.FIRST_TIME
				+ " TEXT," + Bus.IS_UP_DOWN + " TEXT," + Bus.LABEL_NO
				+ " TEXT," + Bus.LATITUDE + " TEXT," + Bus.LINE_NAME + " TEXT,"
				+ Bus.LONGITUDE + " TEXT," + Bus.STATION_NAME + " TEXT,"
				+ Bus.YN_USE_IC_A + " TEXT," + Bus.YN_USE_IC_B + " TEXT,"
				+ Bus.YN_USE_IC_C + " TEXT," + Bus.YN_USE_IC_D + " TEXT)");

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
					Log.d(TAG, "加载数据");
				} catch (IOException e) {
					Log.e(TAG, " 加载数据异常" + e.getMessage());
				} catch (JSONException e) {
					Log.e(TAG, " 加载数据异常" + e.getMessage());
				}
			}
		}).start();

	}

	private void saveBus(SQLiteDatabase db) throws IOException, JSONException {

		// 获取所有的公交线路名称
		List<String> busNameList = AssetsUtil.loadBusList(context);

		List<ContentValues> list =null;
		for (String name : busNameList) {

			list= new ArrayList<ContentValues>();
			// 根据公交线路名称，获取该线路的json数据，解析json,并插入数据库
			String json = AssetsUtil.loadJson(name, context);
			JSONArray array = new JSONArray(json);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = (JSONObject) array.get(i);
				ContentValues values = new ContentValues();
				values.put(BusColumns.CARFARE, obj.getString("carfare"));
				values.put(BusColumns.DEPT_NAME, obj.getString("dept_name"));
				values.put(BusColumns.FIRST_TIME, obj.getString("first_time"));
				values.put(BusColumns.IS_UP_DOWN, obj.getString("is_up_down"));
				values.put(BusColumns.LABEL_NO, obj.getString("label_no"));
				values.put(BusColumns.LATITUDE, obj.getString("lat"));
				values.put(BusColumns.LINE_NAME, obj.getString("line_name"));
				values.put(BusColumns.LONGITUDE, obj.getString("lng"));
				values.put(BusColumns.STATION_NAME,
						obj.getString("station_name"));
				values.put(BusColumns.YN_USE_IC_A, obj.getString("yn_use_ic_a"));
				values.put(BusColumns.YN_USE_IC_B, obj.getString("yn_use_ic_b"));
				values.put(BusColumns.YN_USE_IC_C, obj.getString("yn_use_ic_c"));
				values.put(BusColumns.YN_USE_IC_D, obj.getString("yn_use_ic_d"));

				list.add(values);
			}

			insertDb(db, list, Tables.BUS);
		}
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
		db.execSQL("DROP TABLE IF EXISTS " + Tables.BUS);
		onCreate(db);
	}

}
