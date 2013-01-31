package com.loveplusplus.zhengzhou.ui;

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

			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
