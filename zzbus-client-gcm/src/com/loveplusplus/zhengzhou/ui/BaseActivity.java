package com.loveplusplus.zhengzhou.ui;

import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

public abstract class BaseActivity extends FragmentActivity {
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (this instanceof HomeActivity) {
				return false;
			}

			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
