package com.loveplusplus.zhengzhou.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.io.TaskResultReceiver;
import com.loveplusplus.zhengzhou.io.TaskService;

public class GpsWaitingActivity extends BaseActivity implements
		TaskResultReceiver.Receiver {

	private ProgressDialog progressDialog;
	private TaskResultReceiver taskResultReceiver;
	private TextView lineName;
	private TextView lineDirect;
	private TextView lineWaitStation;
	private TextView lineWaitInfo1;
	private TextView lineWaitInfo2;
	private TextView lineWaitInfo3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gps_waiting);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		lineName = (TextView) findViewById(R.id.line_name);
		lineDirect = (TextView) findViewById(R.id.line_direct);
		lineWaitStation = (TextView) findViewById(R.id.line_wait_station);
		lineWaitInfo1 = (TextView) findViewById(R.id.line_wait_info_1);
		lineWaitInfo2 = (TextView) findViewById(R.id.line_wait_info_2);
		lineWaitInfo3 = (TextView) findViewById(R.id.line_wait_info_3);
		taskResultReceiver = new TaskResultReceiver(new Handler());
		taskResultReceiver.setReceiver(this);
		refresh();
	}

	private void refresh() {
		Intent intent = new Intent(TaskService.EXTRA_STATUS_RECEIVER, null,
				GpsWaitingActivity.this, TaskService.class);
		intent.putExtra(TaskService.EXTRA_STATUS_RECEIVER, taskResultReceiver);
		intent.putExtra("lineName", getIntent().getStringExtra("lineName"));
		intent.putExtra("ud", getIntent().getStringExtra("ud"));
		intent.putExtra("sno", getIntent().getStringExtra("sno"));
		intent.putExtra("hczd", getIntent().getStringExtra("hczd"));
		startService(intent);
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		switch (resultCode) {
		case TaskService.STATUS_RUNNING:
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在查询，请稍候……");
			progressDialog.show();
			break;
		case TaskService.STATUS_FINISHED:
			progressDialog.dismiss();
			String[] result = resultData.getStringArray("response");
			// String[] result = response.split("\n");
			if (null != result) {
				lineName.setText(result[0]+"公交车");
				lineDirect.setText("开往"+result[1]+"方向");
				lineWaitStation.setText("候车于"+result[2]);
				lineWaitInfo1.setText(result[3]);
				lineWaitInfo2.setText(result[4]);
				lineWaitInfo3.setText(result[5]);
			}
			break;
		case TaskService.STATUS_ERROR:
			progressDialog.dismiss();
			Toast.makeText(this, resultData.getString(Intent.EXTRA_TEXT),
					Toast.LENGTH_LONG).show();
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
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

}
