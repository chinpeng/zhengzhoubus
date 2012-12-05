package com.example.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.loveplusplus.androidpn.ServiceManager;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        try {
			ServiceManager serviceManager = new ServiceManager(this);
			serviceManager.setNotificationIcon(R.drawable.ic_launcher);
			serviceManager.startService();
		} catch (Exception e) {
			Log.e(TAG, "启动推送服务失败", e);
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
}
