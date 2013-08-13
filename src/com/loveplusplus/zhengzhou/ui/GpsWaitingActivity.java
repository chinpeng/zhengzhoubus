package com.loveplusplus.zhengzhou.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.util.BusUtil;

public class GpsWaitingActivity extends BaseActivity {

	private TextView lineName;
	private TextView lineDirect;
	private TextView lineWaitStation;
	private TextView lineWaitInfo1;
	private TextView lineWaitInfo2;
	private TextView lineWaitInfo3;

	private String lineName2;
	private String ud;
	private String sno;
	private String hczd;

	private MyAsyncTask task=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gps_waiting);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		lineName = (TextView) findViewById(R.id.line_name);
		lineDirect = (TextView) findViewById(R.id.line_direct);
		lineWaitStation = (TextView) findViewById(R.id.line_wait_station);
		lineWaitInfo1 = (TextView) findViewById(R.id.line_wait_info_1);
		lineWaitInfo2 = (TextView) findViewById(R.id.line_wait_info_2);
		lineWaitInfo3 = (TextView) findViewById(R.id.line_wait_info_3);

		lineName2 = getIntent().getStringExtra("lineName");
		ud = getIntent().getStringExtra("ud");
		sno = getIntent().getStringExtra("sno");
		hczd = getIntent().getStringExtra("hczd");

		refresh();

	}

	private void refresh() {
		task=new MyAsyncTask();
		task.execute();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (task != null) {
			task.cancel(true);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.refresh, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			refresh();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class MyAsyncTask extends AsyncTask<Void, Void, String[]> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(GpsWaitingActivity.this,
					"获取GPS信息", "正在查询，请稍候……");
		}

		@Override
		protected String[] doInBackground(Void... params) {

			// 调用http
			// String back = ServerUtilities.getGps(lineName,ud,sno,hczd);
			String[] back = BusUtil.getGps(lineName2, ud, sno);

			return back;
		}

		@Override
		protected void onPostExecute(String[] result) {
			task=null;
			
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
			if (result != null && result.length == 6) {
				lineName.setText(result[0] + "公交车");
				lineDirect.setText("开往" + result[1] + "方向");
				lineWaitStation.setText("候车于" + result[2]);
				lineWaitInfo1.setText(result[3]);
				lineWaitInfo2.setText(result[4]);
				lineWaitInfo3.setText(result[5]);
			}else{
				Toast.makeText(GpsWaitingActivity.this, "获取GPS信息失败，请稍候再试", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
