package com.loveplusplus.zhengzhou.ui;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.TextView;

import com.loveplusplus.zhengzhou.R;

public class AboutActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		TextView version = (TextView) findViewById(R.id.version);
		try {
			String versionName = getPackageManager().getPackageInfo(
					getPackageName(), 0).versionName;
			version.setText(versionName);
		} catch (NameNotFoundException e) {
		}

	}

}
