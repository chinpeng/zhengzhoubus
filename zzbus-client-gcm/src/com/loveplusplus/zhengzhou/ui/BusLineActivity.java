package com.loveplusplus.zhengzhou.ui;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.provider.BusContract.Line;
import com.loveplusplus.zhengzhou.provider.BusContract.Station;

/**
 * Displays a word and its definition.
 */
public class BusLineActivity extends Activity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	public static SimpleCursorAdapter mAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		Tab tab = actionBar.newTab().setText("上行")
				.setTabListener(new TabListener(this, "up"));
		actionBar.addTab(tab);

		tab = actionBar.newTab().setText("下行")
				.setTabListener(new TabListener(this, "down"));
		actionBar.addTab(tab);
		mAdapter = new SimpleCursorAdapter(this, R.layout.busline_list_item,
				null, new String[] { Station.NAME }, new int[] { R.id.sta }, 0);
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

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(this, getIntent().getData(), new String[] {
				Line.DIRECT, Line.SNO, Station._ID, Station.NAME }, "direct=?",
				new String[] { arg1.getString("direct") }, null);

	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
		mAdapter.swapCursor(data);
		// mAdapter.notifyDataSetChanged();

		ListFragment up = (ListFragment) getFragmentManager()
				.findFragmentByTag("up");

		data.moveToFirst();
		String from = data.getString((data.getColumnIndex(Station.NAME)));
		data.moveToLast();
		String to = data.getString((data.getColumnIndex(Station.NAME)));

		if (null != up) {
			up.setListAdapter(mAdapter);
			getActionBar().getTabAt(0).setText("上行(" + from + "开往" + to + ")");
		}
		ListFragment down = (ListFragment) getFragmentManager()
				.findFragmentByTag("down");
		if (null != down) {
			down.setListAdapter(mAdapter);
			getActionBar().getTabAt(1).setText("上行(" + from + "开往" + to + ")");
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}

	// SimpleCursorAdapter mAdapter=new SimpleCursorAdapter(getActivity(),
	// R.layout.busline_list_item, getArguments().g, new String[]{Station.NAME},
	// new int[]{R.id.sta}, 0);
	// setListAdapter(mAdapter);

	// GpsUtil.getGps(line.getLineName(), ud, sta.get(position).getSno());
	// String lineName=line.getBus().getName();
	// String ud=String.valueOf(direct);
	// String sno=String.valueOf(position);
	// String hczd=stations.get(position);
	// Log.d(TAG, ""+lineName+" ==="+sno+"  "+ud+""+hczd);
	// Intent intent = new Intent(getActivity(), GpsWaitingActivity.class);
	// intent.putExtra("lineName", lineName);
	// intent.putExtra("ud", ud);
	// intent.putExtra("sno", sno);
	// intent.putExtra("hczd", hczd);
	// startActivity(intent);

	public static class TabListener implements ActionBar.TabListener {
		private static final String TAG = "TabListener";
		private final BusLineActivity mActivity;
		private final String mTag;
		private ListFragment mFragment;

		public TabListener(Activity activity, String tag) {
			mActivity = (BusLineActivity) activity;
			mTag = tag;
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {

			Log.d(TAG, tab.toString());
			String direct = "0";
			if (mTag.equals("up")) {
				direct = "0";
			} else if (mTag.equals("down")) {
				direct = "1";
			}
			Bundle args = new Bundle();
			args.putString("direct", direct);

			if (mFragment == null) {
				mFragment = (ListFragment) ListFragment.instantiate(mActivity,
						ListFragment.class.getName());
				// mFragment.setListAdapter(mAdapter);
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				ft.attach(mFragment);
			}

			Loader<Object> loader = mActivity.getLoaderManager().getLoader(0);
			if (null == loader) {
				mActivity.getLoaderManager().initLoader(0, args, mActivity);
			} else {
				mActivity.getLoaderManager().restartLoader(0, args, mActivity);
			}

		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				// Detach the fragment, because another one is being attached
				ft.detach(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}

}
