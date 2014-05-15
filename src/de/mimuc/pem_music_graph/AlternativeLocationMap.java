package de.mimuc.pem_music_graph;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.mimuc.pem_music_graph.utils.Constants;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * Activity to choose alternative location to load events for
 * @author Christopher Gebhardt
 *
 */
public class AlternativeLocationMap extends FragmentActivity {

	private static final String TAG = AlternativeLocationMap.class.getSimpleName();

	private static final float CAMERA_ZOOM_MAX = 12f;
	
	private GoogleMap mMap;

	private Marker mMarker = null;

	private LocationManager mLocationManager;

	private Location mLocation = null;

	private OnClickListener buttonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_cancel:
				setResult(RESULT_CANCELED, null);
				finish();
				break;
			case R.id.button_confirm:
				if(mMarker != null){
					Intent result = new Intent();
					result.putExtra(Constants.LATITUDE, mMarker.getPosition().latitude);
					result.putExtra(Constants.LONGITUDE, mMarker.getPosition().longitude);
					setResult(RESULT_OK, result);
					finish();
				} else {
					Toast.makeText(getBaseContext(), R.string.toast_alternative_location_missing_marker, Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alternative_location_map);

		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		mLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		mMap.getUiSettings().setCompassEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(true);
		mMap.getUiSettings().setMyLocationButtonEnabled(true);

		mMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng location) {
				setMarker(location);
			}
		});

		mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

			@Override
			public void onMarkerDragStart(Marker arg0) { }

			@Override
			public void onMarkerDragEnd(Marker marker) {
				Log.i(TAG, "Dragged marker on "+marker.getPosition().toString());
			}

			@Override
			public void onMarkerDrag(Marker arg0) { }
		});

		findViewById(R.id.button_cancel).setOnClickListener(buttonListener);
		findViewById(R.id.button_confirm).setOnClickListener(buttonListener);

		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		mLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		if(mLocation != null){
			setMarker(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alternative_location_map, menu);
		return true;
	}

	private void setMarker(LatLng location) {
		if(mMarker == null){
			mMarker = mMap.addMarker(new MarkerOptions()
			.position(location)
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)
					));
			mMarker.setDraggable(true);
		} else {
			mMarker.setPosition(location);
		}
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, CAMERA_ZOOM_MAX));
	}
}
