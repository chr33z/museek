package de.mimuc.pem_music_graph.utils;

import android.os.Build;

/**
 * Easy check if api is below a certain target api
 * 
 * @author Christopher Gebhardt
 *
 */
public class ApiGuard {
	
	/**
	 * check whether api is below a specific level
	 * @param version like Build.VERSION_CODES.HONEYCOMB
	 * @return true if api is below that api
	 */
	public static boolean apiBelow(int version){
		
		if (Build.VERSION.SDK_INT < version) {
	        return true;
	    } else {
	    	return false;
	    }
	}
	
	public static boolean belowJellyBean(){
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
	        return true;
	    } else {
	    	return false;
	    }
	}
}
