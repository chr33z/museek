package de.mimuc.pem_music_graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import de.mimuc.pem_music_graph.utils.ApplicationController;
import de.mimuc.pem_music_graph.utils.JsonConstants;

public class LocationController {

	private static final String TAG = LocationController.class.getName();

	public List<EventLocation> eventLocationList;

	public LocationController() {
		super();
		this.eventLocationList = new ArrayList<EventLocation>();
	}

	/**
	 * stellt eine Anfrage an den Server, um die aktuelle Liste der
	 * EventLocations in einem bestimmten Radius abzufragen
	 */
	public void updateLocation(LatLng currentPoint) {
		/*
		 * Locations um einen bestimmten Ort herum finden. Dazu müssen die
		 * Koordinaten und ein Radius angegeben werden
		 */

		// Json für POST Request
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("latitude", currentPoint.latitude + "");
		params.put("longitude", currentPoint.longitude + "");
		// radius in Metern - optional (ansonsten wird standard von 10000m
		// verwendet
		params.put("radius", "1000000");

		// POST request
		JsonObjectRequest req = new JsonObjectRequest(
				ApplicationController.URL_POST_FIND_BY_LOCATION,
				new JSONObject(params), new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						readJson(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
//						 Log.e(TAG, error.getMessage());
						 VolleyLog.e("Error: ", error.getMessage());
					}
				});

		// add the request object to the queue to be executed
		ApplicationController.getInstance().addToRequestQueue(req);
	}

	/**
	 * liest json aus und speichert die Elemente als neue EventLocation
	 * 
	 * @param json
	 */
	protected void readJson(JSONObject json) {
		String name = null;
		String description = " --- ";
		String street = null;
		String housenumber = null;
		String city = null;
		String postcode = null;
		String latitude = null;
		String longitude = null;

		try {
			// eventLocations are stored in an array
			JSONArray jsonArray = json
					.getJSONArray(JsonConstants.TAG_LOCATIONS);

			for (int i = 0; i < 1; i++) {
				JSONObject eventLocation = jsonArray.getJSONObject(i);

				name = eventLocation.getString(JsonConstants.TAG_NAME);

				// Address is an extra JSONObject
				JSONObject address = eventLocation
						.getJSONObject(JsonConstants.TAG_ADDRESS);
				String genre = "";
				String subgenre = "";
				street = address.getString(JsonConstants.TAG_STREET);
				housenumber = address.getString(JsonConstants.TAG_HOUSENUMBER);
				city = address.getString(JsonConstants.TAG_CITY);
				postcode = address.getString(JsonConstants.TAG_POSTCODE);

				String phonenumber = "";
				String emailAddress = "";
				String openingHours = "";
				String ageRestriction = "";
				String furtherInformation = eventLocation
						.getString(JsonConstants.TAG_DESCRIPTION);
				latitude = eventLocation.getString(JsonConstants.TAG_LATITUDE);
				longitude = eventLocation
						.getString(JsonConstants.TAG_LONGITUDE);

				EventLocation newEventLocation = new EventLocation(name, genre,
						subgenre, street, housenumber, city, postcode,
						phonenumber, emailAddress, openingHours,
						ageRestriction, furtherInformation, latitude, longitude);
				eventLocationList.add(newEventLocation);
			}
		} catch (JSONException error) {
			Log.e(TAG, error.getMessage());
		}
	}

	/**
	 * Getter für die aktuelle Liste der EventLocations
	 * 
	 * @return List<EventLocation>
	 */
	public List<EventLocation> getEventLocationList() {
		Log.v(TAG, eventLocationList.size() + "");
		return eventLocationList;
	}
}
