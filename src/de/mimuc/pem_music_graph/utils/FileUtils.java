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

	/**
	 * write an JSONObject event list to internal storage
	 * 
	 * @param eventList
	 * @param activity the calling activity
	 * @throws IOException
	 */
	public static void writeEventListToStorage(JSONObject eventList, Activity activity) throws IOException {
		String json = eventList.toString();

		FileOutputStream fos = activity.openFileOutput(FILENAME_EVENTS, Context.MODE_PRIVATE);
		fos.write(json.getBytes());
		fos.close();
	}
	
	/**
	 * Read an event list from storage and return as a JSONObject
	 * 
	 * @param activity
	 * @return event list as JSONObject or null
	 */
	public static JSONObject readEventListFromStorage(Activity activity){
		JSONObject eventList = null;
		
		FileInputStream fis;
		try {
			fis = activity.openFileInput(FILENAME_EVENTS);
			StringBuffer fileContent = new StringBuffer("");

			byte[] buffer = new byte[1024];

			while (fis.read(buffer) != -1) {
			    fileContent.append(new String(buffer));
			}
			
			eventList = new JSONObject(fileContent.toString());
		} catch (FileNotFoundException e) {
			Log.w(TAG, "Could not get event list from storage.");
			// do nothing and just return null at the end
		} catch (IOException e) {
			Log.w(TAG, "Could not get event list from storage.");
			// do nothing and just return null at the end
		} catch (JSONException e) {
			Log.w(TAG, "Could not convert event list to valid JSONObject.");
			// do nothing and just return null at the end
		}
		return eventList;
	}
}
