package de.mimuc.pem_music_graph.utils;

public interface JsonConstants {

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
	public static final String TAG_RESULT_TIME 	= "res_tim";
	public static final String TAG_RESULT_RADIUS = "res_rad";
	public static final String TAG_RESULT_LATITUDE = "res_lat";
	public static final String TAG_RESULT_LONGITUDE = "res_lon";

	// location specific
	public static final String TAG_LOCATION_ID = "loc_id";
	public static final String TAG_LOCATION_NAME = "loc_nam";
	public static final String TAG_LOCATION_LATITUDE = "loc_lat";
	public static final String TAG_LOCATION_LONGITUDE = "loc_lon";
	public static final String TAG_LOCATION_DESCRIPTION = "loc_des";
	public static final String TAG_LOCATION_ADDRESS_STREET = "loc_add_str";
	public static final String TAG_LOCATION_ADDRESS_NUMBER = "loc_add_num";
	public static final String TAG_LOCATION_ADDRESS_CITY = "loc_add_cit";
	public static final String TAG_LOCATION_ADDRESS_POSTCODE = "loc_add_pos";
	public static final String TAG_LOCATION_WEBSITE = "loc_web";

	// event specific
	public static final String TAG_EVENT_NUMBER = "eve_num";
	public static final String TAG_EVENT_NAME = "eve_nam";
	public static final String TAG_EVENT_DESCRIPTION= "eve_des";
	public static final String TAG_EVENT_START_TIME = "eve_sti";
	public static final String TAG_EVENT_END_TIME = "eve_eti";
	public static final String TAG_EVENT_MIN_AGE = "eve_mag";
	public static final String TAG_EVENT_SPECIAL_OFFER = "eve_spo";
	public static final String TAG_EVENT_GENRES = "eve_gen";
}
