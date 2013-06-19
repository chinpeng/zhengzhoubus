package com.loveplusplus.zhengzhou.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class BusContract {

	interface BusColumns {
		String LABEL_NO = "label_no";
		String IS_UP_DOWN = "is_up_down";
		String LINE_NAME = "line_name";
		String STATION_NAME = "station_name";
		String LONGITUDE = "longitude";
		String LATITUDE = "latitude";
		String CARFARE = "carfare";
		String FIRST_TIME = "first_time";
		String DEPT_NAME = "dept_name";
		String YN_USE_IC_A="yn_use_ic_a";
		String YN_USE_IC_B="yn_use_ic_b";
		String YN_USE_IC_C="yn_use_ic_c";
		String YN_USE_IC_D="yn_use_ic_d";
		String ALIAS="alias";
		
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

	public static final String PATH_BUS = "bus_info";
	public static final String PATH_FAVORITE = "favorite";

	public static class Bus implements BusColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_BUS).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.zzbus.bus";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.zzbus.bus";

		public static Uri buildBusUri(String categoryId) {
			return CONTENT_URI.buildUpon().appendPath(categoryId).build();
		}
	}

	public static class Favorite implements FavoriteColumns, BaseColumns {

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_FAVORITE).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.zzbus.favorite";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.zzbus.favorite";

		public static Uri buildFavoriteUri(String tableId) {
			return CONTENT_URI.buildUpon().appendPath(tableId).build();
		}
	}

}
