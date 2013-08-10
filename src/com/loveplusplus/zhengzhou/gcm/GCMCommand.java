package com.loveplusplus.zhengzhou.gcm;

import android.content.Context;
import android.os.Bundle;

public abstract class GCMCommand {
	public abstract void execute(Context context, String type, String extrasData);
}
