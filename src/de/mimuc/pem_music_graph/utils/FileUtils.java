package de.mimuc.pem_music_graph.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

/**
 * Some functions for writing and reading app data to the storage
 * 
 * @author Christopher Gebhardt
 *
 */
public class FileUtils {
	
	private static final String TAG = FileUtils.class.getSimpleName();
	
	private static final String FILENAME_EVENTS = "events";
	private static final String FILENAME_FAVORITES = "favorites";

	/**
	 * write an JSONObject event list to internal storage
	 * 
	 * @param eventList
	 * @param activity the calling activity
	 * @throws IOException
	 */
	public static void writeEventListToStorage(JSONObject eventList, Activity activity) throws IOException {
		writeToStorage(eventList.toString(), FILENAME_EVENTS, activity);
	}
	
	/**
	 * write an JSONObject event list to internal storage
	 * 
	 * @param eventList
	 * @param activity the calling activity
	 * @throws IOException
	 */
	public static void writeFavoriteListToStorage(JSONObject favoriteList, Activity activity) throws IOException {
		writeToStorage(favoriteList.toString(), FILENAME_FAVORITES, activity);
	}
	
	public static void writeToStorage(String content, String fileName, Activity activity) throws IOException {
		FileOutputStream fos = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
		fos.write(content.getBytes());
		fos.close();
	}
	
	/**
	 * Read an event list from storage and return as a JSONObject
	 * 
	 * @param activity
	 * @return event list as JSONObject or null
	 */
	public static JSONObject readEventListFromStorage(Activity activity){
		return readFromFromStorage(FILENAME_EVENTS, activity);
	}
	
	/**
	 * Read a favorite list from storage and return as a JSONObject
	 * 
	 * @param activity
	 * @return favorite list as JSONObject or null
	 */
	public static JSONObject readFavoriteListFromStorage(Activity activity){
		return readFromFromStorage(FILENAME_FAVORITES, activity);
	}
	
	public static JSONObject readFromFromStorage(String fileName, Activity activity){
		JSONObject eventList = null;
		
		FileInputStream fis;
		try {
			fis = activity.openFileInput(fileName);
			StringBuffer fileContent = new StringBuffer("");

			byte[] buffer = new byte[1024];

			while (fis.read(buffer) != -1) {
			    fileContent.append(new String(buffer));
			}
			
			eventList = new JSONObject(fileContent.toString());
		} catch (FileNotFoundException e) {
			Log.w(TAG, "Could not get json from storage.");
		} catch (IOException e) {
			Log.w(TAG, "Could not get json from storage.");
		} catch (JSONException e) {
			Log.w(TAG, "Could not convert json to valid JSONObject.");
		}
		return eventList;
	}
}
