package com.loveplusplus.zhengzhou.provider;

import java.io.IOException;
import java.util.ArrayList;

import com.loveplusplus.zhengzhou.R;
import com.loveplusplus.zhengzhou.io.LineHandler;
import com.loveplusplus.zhengzhou.io.StationHandler;
import com.loveplusplus.zhengzhou.io.JSONHandler;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;

public class SyncHelper {

	private Context mContext;

	public SyncHelper(Context context) {
		mContext = context;
	}

	public void sync() throws IOException {
		final ContentResolver resolver = mContext.getContentResolver();
		ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
		
		batch.addAll(new LineHandler(mContext).parse(JSONHandler.parseResource(mContext, R.raw.lines)));
		
		batch.addAll(new StationHandler(mContext).parse(JSONHandler.parseResource(mContext, R.raw.stations)));
		
		try {
			resolver.applyBatch(BusContract.CONTENT_AUTHORITY, batch);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			e.printStackTrace();
		}
	}
}
