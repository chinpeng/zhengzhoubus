package com.loveplusplus.zhengzhou.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loveplusplus.zhengzhou.adapter.BusLineAdapter;
import com.loveplusplus.zhengzhou.provider.BusContract.Line;
import com.loveplusplus.zhengzhou.provider.BusContract.Station;

public class BusLineFragment extends ListFragment {

	private static final String TAG = "BusLineFragment";
	private int direct;
	//private Line line;
	private List<String> stations;

	public BusLineFragment() {

	}

	public BusLineFragment(int direct, Line line) {
	//	this.line = line;
		this.direct=direct;
		switch (direct) {
//		case 0:
//			stations = getUpBusLine();
//			break;
//		case 1:
//			stations = getDownBusLine();
//			break;
		}
	}

	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// We have a menu item to show in action bar.
		setHasOptionsMenu(true);
		// Create an empty adapter we will use to display the loaded data.
		BusLineAdapter mAdapter = new BusLineAdapter(stations, this);
		setListAdapter(mAdapter);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// GpsUtil.getGps(line.getLineName(), ud, sta.get(position).getSno());
//        String lineName=line.getBus().getName();
//        String ud=String.valueOf(direct);
//        String sno=String.valueOf(position);
//        String hczd=stations.get(position);
//    Log.d(TAG, ""+lineName+" ==="+sno+"  "+ud+""+hczd);    
//		Intent intent = new Intent(getActivity(), GpsWaitingActivity.class);
//		intent.putExtra("lineName", lineName);
//		intent.putExtra("ud", ud);
//		intent.putExtra("sno", sno);
//		intent.putExtra("hczd", hczd);
//		startActivity(intent);
	}

}
