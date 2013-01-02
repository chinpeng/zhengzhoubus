package com.loveplusplus.zhengzhou.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class BusContract {

	interface BusColumns {
		String NAME = "bus_name";
		String ALIAS="bus_alias";//改名
		String FROM="bus_from";//起点
		String TO="bus_to";//终点
		String START_TIME = "bus_start_time";
		String END_TIME = "bus_end_time";
		String PRICE = "bus_price";
		String CARD = "bus_card";
		String COMPANY = "bus_company";
		String DEFINITION = "bus_definition";

	}

	interface LineColumns {
		String BUS_ID = "line_bus_id";
		String STATION_ID = "line_station_id";
		String DIRECT = "line_direct";
		String SNO = "line_sno";

	}

	interface StationColumns {
		String NAME = "station_name";
		String LATITUDE="station_latitude";
		String LONGTITUDE="station_longtitude";
	}

	interface FavoriteColumns {
		String BUS_NAME = "favorite_bus_name";
		String DIRECT = "favorite_direct";
		String SNO = "favorite_sno";
		String STATION_NAME = "favorite_station_name";
	}

	public static final String CONTENT_AUTHORITY =  "com.loveplusplus.zhengzhou.provider.BusProvider";

	public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
			+ CONTENT_AUTHORITY);

	public static final String PATH_BUS = "bus_info";
	public static final String PATH_LINE = "line";
	public static final String PATH_STATION = "station";
	public static final String PATH_FAVORITE = "favorite";

	public static class Bus implements BusColumns,
			BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_BUS).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.zzbus.bus";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.zzbus.bus";

		public static Uri buildBusUri(String categoryId) {
			return CONTENT_URI.buildUpon().appendPath(categoryId).build();
		}
	}

	public static class Line implements LineColumns,
			BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_LINE).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.zzbus.line";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.zzbus.line";

		public static Uri buildLineUri(String specialityId) {
			return CONTENT_URI.buildUpon().appendPath(specialityId).build();
		}
	}

	public static class Station implements StationColumns,
			BaseColumns {

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_STATION).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.zzbus.station";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.zzbus.station";

		public static Uri buildStationUri(String tableId) {
			return CONTENT_URI.buildUpon().appendPath(tableId).build();
		}
	}
	public static class Favorite implements FavoriteColumns,
	BaseColumns {
		
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_FAVORITE).build();
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.zzbus.favorite";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.zzbus.favorite";
		
		public static Uri buildFavoriteUri(String tableId) {
			return CONTENT_URI.buildUpon().appendPath(tableId).build();
		}
	}

}
