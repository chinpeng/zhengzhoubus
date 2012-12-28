package com.loveplusplus.zhengzhou.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class BusContract {

	interface BusColumns {
		String NAME = "name";
		String START_TIME = "start_time";
		String END_TIME = "end_time";
		String PRICE = "price";
		String CARD = "card";
		String COMPANY = "company";
		String DEFINITION = "definition";

	}

	interface LineColumns {
		String BUS_ID = "bus_id";
		String STATION_ID = "station_id";
		String DIRECT = "direct";
		String SNO = "son";

	}

	interface StationColumns {
		String NAME = "name";
	}

	interface FavoriteColumns {
		String BUS_NAME = "bus_name";
		String DIRECT = "direct";
		String SNO = "sno";
		String STATION_NAME = "station_name";
	}

	public static final String CONTENT_AUTHORITY =  "com.loveplusplus.zhengzhou.provider.BusProvider";

	public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
			+ CONTENT_AUTHORITY);

	public static final String PATH_BUS = "bus";
	public static final String PATH_LINE = "line";
	public static final String PATH_STATION = "station";
	public static final String PATH_FAVORITE = "favorite";

	public static class Bus implements BusColumns,
			BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_BUS).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.zzbus.bus";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.zzbus.bus";

		public static Uri buildCategoryUri(String categoryId) {
			return CONTENT_URI.buildUpon().appendPath(categoryId).build();
		}
	}

	public static class Line implements LineColumns,
			BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_LINE).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.zzbus.line";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.zzbus.line";

		public static Uri buildSpecialityUri(String specialityId) {
			return CONTENT_URI.buildUpon().appendPath(specialityId).build();
		}
	}

	public static class Station implements StationColumns,
			BaseColumns {

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_STATION).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.zzbus.station";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.zzbus.station";

		public static Uri buildTableUri(String tableId) {
			return CONTENT_URI.buildUpon().appendPath(tableId).build();
		}
	}
	public static class Favorite implements FavoriteColumns,
	BaseColumns {
		
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_FAVORITE).build();
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.zzbus.favorite";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.zzbus.favorite";
		
		public static Uri buildTableUri(String tableId) {
			return CONTENT_URI.buildUpon().appendPath(tableId).build();
		}
	}

}
