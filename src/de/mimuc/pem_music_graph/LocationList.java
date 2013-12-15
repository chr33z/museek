package de.mimuc.pem_music_graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import de.mimuc.pem_music_graph.utils.ApplicationController;

import android.util.Log;

public class LocationList {

	private static final String TAG = LocationList.class.getName();

	public List<Location> locations = new ArrayList<Location>();

	// TODO Informationen vom Server auslesen und in Location speichern
	// TODO neue Location zu Map hinzufügen

	public void newLocation() {
//		startRequest();
		Location a = new Location("089", "all", "", "Maximiliansplatz", 5,
				"München", 80333, "089 57004959", "info@089-bar.de",
				"21:00 - 05:00", "ab 18", "", "", "");
		locations.add(a);
	}

	public List<Location> getLocations() {
		newLocation();
		Log.v(TAG, locations.get(0).toString());
		return locations;
	}

	public void startRequest() {
		/*
		 * Locations um einen bestimmten Ort herum finden. Dazu müssen die
		 * Koordinaten und ein Radius angegeben werden
		 */

		// Json für POST Request
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("latitude", "48.1231334245");
		params.put("longitude", "11.0240708323");
		// radius in Metern - optional (ansonsten wird standard von 10000m
		// verwendet
		params.put("radius", "10000");

		// POST request
		JsonObjectRequest req = new JsonObjectRequest(
				ApplicationController.URL_POST_FIND_BY_LOCATION,
				new JSONObject(params), new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// Angekommene Daten hier verarbeiten
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, error.getMessage());
						VolleyLog.e("Error: ", error.getMessage());
					}
				});

		// add the request object to the queue to be executed
		ApplicationController.getInstance().addToRequestQueue(req);

//		try {
//			// locations are stored in an array
//			JSONArray locations2 = json
//					.getJSONArray(JsonConstants.TAG_LOCATIONS);
//
//			for (int i = 0; i < 1; i++) {
//				JSONObject location = locations2.getJSONObject(i);
//
//				String name = location.getString(JsonConstants.TAG_NAME);
//
//				// Address is an extra JSONObject
//				JSONObject address = location
//						.getJSONObject(JsonConstants.TAG_ADDRESS);
//				String genre;
//				String subgenre;
//				String street = address.getString(JsonConstants.TAG_STREET);
//				int housenumber = address.getInt(JsonConstants.TAG_HOUSENUMBER);
//				String city = address.getString(JsonConstants.TAG_CITY);
//				int postcode = address.getInt(JsonConstants.TAG_POSTCODE);
//				
//				String phonenumber;
//				String emailAddress;
//				String openingHours;
//				String ageRestriction;
//				String furtherInformation;
//				String latitude = location
//						.getString(JsonConstants.TAG_LATITUDE);
//				String longitude = location
//						.getString(JsonConstants.TAG_LONGITUDE);
//				
//				Location newLocation = new Location(name, genre, subgenre, street,
//						housenumber, city, postcode, phonenumber, emailAddress,
//						openingHours, ageRestriction, furtherInformation,
//						latitude, longitude);
//				locations.add(newLocation);
//			}
//		} catch (JSONException error) {
//			Log.e(TAG, error.getMessage());
//		}

	}

}