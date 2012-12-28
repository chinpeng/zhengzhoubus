package com.loveplusplus.zhengzhou.ui;

import android.os.Bundle;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.R.id;
import com.loveplusplus.zhengzhou.R.layout;

public class StationMapActivity extends MapActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_map);
		 MapView mapView = (MapView) findViewById(R.id.map_View);
		    mapView.setBuiltInZoomControls(true);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
