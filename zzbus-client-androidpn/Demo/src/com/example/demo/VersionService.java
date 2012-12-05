package com.example.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.loveplusplus.androidpn.Constants;

public class VersionService extends IntentService {

	private static final String TAG = "VersionService";
	private static final String MAIN_ACTIVITY = "com.example.demo.MainActivity";
	private static  final String APK_PATH = "/data/data/com.example.demo/kjj.apk";

	private String title = "科技通";
	private String url = "";
	// 文件存储
	private File file = null;

	// 通知栏
	private NotificationManager updateNotificationManager = null;
	private Notification updateNotification = null;
	// 通知栏跳转Intent
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;

	// 下载状态
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;

	public VersionService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		
		file = new File(APK_PATH);
		 updateNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		 updateNotification = new Notification();

		// 获取传值
		 url = intent.getStringExtra("url");

		// 设置下载过程中，点击通知栏，回到主界面
		 updateIntent = new Intent(MAIN_ACTIVITY);
		 updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent,
				0);
		// 设置通知栏显示内容
		updateNotification.icon = getNotificationIcon();
		updateNotification.tickerText = "开始下载";
		updateNotification.setLatestEventInfo(this, title, "0%",
				updatePendingIntent);
		// 发出通知
		updateNotificationManager.notify(0, updateNotification);

		// 开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
		new Thread(new updateRunnable()).start();// 这个是下载的重点，是下载的过程
	}

	private int getNotificationIcon() {
		return getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(Constants.NOTIFICATION_ICON, 0);
	}

	class updateRunnable implements Runnable {
		Message message = updateHandler.obtainMessage();

		public void run() {
			message.what = DOWNLOAD_COMPLETE;
			try {

				long downloadSize = downloadUpdateFile(url, file);
				if (downloadSize > 0) {
					// 下载成功
					updateHandler.sendMessage(message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				// 下载失败
				updateHandler.sendMessage(message);
			}
		}
	}

	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_COMPLETE:
				try {
					// 点击安装PendingIntent
					String command = "chmod 777 " + APK_PATH;
					Runtime runtime = Runtime.getRuntime();
					runtime.exec(command);

					Intent installIntent = new Intent(Intent.ACTION_VIEW);
					File file = new File(APK_PATH);
					Uri uri = Uri.fromFile(file);

					installIntent.setDataAndType(uri,
							"application/vnd.android.package-archive");

					updatePendingIntent = PendingIntent.getActivity(
							VersionService.this, 0, installIntent, 0);

					updateNotification.defaults = Notification.DEFAULT_SOUND;// 铃声提醒
					updateNotification.setLatestEventInfo(VersionService.this,
							title, "下载完成,点击安装。", updatePendingIntent);
					updateNotificationManager.notify(0, updateNotification);
				} catch (Exception e) {
					Log.d(TAG, "安装出现异常", e);
				}
				// 停止服务
				stopService(updateIntent);
			case DOWNLOAD_FAIL:
				// 下载失败
				updateNotification.setLatestEventInfo(VersionService.this,
						title, "下载失败。", updatePendingIntent);
				updateNotificationManager.notify(0, updateNotification);
			default:
				stopService(updateIntent);
			}
		}
	};

	private long downloadUpdateFile(String downloadUrl, File saveFile)
			throws Exception {
		// 这样的下载代码很多，我就不做过多的说明
		int downloadCount = 0;
		int currentSize = 0;
		long totalSize = 0;
		int updateTotalSize = 0;

		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection
					.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes="
						+ currentSize + "-");
			}
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();
			if (httpConnection.getResponseCode() == 404) {
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile, false);
			byte buffer[] = new byte[4096];
			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;
				// 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
				if ((downloadCount == 0)
						|| (int) (totalSize * 100 / updateTotalSize) - 10 > downloadCount) {
					downloadCount += 10;
					updateNotification.setLatestEventInfo(VersionService.this,
							"正在下载", (int) totalSize * 100 / updateTotalSize
									+ "%", updatePendingIntent);
					updateNotificationManager.notify(0, updateNotification);
				}
			}
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return totalSize;
	}

	
}
