package com.loveplusplus.zhengzhou.ui;

import static com.loveplusplus.zhengzhou.util.LogUtils.LOGI;
import static com.loveplusplus.zhengzhou.util.LogUtils.LOGW;
import static com.loveplusplus.zhengzhou.util.LogUtils.makeLogTag;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.loveplusplus.zhengzhou.Config;
import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.gcm.ServerUtilities;
import com.loveplusplus.zhengzhou.provider.BusContract.Favorite;
import com.loveplusplus.zhengzhou.util.UIUtils;

public class HomeActivity extends BaseActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = makeLogTag(HomeActivity.class);

	private SimpleCursorAdapter mAdapter;
	private ListView mListView;
	private TextView mEmptyView;

	private AsyncTask<Void, Void, Void> mGCMRegisterTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		getSupportActionBar().setHomeButtonEnabled(false);

		mAdapter = new SimpleCursorAdapter(this, R.layout.activity_home_item,
				null,
				new String[] { Favorite.BUS_NAME, Favorite.STATION_NAME },
				new int[] { R.id.bus_name, R.id.station_name, }, 0);

		final LoaderManager loaderManager = getSupportLoaderManager();
		loaderManager.initLoader(0, null, this);

		getContentResolver().registerContentObserver(Favorite.CONTENT_URI,
				true, new ContentObserver(new Handler()) {
					@Override
					public void onChange(boolean selfChange) {

						Loader<Cursor> loader1 = loaderManager.getLoader(0);
						if (loader1 != null) {
							loader1.forceLoad();
						}
					}
				});
		setupView();

		//registerGCMClient();
	}

	private void registerGCMClient() {
		boolean gcm = true;
		try {
			GCMRegistrar.checkDevice(this);
		} catch (UnsupportedOperationException e) {
			gcm = false;
		}

		if (gcm) {
			GCMRegistrar.checkManifest(this);
			final String regId = GCMRegistrar.getRegistrationId(this);
			if (TextUtils.isEmpty(regId)) {
				GCMRegistrar.register(this, Config.GCM_SENDER_ID);
			} else {
				if (ServerUtilities.isRegisteredOnServer(this)) {
					LOGI(TAG, "Already registered on the C2DM server");
				} else {
					mGCMRegisterTask = new AsyncTask<Void, Void, Void>() {
						@Override
						protected Void doInBackground(Void... params) {
							boolean registered = ServerUtilities.register(
									HomeActivity.this, regId);
							if (!registered) {
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
		} else {
			LOGI(TAG, "no gcm");
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
			LOGW(TAG, "C2DM unregistration error", e);
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

				Intent intent = new Intent(HomeActivity.this,
						GpsWaitingActivity.class);

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
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.menu_delete:
			deleteSelectedItem(info.id);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	protected void deleteSelectedItem(long id) {

		getContentResolver().delete(Favorite.buildUri(String.valueOf(id)),
				Favorite._ID + "=?", new String[] { String.valueOf(id) });

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		// 设置搜索
		inflater.inflate(R.menu.search, menu);

		MenuItem searchItem = menu.findItem(R.id.menu_search);
		setUpSearchMenuItem(searchItem);

		// 设置分享
		inflater.inflate(R.menu.share, menu);
		MenuItem shareItem = menu.findItem(R.id.menu_share);
		setUpShareMenuItem(shareItem);

		inflater.inflate(R.menu.score, menu);

		// 关于
		inflater.inflate(R.menu.about, menu);
		return true;
	}

	private void setUpShareMenuItem(MenuItem shareItem) {
		ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat
				.getActionProvider(shareItem);

		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		// shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT,
				getResources().getString(R.string.share_content)
						+ getPackageName());

		mShareActionProvider.setShareIntent(shareIntent);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setUpSearchMenuItem(MenuItem searchItem) {

		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		if (searchView != null) {
			SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
			searchView.setQueryRefinementEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
			return false;
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		case R.id.menu_search:
			if (!UIUtils.hasHoneycomb()) {
				startSearch(null, false, Bundle.EMPTY, false);
				return true;
			}
			break;
		case R.id.menu_score:
			score();
			return true;
		}
		return super.onOptionsItemSelected(item);

	}

	private void score() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=" + getPackageName()));
		startActivity(intent);
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
