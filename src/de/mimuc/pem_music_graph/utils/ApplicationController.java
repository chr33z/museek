package de.mimuc.pem_music_graph.utils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

import android.app.Application;
import android.graphics.Paint;
import android.text.TextUtils;

/**
 * Centralized place for application-wide fields and services
 * 
 * Credit:
 * http://arnab.ch/blog/2013/08/asynchronous-http-requests-in-android-using
 * -volley/
 * 
 * @author chris
 * 
 */
public class ApplicationController extends Application {

	public static final String TAG = ApplicationController.class.getName();

	/** URL base url for service */
	public static final String URL_BASE = "http://pem-project-server.herokuapp.com/";

	/** URL GET find all locations - use with care! */
	public static final String URL_GET_FIND_ALL = URL_BASE + "location/findall";

	/** URL POST find locations by position and radius */
	public static final String URL_GET_FIND_BY_LOCATION = URL_BASE
			+ "location/findbylocation";

	/** URL POST find locations by position and radius */
	public static final String URL_POST_FIND_BY_LOCATION = URL_BASE
			+ "location/findbylocation";
	
	/**
	 * If user has not moved at least this distance, there is no update from the server
	 */
	public static final int MAX_UPDATE_DISTANCE = 1000;
	
	public static final long DEFAULT_UPDATE_LOCATION_INTERVAL = 60 * 1000; // update every 60 seconds
	
	public static final long DEFAULT_TERMINATE_SAT_FINDING = 1 * 60 * 60 * 1000; // for 1 hour
	
	/**
	 * Global request queue for Volley
	 */
	private RequestQueue mRequestQueue;

	/**
	 * A singleton instance of the application class for easy access in other
	 * places
	 */
	private static ApplicationController sInstance;

	@Override
	public void onCreate() {
		super.onCreate();

		// initialize the singleton
		sInstance = this;
	}

	/**
	 * @return ApplicationController singleton instance
	 */
	public static synchronized ApplicationController getInstance() {
		return sInstance;
	}

	/**
	 * @return The Volley Request queue, the queue will be created if it is null
	 */
	public RequestQueue getRequestQueue() {
		// lazy initialize the request queue, the queue instance will be
		// created when it is accessed for the first time
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	/**
	 * Adds the specified request to the global queue, if tag is specified then
	 * it is used else Default TAG is used.
	 * 
	 * @param req
	 * @param tag
	 */
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

		VolleyLog.d("Adding request to queue: %s", req.getUrl());

		getRequestQueue().add(req);
	}

	/**
	 * Adds the specified request to the global queue using the Default TAG.
	 * 
	 * @param req
	 * @param tag
	 */
	public <T> void addToRequestQueue(Request<T> req) {
		// set the default tag if tag is empty
		req.setTag(TAG);

		getRequestQueue().add(req);
	}

	/**
	 * Cancels all pending requests by the specified TAG, it is important to
	 * specify a TAG so that the pending/ongoing requests can be cancelled.
	 * 
	 * @param tag
	 */
	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

}
