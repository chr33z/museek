package de.mimuc.pem_music_graph;

import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.mimuc.pem_music_graph.list.Event;
import de.mimuc.pem_music_graph.utils.ApplicationController;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MapFragment extends Fragment {

	private GoogleMap map;

	LatLng mEventLocation = new LatLng(0, 0);

	private static final int ZOOM = 15;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.map, null, false);

		if(getArguments() != null){
			mEventLocation = new LatLng(
					getArguments().getDouble("lat"),
					getArguments().getDouble("lon"));
		}

		map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		map.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng arg0) {
				openMap(mEventLocation);
			}
		});
		
		map.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			public boolean onMarkerClick(Marker marker) {
				openMap(mEventLocation);
				return true;
			}
		});

		UiSettings uiSettings = map.getUiSettings();
		uiSettings.setAllGesturesEnabled(false);
		uiSettings.setZoomControlsEnabled(false);

		setMarker();

		return v;
	}

	@Override
	public void onResume(){
		super.onResume();

	}

	public void setLocation(LatLng location){
		this.mEventLocation = location;
		setMarker();
	}

	private void setMarker(){
		if(map != null){
			if(mEventLocation != null){
				Marker event = map.addMarker(new MarkerOptions().position(mEventLocation)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(
						new LatLng(mEventLocation.latitude+0.0003, mEventLocation.longitude), ZOOM));
			}
		}
	}

	public void onDetach(){
		super.onDetach();
	}

	private void openMap(LatLng eventLocation) {
		String uri = String.format(Locale.ENGLISH,
				"http://maps.google.com/maps?&daddr=%f,%f (%s)", 
				eventLocation.latitude, eventLocation.longitude,
				"Where the party is");
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClassName("com.google.android.apps.maps",
				"com.google.android.maps.MapsActivity");
		try {
			ApplicationController.getInstance().startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			try {
				Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(uri));
				unrestrictedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ApplicationController.getInstance().startActivity(unrestrictedIntent);
			} catch (ActivityNotFoundException innerEx) {
				Toast.makeText(ApplicationController.getInstance(), "Please install a maps application",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
