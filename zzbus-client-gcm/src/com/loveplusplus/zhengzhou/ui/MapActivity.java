package com.loveplusplus.zhengzhou.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loveplusplus.zhengzhou.R;

public class MapActivity extends Activity {

	protected static final String TAG = "MapActivity";
	private GoogleMap mMap;
	private Marker melbourne;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		//addLocation();
		//addStations();
	}

	private void addStations() {

		List<StationGeo> list = new ArrayList<StationGeo>();
		
		StationGeo bean1 = new StationGeo(34.811457,113.622892);
		list.add(bean1);
		
		
		
		for (StationGeo geo : list) {
			final LatLng MELBOURNE = new LatLng(geo.getLat(), geo.getLon());
			Marker melbourne = mMap.addMarker(new MarkerOptions()
					.position(MELBOURNE).title("东风路与文化路").snippet("kdjf"));
			melbourne.showInfoWindow();
		}
	}

	private void addLocation() {
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
				&& !locationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			Toast.makeText(this, "请打开gps", Toast.LENGTH_SHORT).show();
			return;
		}

		Toast.makeText(this, "正在搜索gps", Toast.LENGTH_SHORT).show();

		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			locationManager
					.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							1000, 0, locationListener);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);

		}

		public void onProviderDisabled(String provider) {

		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	private void updateWithNewLocation(Location result) {
		double lat = result.getLatitude();
		double lon = result.getLongitude();

		setUpMapIfNeeded(lat, lon);

		((LocationManager) this.getSystemService(Context.LOCATION_SERVICE))
				.removeUpdates(locationListener);

	}

	private void setUpMapIfNeeded(double lat, double lon) {

		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
			public void onInfoWindowClick(Marker marker) {
				Log.d(TAG, marker.getTitle());
			}
		});

		final LatLng MELBOURNE = new LatLng(lat, lon);
		melbourne = mMap.addMarker(new MarkerOptions().position(MELBOURNE)
				.title("我的位置").snippet(String.format("[%f,%f]", lat, lon)));
		melbourne.showInfoWindow();
		LatLng latLng = new LatLng(lat, lon);
		CameraUpdate update = CameraUpdateFactory.newLatLng(latLng);
		mMap.moveCamera(update);
		
		mMap.getUiSettings().setMyLocationButtonEnabled(true);

	}

}
