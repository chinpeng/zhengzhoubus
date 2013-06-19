package com.loveplusplus.zhengzhou.util;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SqliteUtil {

	public static final String DATABASE_TABLE = "bus";
	private Context context;
	private File dir = null;
	private File file = null;
	private SQLiteDatabase sqliteDatabase;

	/**
	 * 获取线路所属公司 费用 公交卡信息
	 * @param paramString
	 * @return
	 * @throws SQLException
	 */
	public Cursor getLine(String paramString) throws SQLException {
		String str = "select dept_name,first_time,carfare,yn_use_ic_a,yn_use_ic_b,yn_use_ic_c,yn_use_ic_d from bus where line_name='"
				+ paramString + "' group by line_name";
		Cursor localCursor = this.sqliteDatabase.rawQuery(str, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}

	/**
	 * 搜索建议
	 * @param paramString
	 * @return
	 * @throws SQLException
	 */
	public Cursor getLineName(String paramString) throws SQLException {
		String str = "select distinct LINE_NAME  from bus where LINE_NAME like '%"
				+ paramString + "%' order by _id";
		Cursor localCursor = this.sqliteDatabase.rawQuery(str, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}

	public Cursor getLineNameExact(String paramString) throws SQLException {
		String str = "select distinct LINE_NAME  from bus where LINE_NAME = '"
				+ paramString + "'";
		Cursor localCursor = this.sqliteDatabase.rawQuery(str, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}

	public Cursor getLineNameTop5(String paramString) throws SQLException {
		String str = "select distinct LINE_NAME  from bus where LINE_NAME like '%"
				+ paramString + "%' order by _id  limit 5 ";
		Cursor localCursor = this.sqliteDatabase.rawQuery(str, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}

	public Cursor getStation(String paramString) throws SQLException {
		String str = "select distinct  station_name from bus where station_name like '%"
				+ paramString + "%'";
		Cursor localCursor = this.sqliteDatabase.rawQuery(str, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}

	/**
	 * 查询上行（下行）经过的站台
	 * @param paramString
	 * @param paramInt
	 * @return
	 * @throws SQLException
	 */
	public Cursor getStation(String paramString, int paramInt)
			throws SQLException {
		String str = "SELECT Station_Name,lng,lat from bus where line_name = '"
				+ paramString + "' and is_up_down = " + paramInt
				+ "  order by LABEL_NO";
		Cursor localCursor = this.sqliteDatabase.rawQuery(str, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}

	public Cursor getStation(String paramString1, String paramString2,
			String paramString3, String paramString4) throws SQLException {
		String str = "select STATION_NAME,LNG,LAT from bus where LNG>='"
				+ paramString1 + "' and LNG <='" + paramString2
				+ "' and LAT >='" + paramString3 + "' and LAT<='"
				+ paramString4 + "' ";
		Cursor localCursor = this.sqliteDatabase.rawQuery(str, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}

	public Cursor getStationExact(String paramString) throws SQLException {
		String str = "select distinct  station_name from bus where station_name = '"
				+ paramString + "'";
		Cursor localCursor = this.sqliteDatabase.rawQuery(str, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}

	public Cursor getStationMore(String paramString) throws SQLException {
		String str = "select line_name,dept_name,first_time,carfare,yn_use_ic_a,yn_use_ic_b,yn_use_ic_c,yn_use_ic_d from bus where station_name='"
				+ paramString + "'group by line_name order by _id ";
		Cursor localCursor = this.sqliteDatabase.rawQuery(str, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}

	public Cursor getStationSingle(String paramString, int paramInt)
			throws SQLException {
		String str = "SELECT STATION_NAME,LABEL_NO from bus where line_name = '"
				+ paramString
				+ "' and is_up_down = "
				+ paramInt
				+ "  order by LABEL_NO";
		Cursor localCursor = this.sqliteDatabase.rawQuery(str, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}

	public Cursor getUpDown(String paramString) throws SQLException {
		String str = "SELECT is_up_down ,Station_Name from bus where line_name = '"
				+ paramString + "'  group by is_up_down order by is_up_down ";
		Cursor localCursor = this.sqliteDatabase.rawQuery(str, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}

	public Cursor getXY(String paramString, int paramInt1, int paramInt2)
			throws SQLException {
		String str = "select LNG,LAT  from bus where LINE_NAME='" + paramString
				+ "' and IS_UP_DOWN=" + paramInt1 + " and LABEL_NO="
				+ paramInt2;
		Cursor localCursor = this.sqliteDatabase.rawQuery(str, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}

	public void open() {
		this.dir = new File("data/data/" + this.context.getPackageName()
				+ "/databases");
		this.file = new File(this.dir, "bus.db3");
		this.sqliteDatabase = SQLiteDatabase.openOrCreateDatabase(this.file,
				null);
	}
}
