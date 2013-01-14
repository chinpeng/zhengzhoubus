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

package com.loveplusplus.zhengzhou.ui;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.provider.BusContract.Bus;

public class SearchableBusLineActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = "SearchableBusLineActivity";
	private TextView mTextView;
	private SearchView searchView;
	private String query;
	private SimpleCursorAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mTextView = (TextView) findViewById(R.id.text);

		adapter = new SimpleCursorAdapter(this, R.layout.bus_search_suggest,
				null, new String[] { Bus.LINE_NAME },
				new int[] { R.id.bus_name }, 0);

		setListAdapter(adapter);

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {

		Log.d(TAG, intent.toString());
		Log.d(TAG, intent.getData().toString());
		Log.d(TAG, intent.getDataString());

		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			Intent wordIntent = new Intent(this, StationsActivity.class);
			wordIntent.setData(intent.getData());
			startActivity(wordIntent);
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			Loader<Cursor> loader = getLoaderManager().getLoader(0);
			if (null == loader) {
				getLoaderManager().initLoader(0, intent.getExtras(), this);
			} else {
				getLoaderManager().restartLoader(0, intent.getExtras(), this);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_search, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setQuery(query, false);
		// searchView.setFocusable(false);
		searchView.setIconified(false);
		// searchView.requestFocusFromTouch();
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
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		query = null == args ? "" : args.getString(SearchManager.QUERY);
		return new CursorLoader(this, Bus.CONTENT_URI, null, Bus.LINE_NAME
				+ " like ?", new String[] { query + "%" }, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

		int count = data.getCount();
		String countString = getResources().getString(R.string.search_results,
				new Object[] { query, count });
		mTextView.setText(countString);
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent wordIntent = new Intent(getApplicationContext(),
				StationsActivity.class);
		Uri data = Uri.withAppendedPath(Bus.CONTENT_URI, String.valueOf(id));
		wordIntent.setData(data);
		startActivity(wordIntent);
	}
}
