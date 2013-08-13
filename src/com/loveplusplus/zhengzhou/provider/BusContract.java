package com.loveplusplus.zhengzhou.provider;

import com.loveplusplus.zhengzhou.provider.BusDatabase.Tables;

import android.net.Uri;
import android.provider.BaseColumns;

public class BusContract {

	interface BusLineColumns {
		String LINE_NAME = "line_name";
		String CARFARE = "carfare";
		String FIRST_TIME = "first_time";
		String DEPT_NAME = "dept_name";
		String START_STATION="start_station";
		String END_STATION="end_station";
		
	}
	
	interface BusLineStationColumns {
		String DIRECT = "direct";
		String SNO = "sno";
		String STATION_NAME = "station_name";
		String LINE_NAME = "line_name";
		String GPS_LAT="gps_lat";
		String GPS_LNG="gps_lng";
		
	}

	interface FavoriteColumns {
		String BUS_NAME = "favorite_bus_name";
		String DIRECT = "favorite_direct";
		String SNO = "favorite_sno";
		String STATION_NAME = "favorite_station_name";
	}

	public static final String CONTENT_AUTHORITY = "com.loveplusplus.zhengzhou.provider.BusProvider";

	public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
			+ CONTENT_AUTHORITY);


	public static class BusLine implements BusLineColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(Tables.BUS_LINE).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.zzbus.bus_line";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.zzbus.bus_line";

		public static Uri buildUri(String id) {
			return CONTENT_URI.buildUpon().appendPath(id).build();
		}
	}
	public static class BusLineStation implements BusLineStationColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(Tables.BUS_LINE_STATION).build();
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.zzbus.bus_line_station";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.zzbus.bus_station";
		
		public static Uri buildUri(String id) {
			return CONTENT_URI.buildUpon().appendPath(id).build();
		}
	}

	public static class Favorite implements FavoriteColumns, BaseColumns {

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(Tables.FAVORITE).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.zzbus.favorite";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.zzbus.favorite";

		public static Uri buildUri(String id) {
			return CONTENT_URI.buildUpon().appendPath(id).build();
		}
	}

}
