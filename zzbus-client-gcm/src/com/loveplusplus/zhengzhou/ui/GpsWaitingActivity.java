package com.loveplusplus.zhengzhou.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.R.id;
import com.loveplusplus.zhengzhou.R.layout;
import com.loveplusplus.zhengzhou.R.menu;
import com.loveplusplus.zhengzhou.io.TaskResultReceiver;
import com.loveplusplus.zhengzhou.io.TaskService;

public class GpsWaitingActivity extends Activity  implements
TaskResultReceiver.Receiver {

	private ProgressDialog progressDialog;
	private TaskResultReceiver taskResultReceiver;
	private TextView lineName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps_waiting);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		lineName = (TextView)findViewById(R.id.line_name);
		
		
		taskResultReceiver = new TaskResultReceiver(new Handler());
		taskResultReceiver.setReceiver(this);
		
		
		refresh();
	}


	private void refresh() {
		Intent intent = new Intent(TaskService.EXTRA_STATUS_RECEIVER,null, GpsWaitingActivity.this, TaskService.class);
		intent.putExtra(TaskService.EXTRA_STATUS_RECEIVER,taskResultReceiver);
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
			// List results = resultData.getParcelableList("results");
			// do something interesting
			// hide progress
			progressDialog.dismiss();
			lineName.setText(resultData.getString("response"));
			break;
		case TaskService.STATUS_ERROR:
			// handle the error;
			progressDialog.dismiss();
			Toast.makeText(this,resultData.getString(Intent.EXTRA_TEXT), Toast.LENGTH_LONG).show();
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_refresh, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.menu_refresh:
			refresh();
			return true;
		default:
			return false;
		}
	}
	
}
