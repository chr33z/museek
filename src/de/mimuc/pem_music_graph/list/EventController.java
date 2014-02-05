package de.mimuc.pem_music_graph.list;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import de.mimuc.pem_music_graph.favorite_list.FavoriteLocation;
import de.mimuc.pem_music_graph.graph.GenreNode;
import de.mimuc.pem_music_graph.utils.ApplicationController;
import de.mimuc.pem_music_graph.utils.JsonConstants;

public class EventController implements JsonConstants {

	private static final String TAG = EventController.class.getSimpleName();

	/**
	 * Save events in locationId-Event pairs
	 */
	public Map<String, Event> eventList;

	/**
	 * The activity that handles all callbacks (parent Activity)
	 */
	private EventControllerListener callbackReceiver;

	/**
	 * The last known location
	 */
	private Location currentLocation;

	/**
	 * save last event list in shared preferences
	 */
	private JSONObject jsonForSharedPreferences;

	/**
	 * save favorites in locationId-FavoriteLocation pairs
	 */
	private Map<String, FavoriteLocation> favorites;

	/**
	 * Keep track of expanded views to reset them on a adapter reload
	 */
	private String expandedItem = "";

	/**
	 * stores the current genrenode
	 */
	private GenreNode genreNode;

	/**
	 * is true if the eventlist for a genre has no items
	 */
	private boolean noEvents;

	/**
	 * initialize event controller constructor if no connection to the internet
	 * and no json available in sharedpreferences
	 * 
	 * @param callbackReceiver
	 */
	public EventController(EventControllerListener callbackReceiver) {
		this.callbackReceiver = callbackReceiver;
		this.eventList = new HashMap<String, Event>();
		this.favorites = new HashMap<String, FavoriteLocation>();
	}

	/**
	 * initialize event controller when we already have json data and location
	 * constructor if no connection to the internet and json available in
	 * sharedpreferences
	 * 
	 * @param callbackReceiver
	 * @param json
	 */
	public EventController(EventControllerListener callbackReceiver,
			JSONObject json) {
		this.callbackReceiver = callbackReceiver;
		this.eventList = new HashMap<String, Event>();
		this.favorites = new HashMap<String, FavoriteLocation>();
		readJson(json);
	}

	/**
	 * constructor
	 * 
	 * @param callbackReceiver
	 * @param json
	 * @param location
	 */
	public EventController(EventControllerListener callbackReceiver,
			JSONObject json, Location location) {
		this.callbackReceiver = callbackReceiver;
		this.eventList = new HashMap<String, Event>();
		this.currentLocation = location;
		this.favorites = new HashMap<String, FavoriteLocation>();
		readJson(json);
		setJsonForSharedPreferences(json);
	}

	/**
	 * puts a question to the server to get the current list of events with a
	 * certain radius
	 * 
	 * @param location
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
	 * reads the json and stores the elements as new event
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

		float currentDistance = 0;
		boolean isFavorite = false;
		boolean isExpanded = false;

		try {
			eventList.clear();

			// eventLocations are stored in an array
			JSONArray jsonArray = json.getJSONArray("events");

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject event = jsonArray.getJSONObject(i);

				resultTime = json.getString(TAG_RESULT_TIME);
				resultRadius = json.getString(TAG_RESULT_RADIUS);
				resultLatitude = json.getString(TAG_RESULT_LATITUDE);
				resultLongitude = json.getString(TAG_RESULT_LONGITUDE);

				eventName = event.getString(TAG_EVENT_NAME);
				eventGenre = event.getString(TAG_EVENT_GENRES);
				eventGenre = (eventGenre.equals("") || eventGenre.equals("")) ? "music"
						: eventGenre;

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
				// looks if the current entry is part of favorites
				// and sets isFavorite on true if it is
				for (Map.Entry<String, FavoriteLocation> entry : favorites
						.entrySet()) {
					if (eventList.get(entry.getKey()) != null) {
						if (eventList.get(entry.getKey()).equals(locationID)) {
							isFavorite = true;
						}
					}
				}

				isExpanded = false;
				if (locationID.equals(expandedItem)) {
					isExpanded = true;
				}

				Event newEvent = new Event(resultTime, resultRadius,
						resultLatitude, resultLongitude, eventName, eventGenre,
						eventDescription, startTime, endTime, minAge,
						specialOffer, locationID, locationName,
						locationLatitude, locationLongitude,
						locationDescription, addressStreet, addressNumber,
						addressCity, addressPostcode, locationWebsite,
						currentLocation, currentDistance, isFavorite,
						isExpanded);

				eventList.put(locationID, newEvent);
			}
			Log.v(TAG, "eventListSizeController " + eventList.size() + "");
		} catch (JSONException error) {
			Log.e(TAG, error.getMessage());
		}
	}

	/**
	 * updates the value of isFavorite when a new location was marked as
	 * favorite; it updates the list of events
	 * 
	 * @param locationID
	 */
	public void onAddFavorites(String locationID) {
		FavoriteLocation fLocation = new FavoriteLocation(locationID,
				eventList.get(locationID).locationName,
				eventList.get(locationID).locationLatitude,
				eventList.get(locationID).locationLongitude,
				eventList.get(locationID).locationDescription,
				eventList.get(locationID).addressStreet,
				eventList.get(locationID).addressNumber,
				eventList.get(locationID).addressCity,
				eventList.get(locationID).addressPostcode,
				eventList.get(locationID).locationWebsite);

		favorites.put(locationID, fLocation);
		eventList.get(locationID).isFavorite = true;
		callbackReceiver.onEventControllerUpdate();
	}

	/**
	 * removes the locationID from favorites when the location was removed from
	 * favorites; it updates the list
	 * 
	 * @param locationID
	 */
	public void onRemoveFavorites(String locationID) {

		if (favorites.containsKey(locationID)) {
			if (eventList.get(locationID) != null) {
				eventList.get(locationID).isFavorite = false;
				favorites.remove(locationID);
			}
		}
		callbackReceiver.onEventControllerUpdate();
	}

	/**
	 * updates the entries of the favorite-value of the eventlist
	 */
	public void updateFavorites() {
		for (Map.Entry<String, FavoriteLocation> entry : favorites.entrySet()) {
			Log.v("updateFavorites", entry.toString());
			if (eventList.get(entry.getKey()) != null) {
				eventList.get(entry.getKey()).isFavorite = true;
			}
		}
	}

	/**
	 * stores the locationID of expanded item, sets it in the eventList on true
	 * and sets the previous on false
	 * 
	 * @param locationID
	 */
	public void onExpandedItemTrue(String locationID) {
		Log.v("expandedItemsnull", expandedItem + "");
		if (!(expandedItem.equals("")))
			eventList.get(expandedItem).isExpanded = false;
		expandedItem = locationID;
		eventList.get(expandedItem).isExpanded = true;
	}

	/**
	 * sets the collapsed item in the eventList on false and stores an empty
	 * string as expanded item
	 */
	public void onExpandedItemFalse() {
		eventList.get(expandedItem).isExpanded = false;
		expandedItem = "";
	}

	/**
	 * calculates the distance from the currentDistance to the destination and
	 * stores it in every event
	 * 
	 * @param eL
	 * @return
	 */
	public Map<String, Event> storeDistance(Map<String, Event> eL) {
		Map<String, Event> map = new HashMap<String, Event>();
		Event currentEvent;
		float distance;
		for (Map.Entry<String, Event> entry : eL.entrySet()) {
			currentEvent = entry.getValue();
			
			Location destination = new Location("destination");
			destination.setLatitude(Double
					.parseDouble(currentEvent.locationLatitude));
			destination.setLongitude(Double
					.parseDouble(currentEvent.locationLongitude));
			
			Location currentLocation = currentEvent.currentLocation;
			distance = currentLocation.distanceTo(destination);
			entry.getValue().currentDistance = distance;
			map.put(entry.getKey(), currentEvent);
		}
		return map;
	}

	/**
	 * reads first the favorites, stores the corresponding events in a list and
	 * removes the item from the eventlist; after that it stores the remaining
	 * events also in the sorted list
	 * 
	 * @param events
	 * @return
	 */
	private List<Event> sortFavoriteEvents(List<Event> events) {
		List<Event> localEvents = events;
		List<Event> favoriteLocations = new ArrayList<Event>();
		for (int i = 0; i < events.size(); i++) {
			if (favorites.containsKey(events.get(i).locationID)) {
				favoriteLocations.add(events.get(i));
				localEvents.remove(i);
			}
		}
		for (int i = 0; i < localEvents.size(); i++) {
			favoriteLocations.add(localEvents.get(i));
		}
		return favoriteLocations;
	}

	/**
	 * sorts the eventList according to their genre
	 * @param map
	 * @return
	 */
	public List<Event> sortGenre(Map<String, Event> map) {
		List<Event> events = new ArrayList<Event>();
		List<String> parentGenre = new ArrayList<String>();
		GenreNode currentNode = getGenreNode();
		if (currentNode == null) {
			currentNode = new GenreNode("Music", 0, 0, 0);
		}
		parentGenre.addAll(getChildGenre(currentNode));
		parentGenre.add(currentNode.name);
		Event event;
		for (Map.Entry<String, Event> entry : map.entrySet()) {
			event = entry.getValue();
			boolean isInList = false;
			// nur die speichern, die mit genre in Liste �bereinstimmen
			for (int i = 0; i < parentGenre.size(); i++) {
				for (String string : event.eventGenre.split(";")) {
					if (string.equalsIgnoreCase(parentGenre.get(i))
							|| string.equals("")) {
						if (!isInList) {
							isInList = true;
						}
					}
				}
			}
			if (isInList)
				events.add(event);
		}
		return events;
	}
	
	/**
	 * searches iteratively all child-genres of the current genre and stores
	 * them in a list
	 * 
	 * @param node
	 * @return list
	 */
	private List<String> getChildGenre(GenreNode node) {
		List<String> genres = new ArrayList<String>();
		for (int i = 0; i < node.getChildren().size(); i++) {
			genres.add(node.getChildren().get(i).name);
			genres.addAll(getChildGenre(node.getChildren().get(i)));
		}
		return genres;

	}

	/**
	 * getter for the current list of events
	 * 
	 * @return List<EventLocation>
	 */
	public List<Event> getEventList() {
		updateFavorites();
		// List<Event> eL = new ArrayList<Event>(date(eventList));
		storeDistance(eventList);
		List<Event> eL = sortGenre(eventList);
		Collections.sort(eL, new DateDistanceComparator());
		Log.v("getEventList", eL.size() + "");
		// if the list of events has no items, an empty item is stored into the
		// list and the boolean that shows that no events are in the list is set
		// on true
		if (eL.size() == 0) {
			eL.add(new Event());
			setNoEvents(true);
		}
		return eL;
	}

	/**
	 * getter for the list of favorites
	 * 
	 * @return
	 */
	public Map<String, FavoriteLocation> getFavorites() {
		return favorites;
	}

	/**
	 * getter for the string of json which is stored in shared preferenced
	 * 
	 * @return
	 */
	public String getJsonForSharedPreferences() {
		return jsonForSharedPreferences.toString();
	}

	/**
	 * sets the json for shared preferences
	 * 
	 * @param jsonForSharedPreferences
	 */
	public void setJsonForSharedPreferences(JSONObject jsonForSharedPreferences) {
		this.jsonForSharedPreferences = jsonForSharedPreferences;
	}

	/**
	 * sets the map of favorites
	 * 
	 * @param favorites
	 */
	public void setFavorites(Map<String, FavoriteLocation> favorites) {
		this.favorites = favorites;
	}

	/**
	 * sets the current location
	 * 
	 * @param location
	 */
	public void setLocation(Location location) {
		currentLocation = location;
	}

	/**
	 * getter for the current genrenode
	 * 
	 * @return
	 */
	public GenreNode getGenreNode() {
		return genreNode;
	}

	/**
	 * sets the current genrenode
	 * 
	 * @param genreNode
	 */
	public void setGenreNode(GenreNode genreNode) {
		this.genreNode = genreNode;
	}

	/**
	 * getter; true if the current genre has no events in the list
	 * 
	 * @return
	 */
	public boolean isNoEvents() {
		return noEvents;
	}

	/**
	 * sets the boolean on true if the current node has no events in the list
	 * 
	 * @param noEvents
	 */
	public void setNoEvents(boolean noEvents) {
		this.noEvents = noEvents;
	}
}
