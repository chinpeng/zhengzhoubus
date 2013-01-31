package com.loveplusplus.zhengzhou;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.loveplusplus.zhengzhou.provider.BusContract.Bus;
import com.loveplusplus.zhengzhou.ui.NotifyDetailActivity;
import com.loveplusplus.zhengzhou.util.ServerUtilities;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

	public GCMIntentService() {
		super(Config.SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		// displayMessage(context, getString(R.string.gcm_registered));
		ServerUtilities.register(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		// displayMessage(context, getString(R.string.gcm_unregistered));
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			ServerUtilities.unregister(context, registrationId);
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.i(TAG, "Ignoring unregister callback");
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");
		Bundle extras = intent.getExtras();
		int msgCode = Integer.parseInt(extras.getString("msg_code"));
		
		
		switch (msgCode) {
		case 1000:
			insert(extras);
			break;
		case 1001:
			delete(extras);
			break;
		default:
			generateNotification(context, extras);
		}

		
	}

	private void delete(Bundle extras) {
		String uri = extras.getString("uri");
		String where=extras.getString("where");
		String selection=extras.getString("selection");
		getContentResolver().delete(Uri.parse(uri), where, new String[]{selection});		
	}
	
	private void insert(Bundle extras) {
		String uri = extras.getString("uri");
		ContentValues values=new ContentValues();
		values.put(Bus.ALIAS, extras.getString("alias"));
		values.put(Bus.CARFARE, extras.getString("carfare"));
		values.put(Bus.DEPT_NAME, extras.getString("dept_name"));
		values.put(Bus.FIRST_TIME, extras.getString("first_time"));
		values.put(Bus.IS_UP_DOWN, extras.getString("is_up_down"));
		values.put(Bus.LABEL_NO, extras.getString("label_no"));
		values.put(Bus.LATITUDE, extras.getString("lat"));
		values.put(Bus.LINE_NAME, extras.getString("line_name"));
		values.put(Bus.LONGITUDE, extras.getString("lng"));
		values.put(Bus.STATION_NAME, extras.getString("station_name"));
		values.put(Bus.YN_USE_IC_A, extras.getString("yn_use_ic_a"));
		values.put(Bus.YN_USE_IC_B, extras.getString("yn_use_ic_b"));
		values.put(Bus.YN_USE_IC_C, extras.getString("yn_use_ic_c"));
		values.put(Bus.YN_USE_IC_D, extras.getString("yn_use_ic_d"));
		
		getContentResolver().insert(Uri.parse(uri), values);
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		// displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		// displayMessage(context, getString(R.string.gcm_recoverable_error,
		// errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, Bundle extras) {
		String title = extras.getString("title");
		String message = extras.getString("content");
		String url = extras.getString("url");

		int icon = R.drawable.icon;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		// String title = context.getString(R.string.app_name);
		Intent notificationIntent = new Intent(context,
				NotifyDetailActivity.class);
		notificationIntent.putExtras(extras);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);
	}

}
