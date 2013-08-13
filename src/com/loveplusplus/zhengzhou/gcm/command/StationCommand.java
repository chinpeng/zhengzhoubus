package com.loveplusplus.zhengzhou.gcm.command;

import static com.loveplusplus.zhengzhou.util.LogUtils.LOGD;
import static com.loveplusplus.zhengzhou.util.LogUtils.makeLogTag;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.gcm.GCMCommand;
import com.loveplusplus.zhengzhou.ui.HomeActivity;

public class StationCommand extends GCMCommand {

	private static final String TAG = makeLogTag(StationCommand.class);

	@Override
	public void execute(Context context, String type, String extrasData) {
		LOGD(TAG, context.toString());
		LOGD(TAG, "type"+type);
		LOGD(TAG, "extraData"+extrasData);
		
		((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
        .notify(0, new NotificationCompat.Builder(context)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.bus)
                .setTicker("更新了5条公交信息")
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("更新了5条公交信息，121路，168路，169路，333路，99路")
                .setContentIntent(
                        PendingIntent.getActivity(context, 0,
                                new Intent(context, HomeActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                Intent.FLAG_ACTIVITY_SINGLE_TOP),
                                0))
                .setAutoCancel(true)
                .build());
	}
	
	
}
