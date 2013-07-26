package com.loveplusplus.zhengzhou.ui;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.loveplusplus.zhengzhou.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public abstract class BaseActivity extends Activity{

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
