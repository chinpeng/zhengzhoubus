package com.loveplusplus.zhengzhou.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.provider.BusContract.Bus;
import com.loveplusplus.zhengzhou.provider.BusContract.Favorite;

public class StationsActivity extends FragmentActivity {

	public static final String TAG = "StationsActivity";

	static SectionsPagerAdapter mSectionsPagerAdapter;

	static ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stations);

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_stations, menu);
		return true;
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			ListFragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putString("direct", String.valueOf(position));
			fragment.setArguments(args);
			return fragment;
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
				return null;
			}

		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends ListFragment implements
			LoaderCallbacks<Cursor> {

		SimpleCursorAdapter mAdapter;

		public DummySectionFragment() {

		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {

			Cursor cursor = (Cursor) mAdapter.getItem(position);

			String waitStation = cursor.getString(cursor
					.getColumnIndex(Bus.STATION_NAME));
			String direct = cursor.getString(cursor
					.getColumnIndex(Bus.IS_UP_DOWN));
			String sno = cursor.getString(cursor.getColumnIndex(Bus.LABEL_NO));
			String lineName = cursor.getString(cursor
					.getColumnIndex(Bus.LINE_NAME));

			Log.d(TAG, waitStation + " " + direct + " " + sno + " " + lineName);

			Intent intent = new Intent(getActivity(), GpsWaitingActivity.class);

			intent.putExtra("lineName", lineName);
			intent.putExtra("ud", direct);
			intent.putExtra("sno", sno);
			intent.putExtra("hczd", waitStation);
			startActivity(intent);

			ContentValues values = new ContentValues();
			values.put(Favorite.BUS_NAME, lineName);
			values.put(Favorite.DIRECT, direct);
			values.put(Favorite.SNO, sno);
			values.put(Favorite.STATION_NAME, waitStation);

			getActivity().getContentResolver().insert(Favorite.CONTENT_URI,
					values);
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			mAdapter = new SimpleCursorAdapter(activity,
					R.layout.busline_list_item, null,
					new String[] { Bus.STATION_NAME }, new int[] { R.id.sta },
					0);
			setListAdapter(mAdapter);

			Loader<Cursor> loader = getLoaderManager().getLoader(0);

			if (null == loader) {
				getLoaderManager().initLoader(0, getArguments(), this);
			} else {
				getLoaderManager().restartLoader(0, getArguments(), this);
			}
		}

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
			FragmentActivity activity = getActivity();
			Intent intent = activity.getIntent();
			return new CursorLoader(activity, intent.getData(), null, null,
					new String[] { arg1.getString("direct") }, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			mAdapter.swapCursor(data);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> data) {
			mAdapter.swapCursor(null);
		}

	}

}
