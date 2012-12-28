/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loveplusplus.zhengzhou;

import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.ManagerFactoryParameters;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.loveplusplus.zhengzhou.bean.Line;
import com.loveplusplus.zhengzhou.bean.Station;
import com.loveplusplus.zhengzhou.service.BusDatabase.LineColumns;
import com.loveplusplus.zhengzhou.service.BusDatabase.StationColumns;

/**
 * Displays a word and its definition.
 */
public class BusLineActivity extends FragmentActivity {
	private static final String[] PROJECTION = new String[] {
			LineColumns.DIRECT, LineColumns.SNO, StationColumns._ID,
			StationColumns.NAME };
	private static final String TAG = "BusLineActivity";
	private Line line;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_busline);

		initData();

		buildViewPager();
		buildActionBarAndViewPagerTitles();

	}

	private void initData() {
		Uri uri = getIntent().getData();
		Log.d(TAG, "--------_--------" + uri.toString());
		Cursor data = managedQuery(uri, PROJECTION,null,null,null);

		if (data == null) {
			finish();
		} else {
			line = new Line();
			SortedMap<Integer, Station> up = new TreeMap<Integer, Station>();
			SortedMap<Integer, Station> down = new TreeMap<Integer, Station>();

			while (data.moveToNext()) {
				// LineColumns.DIRECT,LineColumns.SNO,StationColumns._ID,StationColumns.NAME
				int direct = data.getInt(0);
				int sno = data.getInt(1);
				int stationId = data.getInt(2);
				String stationName = data.getString(3);
				if (0 == direct) {
					up.put(sno, new Station(stationId, stationName));
				} else {
					down.put(sno, new Station(stationId, stationName));
				}

			}
			line.setUpStations(up);
			line.setDownStations(down);
			
		}
	}

	private void buildActionBarAndViewPagerTitles() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);

		MainTabListener tabListener = new MainTabListener();

		actionBar.addTab(actionBar.newTab().setText(getString(R.string.up))
				.setTabListener(tabListener));

		actionBar.addTab(actionBar.newTab().setText(getString(R.string.down))
				.setTabListener(tabListener));

	}

	private class MainTabListener implements ActionBar.TabListener {

		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			if (mViewPager.getCurrentItem() != tab.getPosition()) {
				mViewPager.setCurrentItem(tab.getPosition());
			}
		}

		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}
	}

	ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			getActionBar().setSelectedNavigationItem(position);
		}
	};

	private void buildViewPager() {
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		BusLinePagerAdapter adapter = new BusLinePagerAdapter(
				getSupportFragmentManager());
		mViewPager.setOffscreenPageLimit(5);
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(onPageChangeListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_search, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return false;
		}
	}

	private class BusLinePagerAdapter extends FragmentStatePagerAdapter {

		public BusLinePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i) {
			case 0:
				return new BusLineFragment(0, line);
			case 1:
				return new BusLineFragment(1, line);
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "上行";
			case 1:
				return "下行";
			default:
				return "";
			}
		}

	}

	

	
}
