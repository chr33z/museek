package de.mimuc.pem_music_graph.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Manages connection to google play services and in case of an error tries to recover play services.
 * 
 * @author Christopher Gebhardt
 *
 */
public class PlayServicesManager {
	
	private static final String TAG = PlayServicesManager.class.getSimpleName();
	
	private static final int MISSING_PLAY_SERVICES = 0;

	/**
	 * Check if google play services are available and, if not, try updating or downloading them
	 * 
	 * @param context
	 * @return true if play services available, false otherwise
	 */
	public static boolean isGooglePlayServiceUpToDate(Context context){
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		
		if(status == ConnectionResult.SUCCESS){
			Log.i(TAG, "Google play services are up to date. Proceed...");
			return true;
		}
		else if(status == ConnectionResult.SERVICE_MISSING){
			Log.e(TAG, "Google play services is missing. Try recovering user and update play services...");
			recoverPlayServices(context, status);
			return false;
		}
		else if(status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED){
			Log.w(TAG, "Google play services update required.");
			recoverPlayServices(context, status);
			return false;
		}
		else if(status == ConnectionResult.SERVICE_DISABLED){
			Log.e(TAG, "Google play services is disabled.");
			recoverPlayServices(context, status);
			return false;
		}
		else if(status == ConnectionResult.SERVICE_INVALID){
			Log.e(TAG, "Google play services is invald.");
			recoverPlayServices(context, status);
			return false;
		}
		return false;
	}
	
	private static boolean recoverPlayServices(Context context, int status){
		if(GooglePlayServicesUtil.isUserRecoverableError(status)){
			GooglePlayServicesUtil.getErrorDialog(status, (Activity)context, MISSING_PLAY_SERVICES);
		} else {
			Log.e(TAG, "... user is not recoverable.");
		}
		return false;
	}
}
