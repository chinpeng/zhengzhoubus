package com.loveplusplus.zhengzhou.io;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;


public class TaskService extends IntentService {

	private static final String TAG = "TaskService";
	public static final String EXTRA_STATUS_RECEIVER = "com.loveplusplus.zhengzhou.extra.STATUS_RECEIVER";

	public static final int STATUS_RUNNING = 0x1;
	public static final int STATUS_ERROR = 0x2;
	public static final int STATUS_FINISHED = 0x3;

	private RemoteExecutor mRemoteExecutor;

	// 需要一个没有参数的构造方法
	public TaskService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "TaskService start....");
		mRemoteExecutor = new RemoteExecutor();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent(intent=" + intent.toString() + ")");

		ResultReceiver receiver = intent.getParcelableExtra(EXTRA_STATUS_RECEIVER);
		receiver.send(STATUS_RUNNING, Bundle.EMPTY);
		try {
			String lineName = intent.getStringExtra("lineName");
			String ud = intent.getStringExtra("ud");
			String sno = intent.getStringExtra("sno");
			String hczd = intent.getStringExtra("hczd");
			
			// 调用http
			String back = mRemoteExecutor.request(lineName,ud,sno,hczd);
			Log.d(TAG, back.toString());
			Bundle b = new Bundle();
			b.putString("response", back);
			receiver.send(STATUS_FINISHED, b);
		} catch (Exception e) {
			Log.e(TAG, "服务器异常", e);
			if (receiver != null) {
				// Pass back error to surface listener
				Bundle bundle = new Bundle();
				bundle.putString(Intent.EXTRA_TEXT, "服务器异常");
				receiver.send(STATUS_ERROR, bundle);
			}
		}
	}
}
