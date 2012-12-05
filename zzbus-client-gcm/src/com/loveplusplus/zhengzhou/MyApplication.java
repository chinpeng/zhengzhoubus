package com.loveplusplus.zhengzhou;

import static com.loveplusplus.zhengzhou.util.CommonUtilities.SENDER_ID;
import static com.loveplusplus.zhengzhou.util.CommonUtilities.SERVER_URL;

import com.google.android.gcm.GCMRegistrar;
import com.loveplusplus.zhengzhou.util.ServerUtilities;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

public class MyApplication extends Application {

	private static final String TAG = "MyApplication";
	AsyncTask<Void, Void, Void> mRegisterTask;
	@Override
	public void onCreate() {
		super.onCreate();
		checkNotNull(SERVER_URL, "SERVER_URL");
        checkNotNull(SENDER_ID, "SENDER_ID");
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
        
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
             //   mDisplay.append(getString(R.string.already_registered) + "\n");
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered =
                                ServerUtilities.register(context, regId);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }
	}
	
	
//	 @Override
//	    protected void onDestroy() {
//	        if (mRegisterTask != null) {
//	            mRegisterTask.cancel(true);
//	        }
//	      //  unregisterReceiver(mHandleMessageReceiver);
//	        GCMRegistrar.onDestroy(this);
//	        super.onDestroy();
//	    }
    private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(
                    getString(R.string.error_config, name));
        }
    }
}
