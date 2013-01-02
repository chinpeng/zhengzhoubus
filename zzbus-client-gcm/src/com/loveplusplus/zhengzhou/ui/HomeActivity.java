package com.loveplusplus.zhengzhou.ui;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.ShareActionProvider;
import android.widget.SimpleCursorAdapter;

import com.google.android.gcm.GCMRegistrar;
import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.provider.BusContract.Favorite;
import com.loveplusplus.zhengzhou.util.Constants;
import com.loveplusplus.zhengzhou.util.ServerUtilities;

public class HomeActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = "HomeActivity";
	private ShareActionProvider mShareActionProvider;
	SimpleCursorAdapter mAdapter;
	AsyncTask<Void, Void, Void> mRegisterTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(false);
		String[] fromColumns = { Favorite.BUS_NAME, Favorite.STATION_NAME };
		int[] toViews = {R.id.cursor_title,R.id.cursor_sub_title,};
		mAdapter = new SimpleCursorAdapter(this,
				R.layout.custom_cursor_item, null, fromColumns,
				toViews, 0);
		setListAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);

		getContentResolver().registerContentObserver(Favorite.CONTENT_URI,
				true, new ContentObserver(new Handler()) {
					@Override
					public void onChange(boolean selfChange) {

						Loader<Cursor> loader1 = getLoaderManager().getLoader(
								0);
						if (loader1 != null) {
							loader1.forceLoad();
						}
					}
				});
		
		startGCM();
	}

	private void startGCM() {
		checkNotNull(Constants.SERVER_URL, "SERVER_URL");
		checkNotNull(Constants.SENDER_ID, "SENDER_ID");
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);

		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, Constants.SENDER_ID);
		} else {
			if (GCMRegistrar.isRegisteredOnServer(this)) {
			} else {
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						boolean registered = ServerUtilities.register(context,
								regId);
						if (!registered) {
							GCMRegistrar.unregister(context);
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}
				};
				mRegisterTask.execute(null, null, null);
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		// unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getString(R.string.error_config,
					name));
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
		String lineName = cursor.getString(cursor.getColumnIndex(Favorite.BUS_NAME));

		Log.d(TAG, waitStation + " " + direct + " " + sno + " " + lineName);

		Intent intent = new Intent(this, GpsWaitingActivity.class);

		intent.putExtra("lineName", lineName);
		intent.putExtra("ud", direct);
		intent.putExtra("sno", sno);
		intent.putExtra("hczd", waitStation);
		startActivity(intent);
	}
}
