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

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.loveplusplus.zhengzhou.bean.Line;
import com.loveplusplus.zhengzhou.service.BusDatabase;

/**
 * Displays a word and its definition.
 */
public class BusLineActivity extends FragmentActivity {
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
		BusLinePagerAdapter adapter = new BusLinePagerAdapter(getSupportFragmentManager());
		mViewPager.setOffscreenPageLimit(5);
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(onPageChangeListener);
	}

	private void initData() {
		String query = getQueryStr();
		// 获取到查询字符串

		//LineDao dao = new LineDao(this);
	//	Bus b = dao.getBus(query.replace("路", ""));
	//	line = dao.findStationsByBus(b);
	}

	private String getQueryStr() {
		Uri uri = getIntent().getData();
		Cursor cursor = managedQuery(uri, null, null, null, null);

		if (cursor == null) {
			finish();
			return null;
		} else {
			cursor.moveToFirst();
			int wIndex = cursor.getColumnIndexOrThrow(BusDatabase.BusColumns.NAME);
			String query = cursor.getString(wIndex);
			return query;
		}
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

	

	
	private class BusLinePagerAdapter extends FragmentStatePagerAdapter{

		public BusLinePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i) {
			case 0:
				return new BusLineFragment(0,line);
			case 1:
				return new BusLineFragment(1,line);
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
