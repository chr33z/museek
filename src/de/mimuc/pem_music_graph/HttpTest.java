package de.mimuc.pem_music_graph;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import de.mimuc.pem_music_graph.utils.ApplicationController;
import de.mimuc.pem_music_graph.utils.JsonConstants;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

/**
 * simple test for http request to server
 * 
 * @author chris
 *
 */
public class HttpTest extends Activity implements JsonConstants {
	
	public static final String TAG = "http test";
	
	TextView tvName;
	TextView tvDescription;
	TextView tvStreet;
	TextView tvHousenumber;
	TextView tvCity;
	TextView tvPostcode;
	TextView tvLatitude;
	TextView tvLongitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_http_test);
		
		tvName = (TextView) findViewById(R.id.text1);
		tvDescription = (TextView) findViewById(R.id.text2);
		tvStreet = (TextView) findViewById(R.id.text3);
		tvHousenumber = (TextView) findViewById(R.id.text4);
		tvCity = (TextView) findViewById(R.id.text5);
		tvPostcode = (TextView) findViewById(R.id.text6);
		tvLatitude = (TextView) findViewById(R.id.text7);
		tvLongitude = (TextView) findViewById(R.id.text8);
		
		Log.d(TAG, ApplicationController.URL_GET_FIND_ALL);
		
		// pass second argument as "null" for GET requests
		JsonObjectRequest req = new JsonObjectRequest(ApplicationController.URL_GET_FIND_ALL, null,
		       new Response.Listener<JSONObject>() {
		           @Override
		           public void onResponse(JSONObject response) {
		        	   readJson(response);
		           }
		       }, new Response.ErrorListener() {
		           @Override
		           public void onErrorResponse(VolleyError error) {
		               Log.e(TAG, error.getMessage());
		        	   VolleyLog.e("Error: ", error.getMessage());
		           }
		       });

		// add the request object to the queue to be executed
		ApplicationController.getInstance().addToRequestQueue(req);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.http_test, menu);
		return true;
	}
	
	private void readJson(JSONObject json){
		String name = null;
		String description = " --- ";
		String street = null;
		String housenumber = null;
		String city = null;
		String postcode = null;
		String latitude = null;
		String longitude = null;
		
		Log.d(TAG, json.toString());
		
		try {
			// locations are stored in an array
			JSONArray locations = json.getJSONArray(TAG_LOCATIONS);
			
			for (int i=0; i<1; i++) {
				JSONObject location = locations.getJSONObject(i);
				
				name = location.getString(TAG_NAME);
				
				// Address is an extra JSONObject
				JSONObject address = location.getJSONObject(TAG_ADDRESS);
				street = address.getString(TAG_STREET);
				housenumber = address.getString(TAG_HOUSENUMBER);
				city = address.getString(TAG_CITY);
				postcode = address.getString(TAG_POSTCODE);
				
				latitude = location.getString(TAG_LATITUDE);
				longitude = location.getString(TAG_LONGITUDE);
			}
			
			
			tvName.setText(name);
			tvDescription.setText(description);
			tvStreet.setText(street);
			tvHousenumber.setText(housenumber);
			tvCity.setText(city);
			tvPostcode.setText(postcode);
			tvLatitude.setText(latitude);
			tvLongitude.setText(longitude);
			
		} catch (JSONException error){
			Log.e(TAG, error.getMessage());
		}
	}

}
