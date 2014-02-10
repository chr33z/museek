package de.mimuc.pem_music_graph;

import java.util.HashMap;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import de.mimuc.pem_music_graph.list.EventController;
import de.mimuc.pem_music_graph.utils.ApplicationController;
import de.mimuc.pem_music_graph.utils.JsonConstants;
import de.mimuc.pem_music_graph.utils.PlayServicesManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class StartScreen extends Activity 
implements ConnectionCallbacks, OnConnectionFailedListener, JsonConstants {

	private static final String TAG = StartScreen.class.getName();
	private static final int SPLASH_TIME = 3 * 1000;

//	Button btnGraph;

	private LocationClient mLocationClient;
	private Location mLocation;
	
	private boolean localEventList = false;
	
	private boolean mDataLoaded = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startscreen);
		
		if(PlayServicesManager.isGooglePlayServiceUpToDate(this)){
			mLocationClient = new LocationClient(this, this, this);
		}
	}
	
	@Override
	public void onBackPressed() {
		this.finish();
		super.onBackPressed();
	}
	
	private LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			if(location == null){
				Log.i(TAG, "Location is null.");
				return;
			}

			if(mDataLoaded && mLocation != null && location.distanceTo(mLocation) < ApplicationController.MAX_UPDATE_DISTANCE){
				Log.i(TAG, "Location not changed.");
				return;
			}
			
			mLocation = location;
			Log.d(TAG, "Location changed to: "+ location.getLatitude() + "; " + location.getLongitude());
			
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(ApplicationController.getInstance());
			String loadLastEvents = sharedPreferences.getString("events", "");
			
			if(!loadLastEvents.equals("") && !eventListNeedsUpdate(loadLastEvents)){
				Intent intent = new Intent(getBaseContext(), CombinedView.class);
				intent.putExtra("json", loadLastEvents);
				intent.putExtra("latitude", mLocation.getLatitude());
				intent.putExtra("longitude", mLocation.getLongitude());
				startActivity(intent);
				mDataLoaded = true;
				Log.d(TAG, "Loaded events from Shared Preferences");
			} else {
				getEventsFromServer(location);
			}
		}
	};

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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.i(TAG, "Location Connected.");

		// Get last known location
		Location lastLocation = mLocationClient.getLastLocation();
		mLocationListener.onLocationChanged(lastLocation);
		
		// Create location request
		LocationRequest locationRequest = LocationRequest.create()
				.setInterval(ApplicationController.DEFAULT_UPDATE_LOCATION_INTERVAL)
				.setExpirationDuration(ApplicationController.DEFAULT_TERMINATE_SAT_FINDING)
				.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY); // Accuracy of about 100m
		mLocationClient.requestLocationUpdates(locationRequest, mLocationListener);
	}

	@Override
	public void onDisconnected() {

	}
	
	/**
	 * stellt eine Anfrage an den Server, um die aktuelle Liste der
	 * EventLocations in einem bestimmten Radius abzufragen
	 */
	public void getEventsFromServer(final Location location) {
		Log.d(TAG, "Try to retrieve locations from server...");

		// Json fuer POST Request
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(TAG_LATITUDE, location.getLatitude() + "");
		params.put(TAG_LONGITUDE, location.getLongitude() + "");
		params.put("radius", "10000000000"); // radius in m

		// POST request
		JsonObjectRequest req = new JsonObjectRequest(
				ApplicationController.URL_POST_FIND_BY_LOCATION,
				new JSONObject(params), new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.i(TAG, "...success!");
						
						Intent intent = new Intent(getBaseContext(), CombinedView.class);
						intent.putExtra("json", response.toString());
						intent.putExtra("latitude", location.getLatitude());
						intent.putExtra("longitude", location.getLongitude());
						startActivity(intent);
						mDataLoaded = true;
						finish();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (error.getMessage() == null) {
							Log.e(TAG,
									"...could not retrieve events or a meaningfull error...");
							return;
						}
						VolleyLog.e("Error: ", error.getMessage());
						Log.w(TAG, "...could not retrieve locations!");
					}
				});

		ApplicationController.getInstance().addToRequestQueue(req);
	}
	
	private boolean eventListNeedsUpdate(String eventJson){
		boolean needsUpdate = true;
		
		try {
			JSONObject savedJson = new JSONObject(eventJson);
			long saveDate = Long.parseLong(savedJson.getString("saveDate"));
			
			DateTime now = new DateTime();
			
			DateTime difference = now.minus(saveDate);
			
			if(difference.getDayOfMonth() > 7){
				needsUpdate = true;
			} else {
				needsUpdate = false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch(NumberFormatException e){
			
		}
		
		return needsUpdate;
	}
}
