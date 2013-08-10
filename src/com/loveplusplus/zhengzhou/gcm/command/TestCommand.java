package com.loveplusplus.zhengzhou.gcm.command;

import static com.loveplusplus.zhengzhou.util.LogUtils.LOGD;
import static com.loveplusplus.zhengzhou.util.LogUtils.makeLogTag;
import android.content.Context;

import com.loveplusplus.zhengzhou.gcm.GCMCommand;

public class TestCommand extends GCMCommand {

	private static final String TAG = makeLogTag(TestCommand.class);

	@Override
	public void execute(Context context, String type, String extrasData) {
		LOGD(TAG, context.toString());
		LOGD(TAG, "type"+type);
		LOGD(TAG, "extraData"+extrasData);
	}

}
