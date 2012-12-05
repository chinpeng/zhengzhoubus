package com.loveplusplus.zhengzhou;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class NotifyDetailActivity extends BaseActivity {

	private static final String TAG = "NotifyDetailActivity";
	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notify_detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mTextView = (TextView)findViewById(R.id.notify_content);
		
		handleIntent(getIntent());
	}

	private void handleIntent(Intent intent) {
		Log.d(TAG, intent.toString());
		if(null!=intent){
			String title=intent.getStringExtra("title");
			String content=intent.getStringExtra("content");
			String url=intent.getStringExtra("url");
			
			mTextView.setText(title+"/n"+content+"/n"+url);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}
	
	
	
}
