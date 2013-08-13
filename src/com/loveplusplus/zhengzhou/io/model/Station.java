package com.loveplusplus.zhengzhou.io.model;

import com.google.gson.annotations.SerializedName;

public class Station {

	@SerializedName("direct")
	public long direct;
	@SerializedName("sno")
	public long sno;
	@SerializedName("station_name")
	public String stationName;
	@SerializedName("line_name")
	public String lineName;
	@SerializedName("gps_lat")
	public String lat;
	@SerializedName("gps_lng")
	public String lng;

}
