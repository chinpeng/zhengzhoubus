package com.loveplusplus.zhengzhou.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loveplusplus.zhengzhou.io.model.Station;
import com.loveplusplus.zhengzhou.provider.BusContract;
import com.loveplusplus.zhengzhou.util.Lists;

public class StationHandler extends JSONHandler {

	public StationHandler(Context context) {
		super(context);
	}

	@Override
	public ArrayList<ContentProviderOperation> parse(String json)
			throws IOException {
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

		List<Station> busLines = new Gson().fromJson(json,
				new TypeToken<List<Station>>() {
				}.getType());

		for (Station s : busLines) {
			parseBusLine(s, batch);
		}
		return batch;
	}

	private void parseBusLine(Station station,
			ArrayList<ContentProviderOperation> batch) {
		ContentProviderOperation.Builder builder = ContentProviderOperation
				.newInsert(BusContract.BusLineStation.CONTENT_URI);

		builder.withValue(BusContract.BusLineStation.DIRECT, station.direct);
		builder.withValue(BusContract.BusLineStation.GPS_LAT, station.lat);
		builder.withValue(BusContract.BusLineStation.LINE_NAME,
				station.lineName);
		builder.withValue(BusContract.BusLineStation.GPS_LNG, station.lng);
		builder.withValue(BusContract.BusLineStation.SNO, station.sno);
		builder.withValue(BusContract.BusLineStation.STATION_NAME,
				station.stationName);

		batch.add(builder.build());
	}

}
