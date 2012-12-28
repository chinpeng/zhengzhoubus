package com.loveplusplus.zhengzhou.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loveplusplus.zhengzhou.R;

public class BusLineAdapter extends BaseAdapter {

	List<String> data;
    protected LayoutInflater inflater;

	
	public BusLineAdapter(List<String> data,Fragment fragment) {
		this.data=data;
		this.inflater=fragment.getActivity().getLayoutInflater();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public String getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view=inflater.inflate(R.layout.busline_list_item, null);
		TextView sta=(TextView)view.findViewById(R.id.sta);
		sta.setText(getItem(position));
		return view;
	}

}
