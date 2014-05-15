package de.mimuc.pem_music_graph.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import de.mimuc.pem_music_graph.favorite_list.FavoriteLocation;
import de.mimuc.pem_music_graph.graph.GenreNode;
import de.mimuc.pem_music_graph.utils.ApplicationController;
import de.mimuc.pem_music_graph.utils.Constants;

public class EventController implements Constants {

	private static final String TAG = EventController.class.getSimpleName();

	/**
	 * Save events in locationId-Event pairs
	 */
	public List<Event> eventList;

	/**
	 * The activity that handles all callbacks (parent Activity)
	 */
	private EventControllerListener callbackReceiver;

	/** the actual position of the device */
	private Location mLocation;
	/** true if an alternative location is used to load events */
	private boolean mUseAlternativeLocation = false;

	/**
	 * save favorites in locationId-FavoriteLocation pairs
	 */
	private Map<String, FavoriteLocation> favorites;

	/**
	 * Keep track of expanded views to reset them on a adapter reload
	 */
	private int expandedItem = 0;

	/**
	 * stores the current genrenode
	 */
	private GenreNode genreNode;

	/**
	 * is true if the eventlist for a genre has no items
	 */
	private boolean noEvents;

	/** alternative date to sort events to, chosen by user */
	private DateTime mAlternativeDate;
	/** true if alternative date to sort events to os used */
	private boolean mUseAlternativeDate = false;
	
	private boolean showAll = true;

	/**
	 * initialize event controller constructor if no connection to the internet
	 * and no json available in sharedpreferences
	 * 
	 * @param callbackReceiver
	 */
	public EventController(EventControllerListener callbackReceiver) {
		this.callbackReceiver = callbackReceiver;
		this.eventList = new ArrayList<Event>();
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
		this.eventList = new ArrayList<Event>();
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
		this.eventList = new ArrayList<Event>();
		this.mLocation = location;
		this.favorites = new HashMap<String, FavoriteLocation>();
		readJson(json);
	}

	/**
	 * puts a question to the server to get the current list of events with a
	 * certain radius
	 * 
	 * @param location
	 */
	public void updateEvents(Location location) {
		Log.d(TAG, "Try to retrieve locations from server...");

		mLocation = location;

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
		
		int ID;
		
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
		String price = null;

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
			int currentID = 1;

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject event = jsonArray.getJSONObject(i);
				
				ID = currentID;
				currentID++;

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
				price = event.getString(TAG_EVENT_PRICE);

				locationID = event.getString(TAG_LOCATION_ID);
				locationName = event.getString(TAG_LOCATION_NAME);
				locationLatitude = event.getString(TAG_LOCATION_LATITUDE);
				locationLongitude = event
						.getString(Constants.TAG_LOCATION_LONGITUDE);
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
					if (getEvent(entry.getKey()) != null) {
						if (getEvent(entry.getKey()).equals(locationID)) {
							isFavorite = true;
						}
					}
				}

				isExpanded = false;
				if (locationID.equals(expandedItem)) {
					isExpanded = true;
				}
				
				DateTime now = new DateTime();
				if(Long.parseLong(startTime) < now.minusHours(8).getMillis()){
					continue;
				}

				Event newEvent = new Event(ID, resultTime, resultRadius,
						resultLatitude, resultLongitude, eventName, eventGenre,
						eventDescription, startTime, endTime, minAge,
						specialOffer, locationID, locationName,
						locationLatitude, locationLongitude,
						locationDescription, addressStreet, addressNumber,
						addressCity, addressPostcode, locationWebsite,
						mLocation, price, currentDistance, isFavorite,
						isExpanded);

				eventList.add(newEvent);
			}
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
	public void addFavorite(String locationID) {
		FavoriteLocation fLocation = new FavoriteLocation(locationID,
			getEvent(locationID).locationName,
			getEvent(locationID).locationLatitude,
			getEvent(locationID).locationLongitude,
			getEvent(locationID).locationDescription,
			getEvent(locationID).addressStreet,
			getEvent(locationID).addressNumber,
			getEvent(locationID).addressCity,
			getEvent(locationID).addressPostcode,
			getEvent(locationID).locationWebsite);
		

		if(fLocation != null){
			favorites.put(locationID, fLocation);
			
			for(Event event : getAllEvents(locationID)){
				event.isFavorite = true;
			}
		}
	}

	/**
	 * removes the locationID from favorites when the location was removed from
	 * favorites; it updates the list
	 * 
	 * @param locationID
	 */
	public void removeFavorite(String locationID) {

		if (favorites.containsKey(locationID)) {
			for(Event event : getAllEvents(locationID)){
				event.isFavorite = false;
			}
			favorites.remove(locationID);
		}
	}

	/**
	 * updates the entries of the favorite-value of the eventlist
	 */
	public void updateFavorites() {
		for (Map.Entry<String, FavoriteLocation> entry : favorites.entrySet()) {
			if (getEvent(entry.getKey()) != null) {
				for (Event event : getAllEvents(entry.getKey())) {
					event.isFavorite = true;
				}
			}
		}
	}

	/**
	 * stores the locationID of expanded item, sets it in the eventList on true
	 * and sets the previous on false
	 * 
	 * @param locationID
	 */
	public void expandItem(int ID) {
		Log.v("expandedItemsnull", expandedItem + "");
		if (expandedItem != 0)
			getEventID(expandedItem).isExpanded = false;
		expandedItem = ID;
		getEventID(expandedItem).isExpanded = true;
	}

	/**
	 * sets the collapsed item in the eventList on false and stores an empty
	 * string as expanded item
	 */
	public void collapseItem() {
		getEventID(expandedItem).isExpanded = false;
		expandedItem = 0;
	}
	
	/**
	 * calculates the distance from the currentDistance to the destination and
	 * stores it in every event
	 * 
	 * @param eL
	 * @return
	 */
	public List<Event> storeDistance(List<Event> eL) {
		float distance;
		for (Event event : eL) {
			
			// event location
			Location destination = new Location("destination");
			destination.setLatitude(Double.parseDouble(event.locationLatitude));
			destination.setLongitude(Double.parseDouble(event.locationLongitude));
			
			distance = mLocation.distanceTo(destination);
			event.currentDistance = distance;
		}
		return eL;
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
	 * only show events from that date on
	 * @param eL
	 * @return
	 */
	private List<Event> filterDate(List<Event> eL) {
		List<Event> events = new ArrayList<Event>();
		for (int i = 0; i < eL.size(); i++) {
			DateTime date = new DateTime(Long.parseLong((eL.get(i).startTime))).withTimeAtStartOfDay();
			if(getDateTime().compareTo(date) == 0)
				events.add(eL.get(i));
		}
		return events;
	}

	/**
	 * sorts the eventList according to their genre
	 * 
	 * @param list
	 * @return
	 */
	public List<Event> sortGenre(List<Event> list) {
		List<Event> events = new ArrayList<Event>();
		List<String> parentGenre = new ArrayList<String>();
		GenreNode currentNode = getGenreNode();
		if (currentNode == null) {
			currentNode = new GenreNode("Music", 0, 0, 0);
		}
		parentGenre.addAll(getChildGenre(currentNode));
		parentGenre.add(currentNode.name);
		Event event;
		for (int i = 0; i < list.size(); i++) {
			event = list.get(i);
			boolean isInList = false;
			// nur die speichern, die mit genre in Liste �bereinstimmen
			for (int i1 = 0; i1 < parentGenre.size(); i1++) {
				for (String string : event.eventGenre.split(";")) {
					if (string.equalsIgnoreCase(parentGenre.get(i1))
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
		
		List<Event> eL = new LinkedList<Event>();
		DateTime now = (new DateTime()).minusHours(8);
		for (Event event : eventList) {
			if(Long.parseLong(event.startTime) >= now.getMillis()){
				eL.add(event);
			}
		}
		
		eL = storeDistance(eL);
		eL = sortGenre(eL);
		if(mUseAlternativeDate){
			eL = filterDate(eL);
		}
		Collections.sort(eL, new DateDistanceComparator());
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
	 * sets the map of favorites
	 * 
	 * @param favorites
	 */
	public void setFavorites(Map<String, FavoriteLocation> favorites) {
		this.favorites = favorites;
		updateFavorites();
	}

	/**
	 * sets the current location
	 * 
	 * @param location
	 */
	public void setLocation(Location location) {
		mLocation = location;
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

	public DateTime getDateTime() {
		return mAlternativeDate;
	}
	
	public void useAlternativeTime(boolean useAlternativeDate){
		this.mUseAlternativeDate = useAlternativeDate;
	}

	public void setDateTime(DateTime dateTime) {
		this.mAlternativeDate = dateTime;
	}

	public boolean isShowAll() {
		return showAll;
	}

	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}

	public Event getEvent(String locationId) {
		for (int i = 0; i < eventList.size(); i++) {
			if (eventList.get(i).locationID.equals(locationId)) {
				return eventList.get(i);
			}
		}
		return null;
	}
	
	public List<Event> getCompleteEventList(){
		return eventList;
	}
	
	public Event getEventID(int ID) {
		for (int i = 0; i < eventList.size(); i++) {
			if (eventList.get(i).ID == ID) {
				return eventList.get(i);
			}
		}
		return null;
	}
	
	public List<Event> getAllEvents(String locationId) {
		LinkedList<Event> events = new LinkedList<Event>();
		
		for (int i = 0; i < eventList.size(); i++) {
			if (eventList.get(i).locationID.equals(locationId)) {
				events.add(eventList.get(i));
			}
		}
		return events;
	}
	
	public void useAlternativeLocation(boolean useAlternativeLocation) {
		this.mUseAlternativeLocation = useAlternativeLocation;
	}
}
