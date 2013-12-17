package de.mimuc.pem_music_graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import de.mimuc.pem_music_graph.utils.ApplicationController;
import de.mimuc.pem_music_graph.utils.JsonConstants;
import de.mimuc.pem_music_graph.utils.LocationControllerListener;

public class LocationController implements JsonConstants {

	private static final String TAG = LocationController.class.getSimpleName();

	public List<EventLocation> eventLocationList;
	
	private LocationControllerListener callbackReceiver;

	public LocationController(LocationControllerListener callbackReceiver) {
		this.callbackReceiver = callbackReceiver;
		this.eventLocationList = new ArrayList<EventLocation>();
	}

	/**
	 * stellt eine Anfrage an den Server, um die aktuelle Liste der
	 * EventLocations in einem bestimmten Radius abzufragen
	 */
	public void updateLocation(Location location) {
		Log.d(TAG, "Try to retrieve locations from server...");
		
		// Json f�r POST Request
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(TAG_LATITUDE, location.getLatitude() + "");
		params.put(TAG_LONGITUDE, location.getLongitude() + "");
		params.put("radius", "1000000"); // radius in m
		
		// POST request
		JsonObjectRequest req = new JsonObjectRequest(
				ApplicationController.URL_POST_FIND_BY_LOCATION,
				new JSONObject(params), new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d(TAG, response.toString());
						readJson(response);
						Log.i(TAG, "...success!");
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if(error.getMessage() == null){
							Log.e(TAG, "...could not retrieve location or a meaningfull error...");
							return;
						}
						VolleyLog.e("Error: ", error.getMessage());
						Log.w(TAG, "...could not retrieve locations!");
					}
				});

		ApplicationController.getInstance().addToRequestQueue(req);
		
		callbackReceiver.onLocationControllerUpdate();
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
			eventLocationList.clear();
			
			// eventLocations are stored in an array
			JSONArray jsonArray = json
					.getJSONArray(JsonConstants.TAG_LOCATIONS);

			for (int i = 0; i < jsonArray.length(); i++) {
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
				String furtherInformation = "";
				latitude = eventLocation.getString(JsonConstants.TAG_LATITUDE);
				longitude = eventLocation
						.getString(JsonConstants.TAG_LONGITUDE);

				EventLocation newEventLocation = new EventLocation(name, genre,
						subgenre, street, housenumber, city, postcode,
						phonenumber, emailAddress, openingHours,
						ageRestriction, furtherInformation, latitude, longitude);
				
				Log.d(TAG, newEventLocation.name);
				eventLocationList.add(newEventLocation);
				Log.d(TAG, eventLocationList.size()+"");
			}
		} catch (JSONException error) {
			Log.e(TAG, error.getMessage());
		}
	}

	/**
	 * Getter f�r die aktuelle Liste der EventLocations
	 * 
	 * @return List<EventLocation>
	 */
	public List<EventLocation> getEventLocationList() {
		Log.v(TAG, eventLocationList.size() + "");
		return eventLocationList;
	}
}
