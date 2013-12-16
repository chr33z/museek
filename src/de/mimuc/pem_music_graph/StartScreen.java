package de.mimuc.pem_music_graph;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import de.mimuc.pem_music_graph.graph.MusicGraph;

public class StartScreen extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

	private static final String TAG = StartScreen.class.getName();

	private static final long DEFAULT_UPDATE_LOCATION_INTERVAL = 30 * 1000; // update every 30 seconds
	private static final long DEFAULT_TERMINATE_SAT_FINDING = 1 * 60 * 60 * 1000; // for 1 hour

	private LocationController mLocationController;
	private Location mLocation;

	private LocationClient mLocationClient;

	private LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			if(location == null)
				return;

			if(mLocation != null &&
					location.getLatitude() == mLocation.getLatitude() && 
					location.getLongitude() == mLocation.getLongitude()){

				Log.i(TAG, "Location not changed.");
				return;
			}

			Log.i(TAG, "Location changed to (" + location.getLatitude() + ", " + location.getLongitude() + ")");
			mLocationController.updateLocation(location);
		}
	};

	private ExpandableListAdapter2 listAdapter;

	Button btnGraph;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startscreen);

		mLocationClient = new LocationClient(this, this, this);

		mLocationController = new LocationController();

//		// get the listview
//		ExpandableListView expListView = (ExpandableListView) findViewById(R.id.expandableListView1);
//		listAdapter = new ExpandableListAdapter2(this);
//		// setting list adapter
//		expListView.setAdapter(listAdapter);
//		listAdapter.setEventLocationList(mLocationController
//				.getEventLocationList());
		
		btnGraph = (Button) findViewById(R.id.btn_combined_view);
		
		btnGraph.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch(v.getId()){
				case R.id.btn_combined_view:
					startActivity(new Intent(getApplicationContext(), MusicGraph.class));
					break;
				}
			}
		});
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

		if(mLocationClient != null)
			mLocationClient.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if(mLocationClient != null)
			mLocationClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.i(TAG, "Location Connected.");
		// Get last known location
		Location lastLocation = mLocationClient.getLastLocation();
		mLocationListener.onLocationChanged(lastLocation);

		// Create location request
		LocationRequest locationRequest = LocationRequest.create()
				.setInterval(DEFAULT_UPDATE_LOCATION_INTERVAL)
				.setExpirationDuration(DEFAULT_TERMINATE_SAT_FINDING)
				.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		mLocationClient.requestLocationUpdates(locationRequest, mLocationListener);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}
}
