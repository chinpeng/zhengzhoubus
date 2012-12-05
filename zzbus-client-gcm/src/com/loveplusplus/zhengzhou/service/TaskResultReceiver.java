package com.loveplusplus.zhengzhou.service;


import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class TaskResultReceiver extends ResultReceiver {

	public interface Receiver {
		public void onReceiveResult(int resultCode, Bundle resultData);
	}

	private Receiver mReceiver;

	public TaskResultReceiver(Handler handler) {
		super(handler);
	}

	public void setReceiver(Receiver receiver) {
		mReceiver = receiver;
	}

	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {
		if (mReceiver != null) {
			mReceiver.onReceiveResult(resultCode, resultData);
		}
	}

}
