package com.loveplusplus.zhengzhou.ui;

import static com.loveplusplus.zhengzhou.util.LogUtils.LOGD;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;

import com.google.analytics.tracking.android.EasyTracker;
import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.fragment.DownStationListFragment;
import com.loveplusplus.zhengzhou.fragment.UpListFragment;

public class StationsActivity extends BaseActivity implements ActionBar.TabListener,
		ViewPager.OnPageChangeListener {

	public static final String TAG = "StationsActivity";
	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stations);
		// ActionBar actionBar = getSupportActionBar();
		// actionBar.setDisplayShowTitleEnabled(false);
		// actionBar.setDisplayShowHomeEnabled(false);

		//FragmentManager fm = getSupportFragmentManager();
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(new StationPageAdapter(
				getSupportFragmentManager()));
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setPageMarginDrawable(R.drawable.grey_border_inset_lr);
		mViewPager.setPageMargin(getResources().getDimensionPixelSize(
				R.dimen.page_margin_width));
		
		 final ActionBar actionBar = getSupportActionBar();
		 actionBar.setDisplayShowHomeEnabled(true);
		 actionBar.setDisplayHomeAsUpEnabled(true);
         actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
         actionBar.addTab(actionBar.newTab()
                 .setText("上行")
                 .setTabListener(this));
         actionBar.addTab(actionBar.newTab()
                 .setText("下行")
                 .setTabListener(this));
         
        // setHasTabs();
	}

	private class StationPageAdapter extends FragmentPagerAdapter {
		public StationPageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return new UpListFragment();
			case 1:
				return new DownStationListFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onPageScrolled(int i, float v, int i1) {
	}

	@Override
	public void onPageSelected(int position) {
		getSupportActionBar().setSelectedNavigationItem(position);
		String title = "";
		// int titleId = -1;
		switch (position) {
		case 0:
			// titleId = R.string.title_my_schedule;
			title = "上行";
			break;
		case 1:
			title = "下行";
			// titleId = R.string.title_explore;
			break;
		}

		// String title = getString(titleId);
		EasyTracker.getTracker().sendView(title);
		LOGD("Tracker", title);

	}

	@Override
	public void onPageScrollStateChanged(int i) {
	}
}
