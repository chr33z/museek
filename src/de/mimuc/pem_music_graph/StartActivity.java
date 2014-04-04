package de.mimuc.pem_music_graph;

import java.io.IOException;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import de.mimuc.pem_music_graph.utils.ApplicationController;
import de.mimuc.pem_music_graph.utils.FileUtils;
import de.mimuc.pem_music_graph.utils.JsonConstants;
import de.mimuc.pem_music_graph.utils.ServerConnector;
import de.mimuc.pem_music_graph.utils.ServerConnector.ServerConnectorListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;

/**
 * Start Activity of Museek. Show splashscreen and get a valid event list either from server or out of storage.
 * 
 * @author Christopher Gebhardt
 *
 */
public class StartActivity extends Activity implements JsonConstants {

	private static final String TAG = StartActivity.class.getSimpleName();

	/** time splash screen is shown */
	private static final int MAX_WAITING_TIME = 10 * 1000;
	
	private static final long MAX_EVENT_AGE = 7 * 24 * 60 * 60 * 1000; // 7 days in milliseconds

	private LocationManager mLocationManager;

	/** current location */
	private Location mLocation;

	/** the last known location in case we don't get a new on */
	private Location mLastKnownLocation;

	private boolean mDataLoaded = false;

	/*
	 * Listen to location updates from location manager
	 */
	LocationListener mLocationListener = new LocationListener() {

		public void onLocationChanged(Location location) {
			if(location == null){
				Log.w(TAG, "Invalid location received.");
				return;
			}
			
			if(mDataLoaded && mLocation != null && location.distanceTo(mLocation) < ApplicationController.MAX_UPDATE_DISTANCE){
				return;
			}

			mLocation = location;
			Log.d(TAG, "Location changed to: "+ location.getLatitude() + " | " + location.getLongitude());

			initEventList();
			removeLocationUpdates();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) { }

		public void onProviderEnabled(String provider) { }

		public void onProviderDisabled(String provider) { }
	};
	
	/**
	 * This methods are called when the answer from the server is returned
	 */
	ServerConnectorListener mServerListener = new ServerConnectorListener() {
		
		@Override
		public void requestFinished(JSONObject json) {
			try {
				FileUtils.writeEventListToStorage(json, StartActivity.this);
				startMuseek();
			} catch (IOException e) {
				Log.w(TAG, "Could not save event list from server.");
			}
		}
		
		@Override
		public void requestError() {
			Log.w(TAG, "Could get event list from server.");
		}
	};
	
	private Handler maxWaitingTimeHandler = new Handler();
	
	private ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startscreen);

		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		mLastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

		maxWaitingTimeHandler.postDelayed(new Runnable(){
			@Override
			public void run(){
				removeLocationUpdates();
				// TODO do something when nothing happens
			}
		}, MAX_WAITING_TIME);
		
		// show progress bar while loading
		progress = new ProgressDialog(this);
        progress.setTitle("Please Wait!!");
        progress.setMessage("Wait!!");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
		
		/*
		 * the app can only work if we get a location. so the interesting part starts in onLocationChanged()
		 */
	}
	
	/**
	 * Get a valid event list either from storage or from server
	 */
	private void initEventList(){
		/*
		 * we save a valid event list in internal storage so we can load them in
		 * our main activity. So we make sure there is either a valid one alreay in storage
		 * or we get a fresh one from our server and save it in storage.
		 */
		
		// first try to get an existing eventlist from storage
		JSONObject eventList = FileUtils.readEventListFromStorage(this);
		
		if(eventList != null) {
			if(eventListIsValid(eventList)){
				startMuseek();
			} else {
				ServerConnector.getEventListFromServer(mServerListener, mLocation);
			}
		} else {
			ServerConnector.getEventListFromServer(mServerListener, mLocation);
		}
		
	}
	
	/**
	 * Starts the main activity. At this point a valid event list must be stored in internal storage
	 */
	private void startMuseek(){
		Intent mainActivity = new Intent(getBaseContext(), MainActivity.class);
		mainActivity.putExtra("latitude", mLocation.getLatitude());
		mainActivity.putExtra("longitude", mLocation.getLongitude());
		startActivity(mainActivity);
	}
	
	/**
	 * Check if an event list is still valid in terms of age {@link StartActivity.MAX_EVENT_AGE}.
	 * Distance has to be implemented
	 * 
	 * @param eventList
	 * @return true if event list is valid and up to date or false otherwise
	 */
	private boolean eventListIsValid(JSONObject eventList){
		/*
		 * TODO
		 * check for distance as well
		 */
		try {
			String tmp = eventList.getString("timestamp");
			long timeStamp = Long.parseLong(tmp);
			
			if(new DateTime().minus(MAX_EVENT_AGE).getMillis() < timeStamp){
				return true;
			} else {
				return false;
			}
			
		} catch (JSONException e) {
			Log.w(TAG, "eventListIsValid: Validating JSON failed.");
			return false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerLocationUpdates();
	}

	@Override
	protected void onPause() {
		super.onPause();
		removeLocationUpdates();
	}

	private void registerLocationUpdates(){
		if(mLocationManager != null){
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
			mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, mLocationListener);
		}
	}

	private void removeLocationUpdates(){
		if(mLocationManager != null){
			mLocationManager.removeUpdates(mLocationListener);
		}
	}
}
