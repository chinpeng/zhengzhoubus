package com.loveplusplus.zhengzhou.util;

import android.content.Context;
import android.content.Intent;

public class Constants {

	public static final String CHARSET_NAME = "UTF-8";
	public static final String SHARED_PREFERENCE_NAME = null;
	public static final String NOTIFICATION_ICON = null;
	/**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
    public static final String SERVER_URL = "http://192.168.1.85:8080/GCMService";

    /**
     * Google API project id registered to use GCM.
     */
    public static final String SENDER_ID = "1030139280792";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    /**
     * Intent used to display a message in the screen.
     */
    static final String DISPLAY_MESSAGE_ACTION =
            "com.loveplusplus.push.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
