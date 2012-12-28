package com.loveplusplus.zhengzhou.ui;

import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.R.id;
import com.loveplusplus.zhengzhou.R.layout;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends BaseActivity {

	private String versionName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setUpView();

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
