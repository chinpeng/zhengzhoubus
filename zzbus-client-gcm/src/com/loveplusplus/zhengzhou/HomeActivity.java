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
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.ShareActionProvider;
import android.widget.SimpleCursorAdapter;

import com.loveplusplus.zhengzhou.service.BusDatabase;
import com.loveplusplus.zhengzhou.service.BusProvider;

/**
 * Displays a word and its definition.
 */
public class HomeActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String[] PROJECTION = new String[] {
			BusDatabase.FavoriteColumns._ID,
			BusDatabase.FavoriteColumns.BUS_NAME,
			BusDatabase.FavoriteColumns.DIRECT,
			BusDatabase.FavoriteColumns.SNO,
			BusDatabase.FavoriteColumns.STATION_NAME };
	private ShareActionProvider mShareActionProvider;
	SimpleCursorAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(false);
		String[] fromColumns = { BusDatabase.FavoriteColumns.BUS_NAME,
				BusDatabase.FavoriteColumns.STATION_NAME };
		int[] toViews = { android.R.id.text1, android.R.id.text2 };
		mAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, null, fromColumns,
				toViews, 0);
		setListAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);

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
		return new CursorLoader(this, BusProvider.FAVORITE_CONTENT_URI,
				PROJECTION, null, null, null);
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
		// Do somthing when a list item is clicked
	}
}
