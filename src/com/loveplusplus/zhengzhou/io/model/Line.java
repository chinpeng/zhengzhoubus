package com.loveplusplus.zhengzhou.io.model;

import com.google.gson.annotations.SerializedName;

public class Line {

	@SerializedName("line_name")
	public String lineName;
	@SerializedName("carfare")
	public String carfare;
	@SerializedName("first_time")
	public String firstTime;
	@SerializedName("dept_name")
	public String deptName;
	@SerializedName("start_station")
	public String startStation;
	@SerializedName("end_station")
	public String endStation;

}
