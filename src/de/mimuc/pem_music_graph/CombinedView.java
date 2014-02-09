package de.mimuc.pem_music_graph;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.List;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.SupportMapFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.mimuc.pem_music_graph.favorite_list.ExpandableFavoriteListAdapter;
import de.mimuc.pem_music_graph.favorite_list.FavoriteListListener;
import de.mimuc.pem_music_graph.favorite_list.FavoriteLocation;
import android.widget.TextView.OnEditorActionListener;
import de.mimuc.pem_music_graph.graph.GenreGraphConstants;
import de.mimuc.pem_music_graph.graph.GenreGraphListener;
import de.mimuc.pem_music_graph.graph.GenreNode;
import de.mimuc.pem_music_graph.graph.MusicGraphView;
import de.mimuc.pem_music_graph.list.Event;
import de.mimuc.pem_music_graph.list.EventControllerListener;
import de.mimuc.pem_music_graph.list.ExpandableListAdapter2;
import de.mimuc.pem_music_graph.list.EventController;
import de.mimuc.pem_music_graph.list.JsonPreferences;
import de.mimuc.pem_music_graph.utils.ApiGuard;
import de.mimuc.pem_music_graph.utils.ApplicationController;
import de.mimuc.pem_music_graph.utils.LocationFromAdress;
import de.mimuc.pem_music_graph.utils.UndoBarController;
import de.mimuc.pem_music_graph.utils.UndoBarController.UndoListener;

/**
 * Shows both the music graph and the location list in one place
 * 
 * @author Christopher Gebhardt, Anna Kienle, Nicole Lipppner, Edina Smajic
 * 
 */
public class CombinedView extends FragmentActivity implements
ConnectionCallbacks, OnConnectionFailedListener,
EventControllerListener, GenreGraphListener, FavoriteListListener,
UndoListener {

	private static final String TAG = CombinedView.class.getSimpleName();

	// sliding panel
	private SlidingUpPanelLayout slideUpPanel;

	// graph
	private MusicGraphView graphView;

	// list
	private ExpandableListAdapter2 adapter;
	private ExpandableListView locationListView;
	private RelativeLayout listHandle;
	private RelativeLayout listContainer;


	private EventController mEventController;
	private Location mCurrentLocation;
	private Location mAlternativeLocation;

	private boolean mUseAlternativeLocation;

	// location services
	private LocationClient mLocationClient;

	private Fragment mMapFragment;

	// navigation drawer right
	private ExpandableListView mListFavorites;
	private UndoBarController mUndoBarController;

	// navigation drawer left
	private DatePicker datePicker;
	private Button okB;
	private Button resetB;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;

	boolean updated = false;

	int screenWidth = 0;
	int screenHeight = 0;

	private SharedPreferences sharedPreferences;

	// root view for redraw operations
	View rootView;

	/**
	 * Is called when location updates arrive
	 */
	private LocationListener mLocationListener = new LocationListener() {

		public void onLocationChanged(Location location) {
			if (location == null)
				return;

			if (mCurrentLocation != null
					&& location.distanceTo(mCurrentLocation) < ApplicationController.MAX_UPDATE_DISTANCE) {
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
		switch (view.getId()) {
		case R.id.radio_ownStart:
			if (checked) {
				mUseAlternativeLocation = true;
				mEventController.setLocation(mCurrentLocation);
				drawerLayout.closeDrawer(Gravity.LEFT);
			}
			break;
		case R.id.radio_otherStart:
			if (checked) {
				mUseAlternativeLocation = false;
			}
			break;
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_combined_view);

		rootView = findViewById(R.id.root);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		mMapFragment = new MapFragment();

		mListFavorites = (ExpandableListView) findViewById(R.id.listFavorites);
		mListFavorites.setEmptyView(findViewById(R.id.favorite_empty));

		// set undo listener to undo favorite remove
		mUndoBarController = new UndoBarController(findViewById(R.id.undobar),
				this);

		// get location updates
		mLocationClient = new LocationClient(this, this, this);

		// init graph
		graphView = new MusicGraphView(this);
		graphView.setGenreGraphListener(this);
		((FrameLayout)findViewById(R.id.graph_view_frame)).addView(graphView);
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
			mCurrentLocation = location;
			try {
				mEventController = new EventController(this, new JSONObject(
						json), location);
			} catch (JSONException e) {
				Log.w(TAG, "Could not create json from string");
				mEventController = new EventController(this);
			}
		} else {
			mEventController = new EventController(this);
		}

		mEventController.setGenreNode(graphView.getRootNode());

		String favorites = sharedPreferences.getString("favorites", "");
		if (!(favorites.isEmpty())) {
			mEventController.setFavorites(JsonPreferences
					.createFavoritesFromJson(favorites));
			mEventController.updateFavorites();
			updateFavoriteList();
		}

		// intialize listview
		listContainer = (RelativeLayout) findViewById(R.id.list_container);
		locationListView = (ExpandableListView) findViewById(R.id.list_view);
		ExpandableListAdapter2 adapter = new ExpandableListAdapter2(this,
				mEventController.getEventList());
		locationListView.setAdapter(adapter);
		listHandle = (RelativeLayout) findViewById(R.id.list_handle);

		/*
		 * Force redraw of list while scrolling to prevent glitches
		 */
		if(ApiGuard.apiBelow(Build.VERSION_CODES.JELLY_BEAN)){
			locationListView.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
						listContainer.bringToFront();
						rootView.requestLayout();
						rootView.invalidate();
				}
			});
		}

		updateFavoriteList();

		// initialize dimensions
		DisplayMetrics metrics = ApplicationController.getInstance()
				.getResources().getDisplayMetrics();
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;

		// initialize slide panel
		slideUpPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		slideUpPanel.setPanelHeight((int) (screenHeight * 0.5));
		slideUpPanel.setDragView(listHandle);
		slideUpPanel.setCoveredFadeColor(getResources().getColor(android.R.color.transparent));
		slideUpPanel.setPanelSlideListener(new PanelSlideListener() {

			@SuppressLint("NewApi")
			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				graphView.onThreadPause();

				/*
				 * Force redraw of list while panel sliding to prevent glitches
				 */
				if(ApiGuard.apiBelow(Build.VERSION_CODES.JELLY_BEAN)){
					graphView.setVisibility(View.VISIBLE);
					
					listContainer.bringToFront();
					rootView.requestLayout();
					rootView.invalidate();
				}

				locationListView.setPadding(
						locationListView.getPaddingLeft(), 
						locationListView.getPaddingTop(), 
						locationListView.getPaddingRight(), 
						findViewById(R.id.list_container).getTop());
			}

			@Override
			public void onPanelExpanded(View panel) {
				graphView.onThreadPause();
				
				if(ApiGuard.apiBelow(Build.VERSION_CODES.JELLY_BEAN)){
					graphView.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void onPanelCollapsed(View panel) {
				graphView.onThreadPause();
			}

			@Override
			public void onPanelAnchored(View panel) {
				graphView.onThreadPause();
			}
		});

		// get result from edit text
		final RadioButton radioBtnOtherLocation = (RadioButton) findViewById(R.id.radio_otherStart);
		final RadioButton radioBtnOwnLocation = (RadioButton) findViewById(R.id.radio_ownStart);
		final EditText editText = (EditText) findViewById(R.id.auto_text);
		editText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					String addressPattern = editText.getText().toString();
					Address closestAddress = LocationFromAdress
							.getLocationFromAddress(addressPattern);
					if (closestAddress != null) {
						double lat = closestAddress.getLatitude();
						double lon = closestAddress.getLongitude();
						Location otherLocation = new Location("otherLocation");
						otherLocation.setLatitude(lat);
						otherLocation.setLongitude(lon);

						drawerLayout.closeDrawer(Gravity.RIGHT);

						mEventController.setLocation(otherLocation);
						mEventController.useOtherLocation(true);
						onEventControllerUpdate();
					} else {
						Toast.makeText(getApplicationContext(),
								getApplicationContext().getString(R.string.address_not_found),
								Toast.LENGTH_LONG).show();
						editText.setHint(R.string.text_hint);
						radioBtnOwnLocation.setChecked(true);
					}
					handled = true;
				}

				return handled;
			}

		});

		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					radioBtnOtherLocation.setChecked(true);
				}
			}
		});

		datePicker = (DatePicker) this.findViewById(R.id.datePicker1);
		okB = (Button) this.findViewById(R.id.ok_button);
		resetB = (Button) this.findViewById(R.id.reset_button);
		datePicker(null, null);

		drawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_layout);

		if(ApiGuard.apiBelow(Build.VERSION_CODES.JELLY_BEAN)){
			drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
		}

		/*
		 * Force redraw of drawer to prevent glitches
		 */
		if(ApiGuard.apiBelow(Build.VERSION_CODES.JELLY_BEAN)){
			drawerLayout.setDrawerListener(new DrawerListener() {

				@Override
				public void onDrawerStateChanged(int arg0) {

				}

				@Override
				public void onDrawerSlide(View arg0, float arg1) {
					drawerLayout.bringToFront();
					rootView.requestLayout();
					rootView.invalidate();
				}

				@Override
				public void onDrawerOpened(View arg0) {
				}

				@Override
				public void onDrawerClosed(View arg0) {
				}
			});
		}

		//		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
		//				R.drawable.ic_drawer, R.string.drawer_open,
		//				R.string.drawer_close) {
		//
		//			/** Called when a drawer has settled in a completely closed state. */
		//			public void onDrawerClosed(View view) {
		//				super.onDrawerClosed(view);
		//				// getActionBar().setTitle(mTitle);
		//				// invalidateOptionsMenu(); // creates call to
		//				// onPrepareOptionsMenu()
		//				graphView.onThreadResume();
		//			}
		//
		//			/** Called when a drawer has settled in a completely open state. */
		//			public void onDrawerOpened(View drawerView) {
		//				super.onDrawerOpened(drawerView);
		//				// getActionBar().setTitle(mDrawerTitle);
		//				// invalidateOptionsMenu(); // creates call to
		//				// onPrepareOptionsMenu()
		//				graphView.onThreadPause();
		//			}
		//		};

		// Set the drawer toggle as the DrawerListener
		//		drawerLayout.setDrawerListener(drawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			drawerLayout.closeDrawer(Gravity.RIGHT);

			if (!drawerLayout.isDrawerOpen(Gravity.LEFT))
				drawerLayout.openDrawer(Gravity.LEFT);
			else
				drawerLayout.closeDrawer(Gravity.LEFT);
			return true;

		case R.id.favorites:
			drawerLayout.closeDrawer(Gravity.LEFT);

			if (!drawerLayout.isDrawerOpen(Gravity.RIGHT))
				drawerLayout.openDrawer(Gravity.RIGHT);
			else
				drawerLayout.closeDrawer(Gravity.RIGHT);
			return true;

		default:
			return super.onOptionsItemSelected(item);

		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.combined_view, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mLocationClient != null)
			mLocationClient.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if(graphView != null)
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
		Log.v(TAG, "wrote "+json);

		if (graphView != null)
			graphView.onThreadPause();

		if (mLocationClient != null)
			mLocationClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {

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

				// save last scroll position
				int index = locationListView.getFirstVisiblePosition();
				View v = locationListView.getChildAt(0);
				int top = (v == null) ? 0 : v.getTop();

				adapter = new ExpandableListAdapter2(CombinedView.this, mEventController
						.getEventList());
				locationListView.setAdapter(adapter);
				if (mEventController.isNoEvents()) {
					adapter.setNoEvents(true);
					mEventController.setNoEvents(false);
				}

				// restore scroll position
				locationListView.setSelectionFromTop(index, top);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// hijack back button to do what we want
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (!slideUpPanel.isExpanded() && !graphView.isAtRoot()) {
				graphView.graphNavigateBack();
				return true;
			} else if (slideUpPanel.isExpanded()) {
				slideUpPanel.collapsePane();
				return true;
			} else {
				moveTaskToBack(true);
				return true;
			}
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (!drawerLayout.isDrawerOpen(Gravity.LEFT))
				drawerLayout.openDrawer(Gravity.LEFT);
			else
				drawerLayout.closeDrawer(Gravity.LEFT);
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

		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		if (fragment != null)
			getSupportFragmentManager().beginTransaction().remove(fragment)
			.commit();
	}

	@Override
	public void onExpandedItemFalse() {
		mEventController.onExpandedItemFalse();

		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		if (fragment != null)
			getSupportFragmentManager().beginTransaction().remove(fragment)
			.commit();
	}

	@Override
	public void onGraphUpdate(GenreNode node, int newHeight) {
		mEventController.setGenreNode(node);
		onEventControllerUpdate();
		slideUpPanel.animatePanelHeight(rootView.getMeasuredHeight(), (int) (newHeight + GenreGraphConstants.SCREEN_MARGIN_FACTOR * screenWidth * 3));
	}

	@Override
	public void onShareEvent(Event event) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TITLE, event.eventName);
		intent.putExtra(Intent.EXTRA_TEXT, "Ich gehe heute Abend zu "
				+ event.eventName + " ins " + event.locationName + ". Lust? (;");
		startActivity(Intent.createChooser(intent, "Share with..."));
	}

	@Override
	public void scrollEventTop(View listItem) {
		if (listItem != null) {
			locationListView.smoothScrollToPositionFromTop(
					(Integer) listItem.getTag(), 0);
		}
	}

	@Override
	public void attachMap(Event event) {
		if (event != null) {
			Bundle args = new Bundle();
			args.putDouble("lat", Double.parseDouble(event.locationLatitude));
			args.putDouble("lon", Double.parseDouble(event.locationLongitude));

			MapFragment mapFragment = new MapFragment();
			mapFragment.setArguments(args);
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.map_container, mapFragment).commit();
		}
	}

	@Override
	public void onEventControllerFinished() {
		updateFavoriteList();
	}

	/**
	 * Update Favorite List and attach next event to favorite
	 */
	private void updateFavoriteList() {
		LinkedList<FavoriteLocation> favLocations = new LinkedList<FavoriteLocation>();
		List<Event> eventList = mEventController.getCompleteEventList();

		/*
		 * iterate over all favorites and all events if we find an event that
		 * takes place earlirer, save it we want to have the events for the
		 * location that takes place next
		 */
		for (Entry<String, FavoriteLocation> entry : mEventController
				.getFavorites().entrySet()) {
			Event nextEvent = null;

			for (Event event : eventList) {
				String favoriteId = entry.getKey();

				if (event.locationID.equals(favoriteId)) {
					if (nextEvent == null) {
						nextEvent = event;
					} else {
						if (Long.parseLong(nextEvent.startTime) >= Long
								.parseLong(event.startTime)) {
							nextEvent = event;
						}
					}
				}
			}
			FavoriteLocation favLocation = entry.getValue();
			favLocation.setNextEvent(nextEvent);
			favLocations.add(favLocation);
		}

		mListFavorites.setAdapter(new ExpandableFavoriteListAdapter(this,
				favLocations, this));
	}

	@Override
	public void onFavoriteDelete(final String favoriteId) {

		// Dialog for ICE CREAM SANDWICH
		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.dialog_title_delete_favorite)
			.setPositiveButton(R.string.dialog_positiv_delete_favorite,
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					onRemoveFavorites(favoriteId);
				}

			})
			.setNegativeButton(
					R.string.dialog_negative_delete_favorite, null)
					.show();

		} else {
			Bundle restoreToken = new Bundle();
			restoreToken.putString("id", favoriteId);

			mUndoBarController.showUndoBar(false,
					getString(R.string.undobar_sample_message), restoreToken);

			onRemoveFavorites(favoriteId);

			Log.d(TAG, "Removed favorite from list");
		}
	}

	@Override
	public void onUndo(Bundle token) {
		if (token != null) {
			String favoriteId = token.getString("id");
			onAddFavorites(favoriteId);
			updateFavoriteList();

			Log.d(TAG, "Remove favorite from list undo");
		}
	}

	/**
	 * set the color of the date picker dividers
	 * 
	 * @param listener
	 * @param calendar
	 * @return
	 */
	public DatePicker datePicker(OnDateSetListener listener, Calendar calendar) {
		okB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.v(TAG,
						datePicker.getDayOfMonth() + " "
								+ (datePicker.getMonth() + 1) + " "
								+ datePicker.getYear());
				String dateTime = datePicker.getYear() + "-"
						+ (datePicker.getMonth() + 1) + "-"
						+ datePicker.getDayOfMonth() + "T" + "00" + ":" + "00"
						+ ":00.000";
				DateTime time = DateTime.parse(dateTime);
				mEventController.setDateTime(time);
				mEventController.useAlternativeTime(true);
				onEventControllerUpdate();
				drawerLayout.closeDrawer(Gravity.LEFT);
			}
		});

		resetB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mEventController.useAlternativeTime(false);
				onEventControllerUpdate();
				drawerLayout.closeDrawer(Gravity.LEFT);
			}
		});

		LinearLayout llFirst = (LinearLayout) datePicker.getChildAt(0);
		LinearLayout llSecond = (LinearLayout) llFirst.getChildAt(0);
		for (int i = 0; i < llSecond.getChildCount(); i++) {
			NumberPicker picker = (NumberPicker) llSecond.getChildAt(i); // Numberpickers
			Field[] pickerFields = NumberPicker.class.getDeclaredFields();
			for (Field pf : pickerFields) {
				if (pf.getName().equals("mSelectionDivider")) {
					pf.setAccessible(true);
					try {
						pf.set(picker,
								getResources().getDrawable(
										R.drawable.date_picker_shape));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (NotFoundException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}

		return datePicker;
	}

	@Override
	public void onFavoriteClick(FavoriteLocation favoriteLocation) {
		Event event = null;
		int position = 0;

		if(adapter != null && favoriteLocation.nextEvent != null){
			for (int i = 0; i < adapter.getGroupCount(); i++) {
				Event iEvent = (Event) adapter.getGroup(i);

				if(iEvent.startTime.equals(favoriteLocation.nextEvent.startTime)){
					event = iEvent;
					position = i;
					break;
				}
			}
			
			if(event != null){
				drawerLayout.closeDrawer(findViewById(R.id.right_drawer));
				slideUpPanel.expandPane();

				for (int i = 0; i < adapter.getGroupCount(); i++) {
					locationListView.collapseGroup(i);
				}

				locationListView.smoothScrollToPositionFromTop(
						position, 0);
				locationListView.expandGroup(position);
			}
		}
	}
}
