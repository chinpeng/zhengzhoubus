package com.loveplusplus.zhengzhou.ui;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

import com.loveplusplus.zhengzhou.R;

public class AboutActivity extends BaseActivity {

	private String versionName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		setUpView();

	}

	private void setUpView() {
		TextView version = (TextView) findViewById(R.id.version);
		try {
			versionName = getPackageManager().getPackageInfo(getPackageName(),
					0).versionName;
		} catch (NameNotFoundException e) {
		}
		version.setText(versionName);

	}

}
