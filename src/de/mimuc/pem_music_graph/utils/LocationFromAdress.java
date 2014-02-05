package de.mimuc.pem_music_graph.utils;

import java.io.IOException;
import java.util.List;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

public class LocationFromAdress {

	public static Address getLocationFromAddress(String addressPattern){
		Context context = ApplicationController.getInstance();

		LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		//I use last known location, but here we can get real location
		Location lastKnownLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		List<Address> addresses = null;
		try {
			//trying to get all possible addresses by search pattern
			addresses = (new Geocoder(context)).getFromLocationName(addressPattern, Integer.MAX_VALUE);
		} catch (IOException e) {
		}
		if (addresses == null) {
			// location service unavailable or incorrect address
			// so returns null
			return null;
		}

		Address closest = null;
		float closestDistance = Float.MAX_VALUE;
		// look for address, closest to our location
		
		if(lastKnownLocation != null){
			for (Address adr : addresses) {
				if (closest == null) {
					closest = adr;
				} else {
					float[] result = new float[1];
					Location.distanceBetween(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), adr.getLatitude(), adr.getLongitude(), result);
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
