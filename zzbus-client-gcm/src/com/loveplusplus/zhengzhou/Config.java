package com.loveplusplus.zhengzhou;

import android.content.Context;
import android.content.Intent;

public class Config {

	public static final String CHARSET_NAME = "UTF-8";
	public static final String SHARED_PREFERENCE_NAME = null;
	public static final String NOTIFICATION_ICON = null;
	/**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
    public static final String SERVER_URL = "http://zhengzhoubus.sinaapp.com";

    public static final String REGISTER_URL = Config.SERVER_URL + "/api/register";
    public static final String UNREGISTER_URL = Config.SERVER_URL + "/api/unregister";
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
	public static final String BUS_URL = SERVER_URL+"/api/bus";
	public static final String LINE_URL = SERVER_URL+"/api/line";
	public static final String STATION_URL = SERVER_URL+"/api/station";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
