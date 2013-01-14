package com.loveplusplus.zhengzhou.ui;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.ShareActionProvider;
import android.widget.SimpleCursorAdapter;

import com.google.android.gcm.GCMRegistrar;
import com.loveplusplus.zhengzhou.BuildConfig;
import com.loveplusplus.zhengzhou.Config;
import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.provider.BusContract.Favorite;
import com.loveplusplus.zhengzhou.util.LogUtils;
import com.loveplusplus.zhengzhou.util.ServerUtilities;

public class HomeActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = LogUtils.makeLogTag(HomeActivity.class);

	private ShareActionProvider mShareActionProvider;
	private SimpleCursorAdapter mAdapter;
	private AsyncTask<Void, Void, Void> mGCMRegisterTask;
	private ListView listView;
	protected ArrayList<Long> checkedIds = new ArrayList<Long>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(false);
		mAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_activated_2, null,
				new String[] { Favorite.BUS_NAME, Favorite.STATION_NAME },
				new int[] { android.R.id.text1, android.R.id.text2, }, 0);
		setListAdapter(mAdapter);

		getLoaderManager().initLoader(0, null, this);

		getContentResolver().registerContentObserver(Favorite.CONTENT_URI,
				true, new ContentObserver(new Handler()) {
					@Override
					public void onChange(boolean selfChange) {

						Loader<Cursor> loader1 = getLoaderManager()
								.getLoader(0);
						if (loader1 != null) {
							loader1.forceLoad();
						}
					}
				});

		registerGCMClient();

		registerChoiceMode();
	}

	private void registerChoiceMode() {

		listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				// Here you can do something when items are
				// selected/de-selected,
				// such as update the title in the CAB

				if (checked) {
					checkedIds.add(id);
				} else {
					Iterator<Long> iter = checkedIds.iterator();
					while (iter.hasNext()) {
						long stored = (Long) iter.next();
						if (stored == id) {
							iter.remove();
						}
					}
				}

				mode.setTitle("你选择了" + checkedIds.size() + "条目");
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				// Respond to clicks on the actions in the CAB
				switch (item.getItemId()) {
				case R.id.menu_delete:
					deleteSelectedItems();
					mode.finish(); // Action picked, so close the CAB
					return true;
				default:
					return false;
				}
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// Inflate the menu for the CAB
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.context, menu);
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// Here you can make any necessary updates to the activity when
				// the CAB is removed. By default, selected items are
				// deselected/unchecked.
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// Here you can perform updates to the CAB due to
				// an invalidate() request
				return false;
			}
		});

	}

	protected void deleteSelectedItems() {

		for (long id : checkedIds) {
			getContentResolver().delete(
					Favorite.buildFavoriteUri(String.valueOf(id)),
					Favorite._ID + "=?", new String[] { String.valueOf(id) });
		}

	}

	private void registerGCMClient() {
		GCMRegistrar.checkDevice(this);
		if (BuildConfig.DEBUG) {
			GCMRegistrar.checkManifest(this);
		}

		final String regId = GCMRegistrar.getRegistrationId(this);

		if (TextUtils.isEmpty(regId)) {
			// Automatically registers application on startup.
			GCMRegistrar.register(this, Config.GCM_SENDER_ID);

		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration
				LogUtils.LOGI(TAG, "Already registered on the GCM server");

			} else {
				// Try to register again, but not on the UI thread.
				// It's also necessary to cancel the task in onDestroy().
				mGCMRegisterTask = new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						boolean registered = ServerUtilities.register(
								HomeActivity.this, regId);
						if (!registered) {
							// At this point all attempts to register with the
							// app
							// server failed, so we need to unregister the
							// device
							// from GCM - the app will try to register again
							// when
							// it is restarted. Note that GCM will send an
							// unregistered callback upon completion, but
							// GCMIntentService.onUnregistered() will ignore it.
							GCMRegistrar.unregister(HomeActivity.this);
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mGCMRegisterTask = null;
					}
				};
				mGCMRegisterTask.execute(null, null, null);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mGCMRegisterTask != null) {
			mGCMRegisterTask.cancel(true);
		}

		try {
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			LogUtils.LOGW(TAG, "GCM unregistration error", e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_home, menu);

		// 设置分享
		mShareActionProvider = (ShareActionProvider) menu.findItem(
				R.id.menu_item_share).getActionProvider();

		mShareActionProvider
				.setShareHistoryFileName("custom_share_history.xml");

		if (mShareActionProvider != null) {
			mShareActionProvider.setShareIntent(getDefaultShareIntent());
		}
		// 设置搜索
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(
				R.id.menu_item_search).getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(true);
		return true;
	}

	private Intent getDefaultShareIntent() {
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_TEXT,
				getResources().getString(R.string.share_content));
		shareIntent.setType("text/plain");
		return shareIntent;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_item_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		case R.id.menu_item_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, Favorite.CONTENT_URI, null, null, null,
				null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		Cursor cursor = (Cursor) mAdapter.getItem(position);
		String waitStation = cursor.getString(cursor
				.getColumnIndex(Favorite.STATION_NAME));
		String direct = cursor
				.getString(cursor.getColumnIndex(Favorite.DIRECT));
		String sno = cursor.getString(cursor.getColumnIndex(Favorite.SNO));
		String lineName = cursor.getString(cursor
				.getColumnIndex(Favorite.BUS_NAME));

		LogUtils.LOGD(TAG, waitStation + " " + direct + " " + sno + " "
				+ lineName);

		Intent intent = new Intent(this, GpsWaitingActivity.class);

		intent.putExtra("lineName", lineName);
		intent.putExtra("ud", direct);
		intent.putExtra("sno", sno);
		intent.putExtra("hczd", waitStation);
		startActivity(intent);
	}
}
