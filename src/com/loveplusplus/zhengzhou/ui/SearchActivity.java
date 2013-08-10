package com.loveplusplus.zhengzhou.ui;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
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
			LoaderManager loaderManager = getSupportLoaderManager();
			Loader<Cursor> loader = loaderManager.getLoader(0);
			if (null == loader) {
				loaderManager.initLoader(0, intent.getExtras(), this);
			} else {
				loaderManager.restartLoader(0, intent.getExtras(), this);
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.search, menu);
		final MenuItem searchItem = menu.findItem(R.id.menu_search);
		if (searchItem != null && UIUtils.hasHoneycomb()) {
			SearchView searchView = (SearchView) searchItem
					.getActionView();

			SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);

			searchView.setSearchableInfo(searchManager
					.getSearchableInfo(getComponentName()));

			searchView.setQueryRefinementEnabled(true);

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
		return true;
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

		if (data != null) {
			int count = data.getCount();
			String countString = getResources().getString(
					R.string.search_results, query, count);
			mTextView.setText(countString);
			adapter.swapCursor(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

}
