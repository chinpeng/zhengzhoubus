package com.loveplusplus.zhengzhou.ui;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
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
import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.provider.BusContract.Favorite;
import com.loveplusplus.zhengzhou.util.UIUtils;

public class HomeActivity extends BaseActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter mAdapter;
	private ListView mListView;
	private TextView mEmptyView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(false);
		
		mAdapter = new SimpleCursorAdapter(this,
				R.layout.activity_home_item, null,
				new String[] { Favorite.BUS_NAME, Favorite.STATION_NAME },
				new int[] { R.id.bus_name, R.id.station_name, }, 0);

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



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getSupportMenuInflater();
		// 设置搜索
		inflater.inflate(R.menu.search, menu);
		setupSearchMenuItem(menu);

		// 设置分享
		inflater.inflate(R.menu.share, menu);
		MenuItem menuItem = menu.findItem(R.id.menu_share);
		ShareActionProvider mShareActionProvider =  (ShareActionProvider) menuItem.getActionProvider();  //line 387

	    Intent shareIntent = new Intent(Intent.ACTION_SEND);
	    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	    shareIntent.setType("text/plain");

	    shareIntent.putExtra(Intent.EXTRA_TEXT,
				getResources().getString(R.string.share_content));

	    mShareActionProvider.setShareIntent(shareIntent);
	    
		//inflater.inflate(R.menu.setting, menu);
		inflater.inflate(R.menu.about, menu);
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


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
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
