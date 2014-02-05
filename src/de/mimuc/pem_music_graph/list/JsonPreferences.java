package de.mimuc.pem_music_graph.list;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.mimuc.pem_music_graph.favorite_list.FavoriteLocation;
import de.mimuc.pem_music_graph.utils.JsonConstants;

public class JsonPreferences implements JsonConstants{

	public static String createJsonFromFavorites(Map<String, FavoriteLocation> favorites) {
		JSONArray array = new JSONArray();
		for (Map.Entry<String, FavoriteLocation> entry : favorites.entrySet()) {
			try {
				JSONObject object = new JSONObject();
				object.put(TAG_LOCATION_ID, entry.getValue().locationID);
				object.put(TAG_LOCATION_NAME, entry.getValue().locationName);
				object.put(TAG_LOCATION_LATITUDE,
						entry.getValue().locationLatitude);
				object.put(TAG_LOCATION_LONGITUDE,
						entry.getValue().locationLongitude);
				object.put(TAG_LOCATION_DESCRIPTION,
						entry.getValue().locationDescription);
				object.put(TAG_LOCATION_ADDRESS_STREET,
						entry.getValue().addressStreet);
				object.put(TAG_LOCATION_ADDRESS_NUMBER,
						entry.getValue().addressNumber);
				object.put(TAG_LOCATION_ADDRESS_CITY,
						entry.getValue().addressCity);
				object.put(TAG_LOCATION_ADDRESS_POSTCODE,
						entry.getValue().addressPostcode);
				object.put(TAG_LOCATION_WEBSITE,
						entry.getValue().locationWebsite);
				array.put(object);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return array.toString();
	}
	
	public static Map<String, FavoriteLocation> createFavoritesFromJson(String json) {
		Map<String, FavoriteLocation> favorites = new HashMap<String, FavoriteLocation>();
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
		try {
			JSONArray array = new JSONArray(json);

			for (int i = 0; i < array.length(); i++) {
				JSONObject favorite = array.getJSONObject(i);
				locationID = favorite.getString(TAG_LOCATION_ID);
				locationName = favorite.getString(TAG_LOCATION_NAME);
				locationLatitude = favorite.getString(TAG_LOCATION_LATITUDE);
				locationLongitude = favorite
						.getString(JsonConstants.TAG_LOCATION_LONGITUDE);
				locationDescription = favorite
						.getString(TAG_LOCATION_DESCRIPTION);
				addressStreet = favorite.getString(TAG_LOCATION_ADDRESS_STREET);
				addressNumber = favorite.getString(TAG_LOCATION_ADDRESS_NUMBER);
				addressCity = favorite.getString(TAG_LOCATION_ADDRESS_CITY);
				addressPostcode = favorite
						.getString(TAG_LOCATION_ADDRESS_POSTCODE);
				locationWebsite = favorite.getString(TAG_LOCATION_WEBSITE);

				FavoriteLocation favoriteLocation = new FavoriteLocation(
						locationID, locationName, locationLatitude,
						locationLongitude, locationDescription, addressStreet,
						addressNumber, addressCity, addressPostcode,
						locationWebsite);
				favorites.put(locationID, favoriteLocation);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return favorites;
	}
}
