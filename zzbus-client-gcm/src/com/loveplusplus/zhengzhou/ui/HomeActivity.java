package com.loveplusplus.zhengzhou.ui;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.google.android.gcm.GCMRegistrar;
import com.loveplusplus.zhengzhou.BuildConfig;
import com.loveplusplus.zhengzhou.Config;
import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.provider.BusContract.Favorite;
import com.loveplusplus.zhengzhou.util.LogUtils;
import com.loveplusplus.zhengzhou.util.ServerUtilities;
import com.loveplusplus.zhengzhou.util.UIUtils;

public class HomeActivity extends BaseActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = LogUtils.makeLogTag(HomeActivity.class);

	private ShareActionProvider mShareActionProvider;
	private SimpleCursorAdapter mAdapter;
	private AsyncTask<Void, Void, Void> mGCMRegisterTask;

	private ListView mListView;

	private TextView mEmptyView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(false);
		mAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, null,
				new String[] { Favorite.BUS_NAME, Favorite.STATION_NAME },
				new int[] { android.R.id.text1, android.R.id.text2, }, 0);

		getSupportLoaderManager().initLoader(0, null, this);

		getContentResolver().registerContentObserver(Favorite.CONTENT_URI,
				true, new ContentObserver(new Handler()) {
					@Override
					public void onChange(boolean selfChange) {

						Loader<Cursor> loader1 = getSupportLoaderManager()
								.getLoader(0);
						if (loader1 != null) {
							loader1.forceLoad();
						}
					}
				});

		
		
		
		setupView();
		
		
		try {
			registerGCMClient();
		} catch (Exception e) {
			Log.d(TAG, "gcm...");
		}
	}

	private void setupView() {

		mListView = (ListView) findViewById(R.id.list);
		mEmptyView = (TextView) findViewById(R.id.empty);
		mListView.setEmptyView(mEmptyView);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Cursor cursor = (Cursor) mAdapter.getItem(position);
				String waitStation = cursor.getString(cursor
						.getColumnIndex(Favorite.STATION_NAME));
				String direct = cursor.getString(cursor
						.getColumnIndex(Favorite.DIRECT));
				String sno = cursor.getString(cursor
						.getColumnIndex(Favorite.SNO));
				String lineName = cursor.getString(cursor
						.getColumnIndex(Favorite.BUS_NAME));

				LogUtils.LOGD(TAG, waitStation + " " + direct + " " + sno + " "
						+ lineName);

				Intent intent = new Intent(HomeActivity.this, GpsWaitingActivity.class);

				intent.putExtra("lineName", lineName);
				intent.putExtra("ud", direct);
				intent.putExtra("sno", sno);
				intent.putExtra("hczd", waitStation);
				startActivity(intent);
			}
		});

		registerForContextMenu(mListView);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.delete, menu);
	}
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.menu_delete:
	        	deleteSelectedItem(info.id);
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	protected void deleteSelectedItem(long id) {

			getContentResolver().delete(
					Favorite.buildFavoriteUri(String.valueOf(id)),
					Favorite._ID + "=?", new String[] { String.valueOf(id) });

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
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getSupportMenuInflater();
		// 设置搜索
		inflater.inflate(R.menu.search, menu);
		setupSearchMenuItem(menu);

		// 设置分享
		inflater.inflate(R.menu.share, menu);
		// mShareActionProvider = (ShareActionProvider) menu.findItem(
		// R.id.menu_share).getActionProvider();
		//
		// mShareActionProvider
		// .setShareHistoryFileName("custom_share_history.xml");
		//
		// if (mShareActionProvider != null) {
		// mShareActionProvider.setShareIntent(getDefaultShareIntent());
		// }

		inflater.inflate(R.menu.setting, menu);
		return true;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupSearchMenuItem(Menu menu) {
		MenuItem searchItem = menu.findItem(R.id.menu_search);
		if (searchItem != null && UIUtils.hasHoneycomb()) {
			SearchView searchView = (SearchView) searchItem.getActionView();
			if (searchView != null) {
				SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
				searchView.setSearchableInfo(searchManager
						.getSearchableInfo(getComponentName()));
			}
		}
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
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
			// case R.id.menu_settings:
			// startActivity(new Intent(this, SettingsActivity.class));
			// return true;
		case R.id.menu_search:
			if (!UIUtils.hasHoneycomb()) {
				startSearch(null, false, Bundle.EMPTY, false);
				return true;
			}
			break;
		}
		return super.onOptionsItemSelected(item);
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

}
