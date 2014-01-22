package de.mimuc.pem_music_graph;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import de.mimuc.pem_music_graph.graph.MusicGraphView;
import de.mimuc.pem_music_graph.list.EventControllerListener;
import de.mimuc.pem_music_graph.list.ExpandableListAdapter2;
import de.mimuc.pem_music_graph.list.EventController;
import de.mimuc.pem_music_graph.utils.ApplicationController;

/**
 * Shows both the music graph and the location list in one place
 * 
 * @author Christopher Gebhardt
 * 
 */
public class CombinedView extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener, EventControllerListener {

	private static final String TAG = CombinedView.class.getSimpleName();

	private Context context;

	private SlidingUpPanelLayout layout;

	private MusicGraphView graphView;

	private ExpandableListAdapter2 adapter;
	private ExpandableListView locationListView;
	private FrameLayout listHandle;

	private EventController mEventController;
	private Location mLocation;

	private LocationClient mLocationClient;

	// coordinates for moving the view
	private double dy;

	boolean updated = false;

	private SharedPreferences sharedPreferences;

	/**
	 * Is called when location updates arrive
	 */
	private LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			if (location == null)
				return;

			if (mLocation != null
					&& location.distanceTo(mLocation) < ApplicationController.MAX_UPDATE_DISTANCE) {
				Log.i(TAG, "Location not changed.");
				return;
			}

			Log.i(TAG, "Location changed to (" + location.getLatitude() + ", "
					+ location.getLongitude() + ")");
			mEventController.updateEvents(location);

			mEventController.updateEvents(location);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_combined_view);

		// sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//		sharedPreferences.edit().clear().commit();
		String loadLastEvents = sharedPreferences.getString("events", "");

		// get location updates
		mLocationClient = new LocationClient(this, this, this);

		if (getIntent().getStringExtra("json") != null) {

			// initialize controller
			String json = getIntent().getStringExtra("json");
			double latitude = getIntent().getDoubleExtra("latitude", 0.0);
			double longitude = getIntent().getDoubleExtra("longitude", 0.0);
			Location location = new Location("location");
			location.setLatitude(latitude);
			location.setLongitude(longitude);
			try {
				mEventController = new EventController(this, new JSONObject(
						json), location);
			} catch (JSONException e) {
				Log.w(TAG, "Could not create json from string");
				e.printStackTrace();
				mEventController = new EventController(this);
				// mEventController = new EventController(this, new
				// JSONObject(loadLastEvents));
			}
		} else {
			mEventController = new EventController(this);
			// mEventController = new EventController(this, new
			// JSONObject(loadLastEvents));
		}

		this.context = this;

		// get list handle
		listHandle = (FrameLayout) findViewById(R.id.list_handle);

		// Put graph in framelayout because otherwise there is an error
		FrameLayout frame = (FrameLayout) findViewById(R.id.graph_view_frame);
		graphView = new MusicGraphView(this);
		frame.addView(graphView);
		graphView.onThreadResume();

		// intialize listview
		locationListView = (ExpandableListView) findViewById(R.id.list_view);
		ExpandableListAdapter2 adapter = new ExpandableListAdapter2(this,
				mEventController.getEventList());
		locationListView.setAdapter(adapter);

		// Update once per hand
		onEventControllerUpdate();

		// initialize dimensions
		DisplayMetrics metrics = ApplicationController.getInstance()
				.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;

		layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		layout.setPanelHeight((int) (height * 0.5));
		layout.setDragView(listHandle);
		layout.setCoveredFadeColor(getResources().getColor(
				android.R.color.transparent));
		layout.setPanelSlideListener(new PanelSlideListener() {

			@SuppressLint("NewApi")
			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				graphView.onThreadPause();

				if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
					if (slideOffset < 0.2) {
						if (getActionBar().isShowing()) {
							getActionBar().hide();
						}
					} else {
						if (!getActionBar().isShowing()) {
							getActionBar().show();
						}
					}
				}
			}

			@Override
			public void onPanelExpanded(View panel) {
				graphView.onThreadPause();

				// FIXME find other method for Android 2.3
				if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
					((FrameLayout) layout.findViewById(R.id.graph_view_frame))
							.removeAllViews();
				}
			}

			@Override
			public void onPanelCollapsed(View panel) {
				graphView.onThreadResume();

				// FIXME find other method for Android 2.3
				if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
					FrameLayout frame = ((FrameLayout) layout
							.findViewById(R.id.graph_view_frame));
					if (frame.getChildCount() == 0) {
						frame.addView(graphView);
					}
				}
			}

			@Override
			public void onPanelAnchored(View panel) {

			}
		});

		String favorites = sharedPreferences.getString("favorites", "");
		if (!(favorites.isEmpty()))
			mEventController.writeInFavorites(favorites);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.combined_view, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (graphView != null)
			graphView.onThreadResume();

		if (mLocationClient != null)
			mLocationClient.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();

		graphView.onThreadPause();

		if (mLocationClient != null)
			mLocationClient.disconnect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		String json = mEventController.getJsonForSharedPreferences().toString();
		sharedPreferences.edit().putString("events", json).commit();
		String favorites = mEventController.getFavorites().toString();
		sharedPreferences.edit().putString("favorites", favorites).commit();
		Log.v(TAG, favorites);

		graphView.onThreadPause();
		if (graphView != null)
			graphView.onThreadPause();

		if (mLocationClient != null)
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
		LocationRequest locationRequest = LocationRequest
				.create()
				.setInterval(
						ApplicationController.DEFAULT_UPDATE_LOCATION_INTERVAL)
				.setExpirationDuration(
						ApplicationController.DEFAULT_TERMINATE_SAT_FINDING)
				.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY); // Accuracy
																				// of
																				// about
																				// 100m
		mLocationClient.requestLocationUpdates(locationRequest,
				mLocationListener);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEventControllerUpdate() {
		Log.d(TAG, "Update ListAdapter");

		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				graphView.onThreadPause();
				adapter = new ExpandableListAdapter2(context, mEventController
						.getEventList());
				locationListView.setAdapter(adapter);
				graphView.onThreadResume();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// hijack back button to do what we want
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (!layout.isExpanded() && !graphView.isAtRoot()) {
				graphView.graphNavigateBack();
				return true;
			} else if (layout.isExpanded()) {
				layout.collapsePane();
				return true;
			} else {
				moveTaskToBack(true);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onAddFavorites(String locationID) {
		mEventController.onAddFavorites(locationID);
	}

	@Override
	public void onRemoveFavorites(String locationID) {
		mEventController.onRemoveFavorites(locationID);
	}

	@Override
	public void onExpandedItem(String locationID, boolean b) {
		mEventController.onExpandedItem(locationID, b);
	}

}
