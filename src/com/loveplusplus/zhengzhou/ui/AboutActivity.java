package com.loveplusplus.zhengzhou.ui;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.loveplusplus.zhengzhou.R;

public class AboutActivity extends BaseActivity {

	private String versionName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setUpView();
		// Intent intent = new Intent(Intent.ACTION_VIEW);
		// intent.setData(Uri.parse("market://details?id=com.example.android"));
		// startActivity(intent);
	}

	private void setUpView() {
		TextView version = (TextView) findViewById(R.id.version);
		try {
			versionName = getPackageManager().getPackageInfo(getPackageName(),
					0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		version.setText(versionName);
	}


}
