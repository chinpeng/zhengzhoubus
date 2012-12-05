package com.loveplusplus.zhengzhou.bean;

import java.util.SortedMap;

public class Line {

	private Bus bus;
	/**
	 * 上行
	 */
	private SortedMap<Integer,Station> upStations;
	/**
	 * 下行
	 */
	private SortedMap<Integer,Station> downStations;	
	
	public Bus getBus() {
		return bus;
	}
	public void setBus(Bus bus) {
		this.bus = bus;
	}
	public SortedMap<Integer, Station> getUpStations() {
		return upStations;
	}
	public void setUpStations(SortedMap<Integer, Station> upStations) {
		this.upStations = upStations;
	}
	public SortedMap<Integer, Station> getDownStations() {
		return downStations;
	}
	public void setDownStations(SortedMap<Integer, Station> downStations) {
		this.downStations = downStations;
	}
	@Override
	public String toString() {
		return "Line [bus=" + bus + ",\n upStations=\n" + upStations
				+ ", \n downStations=\n" + downStations + "]";
	}
	
	
	
	
}
