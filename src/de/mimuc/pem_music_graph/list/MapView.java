package de.mimuc.pem_music_graph.list;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.maps.MapActivity;

import de.mimuc.pem_music_graph.MainActivity;

public class MapView extends MapActivity {
	
	private GoogleMap mMap;
	private String lon;
	private String lat;
	
	MainActivity combinendView;

	public MapView(MainActivity cv){
		this.combinendView = cv;
	}
	
	public MapView() {
		
	}
	
	

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	private void showLocation() {
		// TODO Auto-generated method stub
		new AsyncTask<String, String, Void>() {

			@Override
			protected Void doInBackground(String... params) {
				try {
					

					String string = EntityUtils.toString(response.getEntity());
					Log.v("GoogleMaps", string);

					String[] lat1 = string.split("<latitude>");
					String lat2 = lat1[1];
					String[] lat3 = lat2.split("</latitude>");
					lat = lat3[0];
					String[] long1 = string.split("<longitude>");
					String long2 = long1[1];
					String[] long3 = long2.split("</longitude>");
					lon = long3[0];
					
				} catch (Exception e) {
					
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
//				if(la == null){
//					la = "45.346";
//				}
//				if(lo == null){
//					lo = "78.34";
//				}
				LatLng latlng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
				MarkerOptions marker = new MarkerOptions()
						.position(latlng)
						.title("Server");
				
				mMap.addMarker(marker);
				
			}
		}.execute();
	}
	*/
}

