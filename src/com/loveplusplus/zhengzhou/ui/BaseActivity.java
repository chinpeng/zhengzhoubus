package com.loveplusplus.zhengzhou.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.loveplusplus.zhengzhou.R;

public abstract class BaseActivity extends ActionBarActivity{

	protected ActionBar actionBar;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		EasyTracker.getInstance().setContext(this);  
        
        // Get the GoogleAnalytics singleton. Note that the SDK uses  
        // the application context to avoid leaking the current context.  
        GoogleAnalytics mGaInstance = GoogleAnalytics.getInstance(getApplicationContext());  
        mGaInstance.setDebug(true);  
        // Use the GoogleAnalytics singleton to get a Tracker.  
        Tracker mGaTracker = mGaInstance.getTracker(getString(R.string.ga_trackingId)); // Placeholder tracking ID.  
         // The rest of your onCreate() code. 
        mGaTracker.sendView(this.getClass().getSimpleName());  
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		EasyTracker.getInstance().activityStart(this); // Add this method.
	}
	
	@Override
	  public void onStop() {
	    super.onStop();

	    EasyTracker.getInstance().activityStop(this); // Add this method.
	  }
}
