package de.mimuc.pem_music_graph.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class EventController implements JsonConstants {

	private static final String TAG = EventController.class.getSimpleName();

	public Map<String, Event> eventList;

	private EventControllerListener callbackReceiver;

	private Location currentLocation;

	private JSONObject jsonForSharedPreferences;
	private List<String> favorites;
	private List<String> expandedItems;

	public EventController(EventControllerListener callbackReceiver) {
		this.callbackReceiver = callbackReceiver;
		this.eventList = new HashMap<String, Event>();
		this.favorites = new ArrayList<String>();
		this.expandedItems = new ArrayList<String>();
	}

	public EventController(EventControllerListener callbackReceiver,
			JSONObject json) {
		this.callbackReceiver = callbackReceiver;
		this.eventList = new HashMap<String, Event>();
		favorites = new ArrayList<String>();
	}

	public EventController(EventControllerListener callbackReceiver,
			JSONObject json, Location location) {
		this.callbackReceiver = callbackReceiver;
		this.eventList = new HashMap<String, Event>();
		this.currentLocation = location;
		favorites = new ArrayList<String>();
		readJson(json);
	}

	public void setLocation(Location location) {
		currentLocation = location;
	}

	/**
	 * stellt eine Anfrage an den Server, um die aktuelle Liste der
	 * EventLocations in einem bestimmten Radius abzufragen
	 */
	public void updateEvents(Location location) {
		Log.d(TAG, "Try to retrieve locations from server...");

		currentLocation = location;

		// Json fuer POST Request
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(TAG_LATITUDE, location.getLatitude() + "");
		params.put(TAG_LONGITUDE, location.getLongitude() + "");
		params.put("radius", "10000000000"); // radius in m

		// POST request
		JsonObjectRequest req = new JsonObjectRequest(
				ApplicationController.URL_POST_FIND_BY_LOCATION,
				new JSONObject(params), new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// Log.d(TAG, response.toString());
						readJson(response);
						setJsonForSharedPreferences(response);
						Log.i(TAG, "...success!");
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (error.getMessage() == null) {
							Log.e(TAG,
									"...could not retrieve location or a meaningfull error...");
							return;
						}
						VolleyLog.e("Error: ", error.getMessage());
						Log.w(TAG, "...could not retrieve locations!");
					}
				});

		ApplicationController.getInstance().addToRequestQueue(req);

		callbackReceiver.onEventControllerUpdate();
	}

	/**
	 * liest json aus und speichert die Elemente als neue EventLocation
	 * 
	 * @param json
	 */
	protected void readJson(JSONObject json) {
		String resultTime = null;
		String resultRadius = null;
		String resultLatitude = null;
		String resultLongitude = null;

		String eventName = null;
		String eventGenre = null;
		String eventDescription = null;
		String startTime = null;
		String endTime = null;
		String minAge = null;
		String specialOffer = null;

		String locationID = null;
		String locationName = null;
		String locationLatitude = null;
		String locationLongitude = null;
		String locationDescription = null;
		String addressStreet = null;
		String addressNumber = null;
		String addressCity = null;
		String addressPostcode = null;
		String locationWebsite = null;
		boolean isFavorite = false;
		boolean isExpanded = false;

		try {
			eventList.clear();

			// eventLocations are stored in an array
			JSONArray jsonArray = json.getJSONArray("events");

			for (int i = 0; i < jsonArray.length(); i++) {
				Log.v("anzahl in for", i+"");
				JSONObject event = jsonArray.getJSONObject(i);

				resultTime = json.getString(TAG_RESULT_TIME);
				resultRadius = json.getString(TAG_RESULT_RADIUS);
				resultLatitude = json.getString(TAG_RESULT_LATITUDE);
				resultLongitude = json.getString(TAG_RESULT_LONGITUDE);

				eventName = event.getString(TAG_EVENT_NAME);
				eventGenre = event.getString(TAG_EVENT_GENRES);
				eventDescription = event.getString(TAG_EVENT_DESCRIPTION);
				startTime = event.getString(TAG_EVENT_START_TIME);
				endTime = event.getString(TAG_EVENT_END_TIME);
				minAge = event.getString(TAG_EVENT_MIN_AGE);
				specialOffer = event.getString(TAG_EVENT_SPECIAL_OFFER);

				locationID = event.getString(TAG_LOCATION_ID);
				locationName = event.getString(TAG_LOCATION_NAME);
				locationLatitude = event.getString(TAG_LOCATION_LATITUDE);
				locationLongitude = event
						.getString(JsonConstants.TAG_LOCATION_LONGITUDE);
				locationDescription = event.getString(TAG_LOCATION_DESCRIPTION);
				addressStreet = event.getString(TAG_LOCATION_ADDRESS_STREET);
				addressNumber = event.getString(TAG_LOCATION_ADDRESS_NUMBER);
				addressCity = event.getString(TAG_LOCATION_ADDRESS_CITY);
				addressPostcode = event
						.getString(TAG_LOCATION_ADDRESS_POSTCODE);
				locationWebsite = event.getString(TAG_LOCATION_WEBSITE);

				isFavorite = false;
				// schaut bei jedem Mal, wenn die Liste neu geladen wird, ob der
				// aktuelle Eintrag schon zu den Favoriten hinzugefuegt wurde
				for (int j = 0; j < favorites.size(); j++) {
					Log.v("favoritesEqualsID", favorites.get(j).equals(locationID)+"");
					if (favorites.get(j).equals(locationID)){
						Log.v("readJson1", isFavorite + " " + locationID);
						isFavorite = true;
						Log.v("readJson2", isFavorite + " " + locationID);
					}
				}

				isExpanded = false;
				if (expandedItems != null) {
					for (int j = 0; j < expandedItems.size(); j++) {
						if (expandedItems.get(j).equals(locationID)) {
							isExpanded = true;
//							Log.v("expandedItem", isExpanded + " " + locationID);
						}
					}
				}

				Event newEvent = new Event(resultTime, resultRadius,
						resultLatitude, resultLongitude, eventName, eventGenre,
						eventDescription, startTime, endTime, minAge,
						specialOffer, locationID, locationName,
						locationLatitude, locationLongitude,
						locationDescription, addressStreet, addressNumber,
						addressCity, addressPostcode, locationWebsite,
						currentLocation, isFavorite, isExpanded);

				// TODO boolean fuer isFavorite aus SharedPreferences auslesen
				// und setzen
				// Log.d(TAG, newEvent.eventName);
				eventList.put(locationID, newEvent);
			}
			Log.v("eventListSizeController", eventList.size()+"");
		} catch (JSONException error) {
			Log.e(TAG, error.getMessage());
		}
	}

	/**
	 * Getter fuer die aktuelle Liste der EventLocations
	 * 
	 * @return List<EventLocation>
	 */
	public Map<String, Event> getEventList() {
		updateFavorites();
		// Log.v(TAG, eventList.size() + "");
		return eventList;
	}

	public JSONObject getJsonForSharedPreferences() {
		return jsonForSharedPreferences;
	}

	public void setJsonForSharedPreferences(JSONObject jsonForSharedPreferences) {
		this.jsonForSharedPreferences = jsonForSharedPreferences;
	}

	/**
	 * aktualisiert den Wert von isFavorite, wenn eine Location zu Favoriten
	 * hinzugefuegt wurde und updatet
	 * 
	 * @param locationID
	 */
	public void onAddFavorites(String locationID) {
		favorites.add(locationID);
		eventList.get(locationID).isFavorite = true;
		callbackReceiver.onEventControllerUpdate();

		// TODO als Map speichern und im Adapter nur als ArrayList
		// for (int i = 0; i < eventList.size(); i++) {
		// if(eventList.get(i).locationID.equals(locationID))
		// eventList.get(i).isFavorite = true;
		// }
	}

	/**
	 * entfernt die LocationID aus Favoriten, wenn eine Location aus den
	 * Favoriten entfernt wurde und updatet
	 * 
	 * @param locationID
	 */
	public void onRemoveFavorites(String locationID) {
		for (int i = 0; i < favorites.size(); i++) {
			if (favorites.get(i).equals(locationID)) {
				favorites.remove(i);
				eventList.get(locationID).isFavorite = false;
			}
			callbackReceiver.onEventControllerUpdate();
		}
	}

	public List<String> getFavorites() {
		return favorites;
	}

	public void updateFavorites() {
		for (int i = 0; i < favorites.size(); i++) {
			if (eventList.get(favorites.get(i)) != null) {
				Log.v("before", eventList.get(favorites.get(i)) + " "
						+ eventList.get(favorites.get(i)).isFavorite);
				eventList.get(favorites.get(i)).isFavorite = true;
//				Log.v("updateFavorites", eventList.toString());
			}
		}
	}

	public void writeInFavorites(String fav) {
//		Log.v("fav", fav);
		fav = fav.replaceAll("\\s+", "");
		String lF = fav.substring(1, fav.length() - 1);
//		Log.v("fav2", lF);
		String[] singleFavorites = lF.split(",");
//		Log.v("woiehweohii", singleFavorites.length + "");
//		Log.v("Eintrag1", singleFavorites[0]);
//		Log.v("Eintrag2", singleFavorites[1]);
		for (int i = 0; i < singleFavorites.length; i++) {
			this.favorites.add(singleFavorites[i]);
//			Log.v("split", singleFavorites[i]);
		}
//		Log.v("woiehweohii", singleFavorites.length + "");
		updateFavorites();
//		Log.v("writeInFavorites", favorites.toString());
	}

	public void onExpandedItem(String locationID, boolean b) {
		if (expandedItems != null) {
			for (int i = 0; i < expandedItems.size(); i++) {
				if (eventList.get(expandedItems.get(i)) != null) {
					eventList.get(expandedItems.get(i)).isExpanded = b;
					// Log.v("onExpandedItem",
					// b + "" + eventList.get(expandedItems.get(i)));
				}
			}
		}
	}
}
