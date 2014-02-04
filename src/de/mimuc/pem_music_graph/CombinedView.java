package de.mimuc.pem_music_graph;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.internal.ar;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import de.mimuc.pem_music_graph.favorite_list.FavoriteAdapter;
import de.mimuc.pem_music_graph.graph.GenreGraphListener;
import de.mimuc.pem_music_graph.graph.GenreNode;
import de.mimuc.pem_music_graph.graph.MusicGraphView;
import de.mimuc.pem_music_graph.list.Event;
import de.mimuc.pem_music_graph.list.EventControllerListener;
import de.mimuc.pem_music_graph.list.ExpandableListAdapter2;
import de.mimuc.pem_music_graph.list.EventController;
import de.mimuc.pem_music_graph.list.FavoriteLocation;
import de.mimuc.pem_music_graph.list.JsonPreferences;
import de.mimuc.pem_music_graph.utils.ApplicationController;

/**
 * Shows both the music graph and the location list in one place
 * 
 * @author Christopher Gebhardt
 * 
 */
public class CombinedView extends FragmentActivity implements
ConnectionCallbacks, OnConnectionFailedListener,
EventControllerListener, GenreGraphListener {

	private static final String TAG = CombinedView.class.getSimpleName();

	private Context context;

	private SlidingUpPanelLayout layout;

	private MusicGraphView graphView;

	private ExpandableListAdapter2 adapter;
	private ExpandableListView locationListView;
	private RelativeLayout listHandle;

	private EventController mEventController;
	private Location mLocation;

	private LocationClient mLocationClient;

	private FragmentManager fragmentManager;
	
	private Fragment mapFragment;
	
	private ListView listFavorites;

	// coordinates for moving the view
	private double dy;

	boolean updated = false;

	int width = 0;
	int height = 0;

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
		}
	};

	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_eigenerStand:
	            if (checked)
	               
	            	// Pirates are the best
	            break;
	        case R.id.radio_andererStand:
	            if (checked)
	                // Ninjas rule
	            break;
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_combined_view);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String loadLastEvents = sharedPreferences.getString("events", "");
		
		mapFragment = new MapFragment();
		
		listFavorites = (ListView) findViewById(R.id.listFavorites);

		// get location updates
		mLocationClient = new LocationClient(this, this, this);

		// Put graph in framelayout because otherwise there is an error
		FrameLayout frame = (FrameLayout) findViewById(R.id.graph_view_frame);
		graphView = new MusicGraphView(this);
		graphView.setGenreGraphListener(this);
		frame.addView(graphView);
		graphView.onThreadResume();

		// try to load a json file we got from start screen
		if (getIntent().getStringExtra("json") != null) {

			// initialize controller
			String json = getIntent().getStringExtra("json");
			double latitude = getIntent().getDoubleExtra("latitude", 0.0);
			double longitude = getIntent().getDoubleExtra("longitude", 0.0);
			Location location = new Location("location");
			location.setLatitude(latitude);
			location.setLongitude(longitude);
			mLocation = location;
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

		mEventController.setGenreNode(graphView.getRootNode());

		String favorites = sharedPreferences.getString("favorites", "");
		if (!(favorites.isEmpty())) {
			mEventController.setFavorites(JsonPreferences
					.createFavoritesFromJson(favorites));
			mEventController.updateFavorites();

			Log.v("Favorites", favorites);
		}
		
		updateFavoriteList();

		this.context = this;

		// intialize listview
		locationListView = (ExpandableListView) findViewById(R.id.list_view);
		ExpandableListAdapter2 adapter = new ExpandableListAdapter2(this,
				mEventController.getEventList());
		locationListView.setAdapter(adapter);

		// initialize dimensions
		DisplayMetrics metrics = ApplicationController.getInstance()
				.getResources().getDisplayMetrics();
		width = metrics.widthPixels;
		height = metrics.heightPixels;

		// get list handle
		listHandle = (RelativeLayout) findViewById(R.id.list_handle);

		// initialize slide panel
		layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		layout.setPanelHeight((int) (height * 0.5));
		layout.setDragView(listHandle);
		layout.setCoveredFadeColor(getResources().getColor(
				android.R.color.transparent));
		layout.setPanelSlideListener(new PanelSlideListener() {

			@SuppressLint("NewApi")
			@Override
			public void onPanelSlide(View panel, float slideOffset) {
//				graphView.onThreadPause();

				// if(android.os.Build.VERSION.SDK_INT >
				// Build.VERSION_CODES.HONEYCOMB){
				// if (slideOffset < 0.2) {
				// if (getActionBar().isShowing()) {
				// getActionBar().hide();
				// }
				// } else {
				// if (!getActionBar().isShowing()) {
				// getActionBar().show();
				// }
				// }
				// }
			}

			@Override
			public void onPanelExpanded(View panel) {
//				graphView.onThreadPause();

				// FIXME find other method for Android 2.3
				if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
					((FrameLayout) layout.findViewById(R.id.graph_view_frame))
					.removeAllViews();
				}
			}

			@Override
			public void onPanelCollapsed(View panel) {
//				graphView.onThreadResume();

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
		String json = mEventController.getJsonForSharedPreferences();
		sharedPreferences.edit().putString("events", json).commit();
		String favorites = JsonPreferences
				.createJsonFromFavorites(mEventController.getFavorites());
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
								.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

		mLocationClient.requestLocationUpdates(locationRequest,
				mLocationListener);
	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onEventControllerUpdate() {
		Log.d(TAG, "Update ListAdapter");
		
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				graphView.onThreadPause();

				// save last scroll position
				int index = locationListView.getFirstVisiblePosition();
				View v = locationListView.getChildAt(0);
				int top = (v == null) ? 0 : v.getTop();

				adapter = new ExpandableListAdapter2(context, mEventController
						.getEventList());
				locationListView.setAdapter(adapter);
				if (mEventController.isNoEvents()) {
					adapter.setNoEvents(true);
					mEventController.setNoEvents(false);
				}

				// restore scroll position
				locationListView.setSelectionFromTop(index, top);

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

	/**
	 * calls the method to add a favorite in the EventController
	 */
	@Override
	public void onAddFavorites(String locationID) {
		mEventController.onAddFavorites(locationID);
		updateFavoriteList();
	}

	/**
	 * calls the method to remove the ID from favorites in the EventController
	 */
	@Override
	public void onRemoveFavorites(String locationID) {
		mEventController.onRemoveFavorites(locationID);
		updateFavoriteList();
	}

	@Override
	public void onExpandedItemTrue(String locationID) {
		mEventController.onExpandedItemTrue(locationID);
		
		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		if(fragment != null)
			getSupportFragmentManager().beginTransaction().remove(fragment).commit();
	}

	@Override
	public void onExpandedItemFalse(){
		mEventController.onExpandedItemFalse();
		
		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		if(fragment != null)
			getSupportFragmentManager().beginTransaction().remove(fragment).commit();
	}

	@SuppressLint("NewApi")
	@Override
	public void onGraphUpdate(GenreNode node, int newHeight) {
		mEventController.setGenreNode(node);
		onEventControllerUpdate();
		// layout.animatePanelHeight((int)(newHeight +
		// GenreGraphConstants.SCREEN_MARGIN_FACTOR * width * 3));
		Log.d(TAG, "Click on node " + node.name);
	}

	@Override
	public void onShareEvent(Event event) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TITLE, event.eventName);
		intent.putExtra(Intent.EXTRA_TEXT, "Ich gehe heute Abend zu "+ event.eventName+" ins "+event.locationName); 
		startActivity(Intent.createChooser(intent, "Share with..."));
	}

	@Override
	public void scrollEventTop(View listItem) {
		if(listItem != null){
			locationListView.smoothScrollToPositionFromTop((Integer)listItem.getTag(), 0);
		}
	}

	@Override
	public void attachMap(Event event) {
		if(event != null){
			Bundle args = new Bundle();
			args.putDouble("lat", Double.parseDouble(event.locationLatitude));
			args.putDouble("lon", Double.parseDouble(event.locationLongitude));
			
			MapFragment mapFragment = new MapFragment();
			mapFragment.setArguments(args);
			getSupportFragmentManager().beginTransaction().replace(R.id.map_container, mapFragment).commit();
		}
	}
	
	private void updateFavoriteList(){
		LinkedList<FavoriteLocation> favLocations = new LinkedList<FavoriteLocation>();
		
		for (Entry<String, FavoriteLocation> entry : mEventController.getFavorites().entrySet()) {
			favLocations.add(entry.getValue());
		}
		
		listFavorites.setAdapter(new FavoriteAdapter(
				this, favLocations));
	}

	@Override
	public void onEventControllerFinished() {
		updateFavoriteList();
	}
}
