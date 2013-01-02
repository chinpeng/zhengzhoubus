package com.loveplusplus.zhengzhou.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.loveplusplus.zhengzhou.R;

public class SettingsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		 // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
	}
	
	
	
	public static class SettingsFragment extends PreferenceFragment {

		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			addPreferencesFromResource(R.xml.settings);
		}
	}
}
