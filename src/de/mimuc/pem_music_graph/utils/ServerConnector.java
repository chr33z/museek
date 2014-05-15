package de.mimuc.pem_music_graph.utils;

import java.util.HashMap;

import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import android.location.Location;
import android.util.Log;

/**
 * Manages all requests and answers from our server.
 * 
 * @author Christopher Gebhardt
 *
 */
public class ServerConnector implements Constants {

	private static final String TAG = ServerConnector.class.getSimpleName();

	public interface ServerConnectorListener {

		public void requestFinished(JSONObject json);

		public void requestError();

	}

	/**
	 * Try to get an event list from server
	 * 
	 * @param listener
	 */
	public static void getEventListFromServer(final ServerConnectorListener listener, Location location){
		Log.d(TAG, "Try to retrieve events from server...");

		// Json for the POST Request
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
						Log.i(TAG, "...success!");
						listener.requestFinished(response);
					}
				}, 
				
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (error.getMessage() == null) {
							Log.e(TAG,"...could not retrieve events or a meaningfull error");
						}
						VolleyLog.e("Error: ", error.getMessage());
						Log.w(TAG, "...could not retrieve events!");
						listener.requestError();
					}
				});

		ApplicationController.getInstance().addToRequestQueue(req);
	}
}
