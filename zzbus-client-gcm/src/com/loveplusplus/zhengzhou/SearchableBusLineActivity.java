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
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.loveplusplus.zhengzhou.service.BusDatabase.BusColumns;
import com.loveplusplus.zhengzhou.service.BusProvider;

/**
 * The main activity for the dictionary. Displays search results triggered by
 * the search dialog and handles actions from search suggestions.
 */
public class SearchableBusLineActivity extends Activity {

	private TextView mTextView;
	private ListView mListView;
	private SearchView searchView;
	private String query;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		mTextView = (TextView) findViewById(R.id.text);
		mListView = (ListView) findViewById(R.id.list);
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {

		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			// handles a click on a search suggestion; launches activity to show
			// word
			Intent wordIntent = new Intent(this, BusLineActivity.class);
			wordIntent.setData(intent.getData());
			startActivity(wordIntent);
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// handles a search query
			 query = intent.getStringExtra(SearchManager.QUERY);
			
			showResults(query);
		}
	}

	/**
	 * Searches the dictionary and displays results for the given query.
	 * 
	 * @param query
	 *            The search query
	 */
	private void showResults(String query) {

		Cursor cursor = managedQuery(BusProvider.QUERY_CONTENT_URI, null,
				null, new String[] { query }, null);

		if (cursor == null) {
			// There are no results
			mTextView.setText(getString(R.string.no_results,
					new Object[] { query }));
		} else {
			// Display the number of results
			int count = cursor.getCount();
			String countString = getResources().getQuantityString(
					R.plurals.search_results, count,
					new Object[] { count, query });
			mTextView.setText(countString);

			// Specify the columns we want to display in the result
			String[] from = new String[] { BusColumns.NAME,
					BusColumns.DEFINITION };

			// Specify the corresponding layout elements where we want the
			// columns to go
			int[] to = new int[] { R.id.bus_name, R.id.bus_definition };

			// Create a simple cursor adapter for the definitions and apply them
			// to the ListView
			SimpleCursorAdapter words = new SimpleCursorAdapter(this,
					R.layout.bus_search_suggest, cursor, from, to);
			mListView.setAdapter(words);

			// Define the on-click listener for the list items
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// Build the Intent used to open WordActivity with a
					// specific word Uri
					Intent wordIntent = new Intent(getApplicationContext(),
							BusLineActivity.class);
					Uri data = Uri.withAppendedPath(
							BusProvider.GET_BUS_URI, String.valueOf(id));
					wordIntent.setData(data);
					startActivity(wordIntent);
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_search, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setQuery(query,false);
		//   searchView.setFocusable(false);
		    searchView.setIconified(false);
		//    searchView.requestFocusFromTouch();
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
}
