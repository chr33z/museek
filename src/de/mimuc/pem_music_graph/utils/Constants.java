package de.mimuc.pem_music_graph.utils;

public interface Constants {
	
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";

	public static final String TAG_FAVORITES = "favorites";

	public static final String TAG_LOCATIONS 			= "locations";

	public static final String TAG_NAME 				= "name";
	public static final String TAG_DESCRIPTION 			= "description";

	public static final String TAG_ADDRESS 				= "address";
	public static final String TAG_STREET 				= "street";
	public static final String TAG_HOUSENUMBER 			= "housenumber";
	public static final String TAG_CITY 				= "city";
	public static final String TAG_POSTCODE 			= "postcode";

	public static final String TAG_PHONENUMBER 			= "phonenumber";
	public static final String TAG_EMAILADDRESS 		= "emailaddress";
	public static final String TAG_OPENINGHOURS 		= "openinghours";
	public static final String TAG_AGERESTRICTION		= "agerestriction";
	public static final String TAG_FURTHERINFORMATION 	= "furtherinformation";


	public static final String TAG_LATITUDE 			= "lat";
	public static final String TAG_LONGITUDE 			= "lon";

	/*
	 * new Server API json tags
	 */
	// general
    public static final String TAG_RESULT_TIME         = "result_time";
    public static final String TAG_RESULT_RADIUS = "search_radius";
    public static final String TAG_RESULT_LATITUDE = "search_latitude";
    public static final String TAG_RESULT_LONGITUDE = "search_longitude";

    // location specific
    public static final String TAG_LOCATION_ID = "location_id";
    public static final String TAG_LOCATION_NAME = "location_name";
    public static final String TAG_LOCATION_LATITUDE = "location_latitude";
    public static final String TAG_LOCATION_LONGITUDE = "location_longitude";
    public static final String TAG_LOCATION_DESCRIPTION = "location_description";
    public static final String TAG_LOCATION_ADDRESS_STREET = "location_address_street";
    public static final String TAG_LOCATION_ADDRESS_NUMBER = "location_address_housenumber";
    public static final String TAG_LOCATION_ADDRESS_CITY = "location_address_city";
    public static final String TAG_LOCATION_ADDRESS_POSTCODE = "location_address_postcode";
    public static final String TAG_LOCATION_WEBSITE = "location_website";

	// event specific
	public static final String TAG_EVENT_NUMBER = "event_number";
	public static final String TAG_EVENT_NAME = "event_name";
	public static final String TAG_EVENT_DESCRIPTION= "event_description";
	public static final String TAG_EVENT_START_TIME = "event_start_time";
	public static final String TAG_EVENT_END_TIME = "event_end_time";
	public static final String TAG_EVENT_MIN_AGE = "event_age_restriction";
	public static final String TAG_EVENT_SPECIAL_OFFER = "event_special_offer";
	public static final String TAG_EVENT_GENRES = "event_genres";
	public static final String TAG_EVENT_PRICE = "event_price";

}
