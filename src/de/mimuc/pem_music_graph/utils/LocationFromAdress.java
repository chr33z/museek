package de.mimuc.pem_music_graph.utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class LocationFromAdress {

	public static Address getLocationFromAddress(String addressPattern, Location currentLocation){
		Context context = ApplicationController.getInstance();

		LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		List<Address> addresses = null;

		Address closest = null;
		float closestDistance = Float.MAX_VALUE;
		// look for address, closest to our location
		
		if(currentLocation != null){
			for (Address adr : addresses) {
				if (closest == null) {
					closest = adr;
				} else {
					float[] result = new float[1];
					Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), adr.getLatitude(), adr.getLongitude(), result);
					float distance = result[0];
					if (distance < closestDistance) {
						closest = adr;
						closestDistance = distance;
					}
				}
			}
		} else {
			closest = (addresses.size() > 1) ? addresses.get(0) : null;
		}
		return closest; //here can be null if we did not find any addresses by search pattern.
	}
}
