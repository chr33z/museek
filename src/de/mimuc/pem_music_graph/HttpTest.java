package de.mimuc.pem_music_graph;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import de.mimuc.pem_music_graph.utils.ApplicationController;

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
public class HttpTest extends Activity {
	
	public static final String TAG = "http test";
	
	TextView text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_http_test);
		
		text = (TextView) findViewById(R.id.text1);
		
		Log.d(TAG, ApplicationController.URL_GET_FIND_ALL);
		
		// pass second argument as "null" for GET requests
		JsonObjectRequest req = new JsonObjectRequest(ApplicationController.URL_GET_FIND_ALL, null,
		       new Response.Listener<JSONObject>() {
		           @Override
		           public void onResponse(JSONObject response) {
		               try {
		                   VolleyLog.v("Response:%n %s", response.toString(4));
		                   text.setText(response.toString(4));
		                   
		               } catch (JSONException e) {
		                   e.printStackTrace();
		               }
		           }
		       }, new Response.ErrorListener() {
		           @Override
		           public void onErrorResponse(VolleyError error) {
		               Log.e(TAG, error.getMessage());
		        	   VolleyLog.e("Error: ", error.getMessage());
		           }
		       });

		Log.d(TAG, "applica is null "+(ApplicationController.getInstance() == null)+"");
		Log.d(TAG, "req is null "+(req == null)+"");
		
		// add the request object to the queue to be executed
		ApplicationController.getInstance().addToRequestQueue(req);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.http_test, menu);
		return true;
	}

}
