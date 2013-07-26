package com.loveplusplus.zhengzhou.ui;

import android.app.Activity;
import android.view.MenuItem;

public class BaseActivity extends Activity{

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
