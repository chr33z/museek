package de.mimuc.pem_music_graph.favorite_list;

import de.mimuc.pem_music_graph.list.Event;

public class FavoriteLocation {

	public String locationID;
	public String locationName;
	public String locationLatitude;
	public String locationLongitude;
	public String locationDescription;
	public String addressStreet;
	public String addressNumber;
	public String addressCity;
	public String addressPostcode;
	public String locationWebsite;
	
	public Event nextEvent;

	public FavoriteLocation(String locationID, String locationName,
			String locationLatitude, String locationLongitude,
			String locationDescription, String addressStreet,
			String addressNumber, String addressCity, String addressPostcode,
			String locationWebsite) {
		
		this.locationID = locationID;
		this.locationName = locationName;
		this.locationLatitude = locationLatitude;
		this.locationLongitude = locationLongitude;
		this.locationDescription = locationDescription;
		this.addressStreet = addressStreet;
		this.addressNumber = addressNumber;
		this.addressCity = addressCity;
		this.addressPostcode = addressPostcode;
		this.locationWebsite = locationWebsite;
	}
	
	public void setNextEvent(Event event){
		this.nextEvent = event;
	}
}
