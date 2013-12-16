package de.mimuc.pem_music_graph;

import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.ExpandableListView;

public class StartScreen extends Activity implements LocationSource,
		LocationListener {

	private static final String TAG = StartScreen.class.getName();

	LocationController locationController;
	Location mLocation;

	LocationManager mLocationManager = null;
	OnLocationChangedListener mLocationListener = null;
	Criteria mCriteria;

	private ExpandableListAdapter2 listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startscreen);

		mCriteria = new Criteria();
		mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		locationController = new LocationController();

		// get the listview
		ExpandableListView expListView = (ExpandableListView) findViewById(R.id.expandableListView1);
		listAdapter = new ExpandableListAdapter2(this);
		// setting list adapter
		expListView.setAdapter(listAdapter);
		listAdapter.setEventLocationList(locationController
				.getEventLocationList());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocationManager
				.requestLocationUpdates(0L, 0.0f, mCriteria, this, null);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		mLocation = location;
		LatLng currnetPoint = new LatLng(location.getLatitude(),
				location.getLongitude());
		if (mLocationListener != null) {
			mLocationListener.onLocationChanged(location);
		}
		Log.v(TAG, currnetPoint.toString());
		locationController.updateLocation(currnetPoint);
		listAdapter.setEventLocationList(locationController
				.getEventLocationList());
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		mLocationListener = listener;
	}

	@Override
	public void deactivate() {
		mLocationListener = null;
	}
}