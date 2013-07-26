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
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.provider.BusContract.Bus;
import com.loveplusplus.zhengzhou.util.ReflectionUtils;
import com.loveplusplus.zhengzhou.util.UIUtils;

public class SearchActivity extends BaseActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private TextView mTextView;
	private String query;
	private SimpleCursorAdapter adapter;
	private ListView mListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mTextView = (TextView) findViewById(R.id.text);

		adapter = new SimpleCursorAdapter(this, R.layout.bus_search_suggest,
				null, new String[] { Bus.LINE_NAME, "to_station" }, new int[] {
						R.id.bus_name, R.id.bus_definition }, 0);

		mListView = (ListView) findViewById(android.R.id.list);
		mListView.setAdapter(adapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Cursor cursor = (Cursor) adapter.getItem(position);
				String lineName = cursor.getString(cursor
						.getColumnIndex(Bus.LINE_NAME));

				Intent wordIntent = new Intent(getApplicationContext(),
						StationsActivity.class);
				Uri data = Uri.withAppendedPath(Bus.CONTENT_URI, lineName);
				wordIntent.setData(data);
				startActivity(wordIntent);
			}
		});

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		onNewIntent(getIntent());
	}

	@Override
	public void onNewIntent(Intent intent) {
		setIntent(intent);
		query = intent.getStringExtra(SearchManager.QUERY);

		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			Intent stationsIntent = new Intent(this, StationsActivity.class);
			stationsIntent.setData(intent.getData());
			startActivity(stationsIntent);
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			Loader<Cursor> loader = getLoaderManager().getLoader(0);
			if (null == loader) {
				getLoaderManager().initLoader(0, intent.getExtras(),
						this);
			} else {
				getLoaderManager().restartLoader(0, intent.getExtras(),
						this);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.search, menu);
		setupSearchMenuItem(menu);
		return true;
	}

	private void setupSearchMenuItem(Menu menu) {
		final MenuItem searchItem = menu.findItem(R.id.menu_search);
		if (searchItem != null && UIUtils.hasHoneycomb()) {
			SearchView searchView = (SearchView) searchItem.getActionView();
			if (searchView != null) {
				SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
				searchView.setSearchableInfo(searchManager
						.getSearchableInfo(getComponentName()));
				searchView
						.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
							@Override
							public boolean onQueryTextSubmit(String s) {
								ReflectionUtils.tryInvoke(searchItem,
										"collapseActionView");
								return false;
							}

							@Override
							public boolean onQueryTextChange(String s) {
								return false;
							}
						});
				searchView
						.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
							@Override
							public boolean onSuggestionSelect(int i) {
								return false;
							}

							@Override
							public boolean onSuggestionClick(int i) {
								ReflectionUtils.tryInvoke(searchItem,
										"collapseActionView");
								return false;
							}
						});
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
		query = null == args ? "" : args.getString(SearchManager.QUERY);
		return new CursorLoader(this, Bus.CONTENT_URI, null, Bus.LINE_NAME
				+ " like ?", new String[] { query + "%" }, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

		int count = data.getCount();
		String countString = getResources().getString(R.string.search_results,
				query, count);
		mTextView.setText(countString);
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

}
