package de.mimuc.pem_music_graph.utils;

import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * Bundle contains:
 * double latitude := "latitude"
 * double longitude := "longitude"
 * String addressPattern := "address"
 * 
 * @author Christopher Gebhardt
 *
 */
public class GeoCodingTask extends AsyncTask<Bundle, Void, Address> {
	
	private final static String TAG = GeoCodingTask.class.getSimpleName();

	Context mContext;

	GeocodingListener mListener;

	public interface GeocodingListener {

		public void onGeocodingFinished(Address address);
	}

	public GeoCodingTask(Context context, GeocodingListener geocodingListener) {
		super();
		mContext = context;
		mListener = geocodingListener;
	}

	@Override
	protected Address doInBackground(Bundle... bundle) {
		Bundle args = bundle[0];
		String address = args.getString("address");
		address = address.replace(", ", " ");
		address = address.replace(",", " ");
		address = address.replace(" ", "+");
		address = address.replace("ä", "ae").replace("ö", "oe").replace("ü", "ue").replace("ß", "ss");
        String url = "http://maps.googleapis.com/maps/api/geocode/json?address="+ address +"&sensor=true";
		
		// POST request
		JsonObjectRequest req = new JsonObjectRequest(url, null, 
				
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.i(TAG, "...success!");
						Log.i(TAG, response.toString());
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (error.getMessage() == null) {
							Log.e(TAG,
									"...could not retrieve locations");
							return;
						}
						VolleyLog.e("Error: ", error.getMessage());
						Log.w(TAG, "...could not retrieve locations!");
					}
				});
		ApplicationController.getInstance().addToRequestQueue(req);
		
		return null;
	}
}
