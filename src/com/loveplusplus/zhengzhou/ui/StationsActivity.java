package com.loveplusplus.zhengzhou.ui;

import com.loveplusplus.zhengzhou.fragment.StationListFragment;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class StationsActivity extends Activity {

	public static final String TAG = "StationsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);

		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				// show the given tab
				Bundle b=new Bundle();
				b.putString("direct", 1+"");
	            Fragment mFragment=new StationListFragment();
	            mFragment.setArguments(b);
	            
				ft.add(android.R.id.content, mFragment, "xx");
			}

			public void onTabUnselected(ActionBar.Tab tab,
					FragmentTransaction ft) {
				// hide the given tab
				//tab.g
				//ft.detach(fragment)
			}

			public void onTabReselected(ActionBar.Tab tab,
					FragmentTransaction ft) {
				// probably ignore this event
			}
		};

		actionBar.addTab(actionBar.newTab().setText("上行").setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("下行").setTabListener(tabListener));


		// args.putString("direct", String.valueOf(position));
	}

}
