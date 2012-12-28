package com.loveplusplus.zhengzhou.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

public abstract class BaseActivity extends Activity {

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			 Intent intent = new Intent(this, HomeActivity.class);
             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}
}
