package com.loveplusplus.zhengzhou.ui;

import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public abstract class BaseActivity extends SherlockFragmentActivity {

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (this instanceof HomeActivity) {
				return false;
			}

			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
