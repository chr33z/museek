package de.mimuc.pem_music_graph.list;

import android.location.Location;

public class Event {

	public String resultTime;
	public String resultRadius;
	public String resultLatitude;
	public String resultLongitude;

	public String eventName;
	public String eventGenre;
	public String eventDescription;
	public String startTime;
	public String endTime;
	public String minAge;
	public String specialOffer;

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
	public Location currentLocation;
	public boolean isFavorite;
	public boolean isExpanded;

	public Event(String resultTime, String resultRadius, String resultLatitude,
			String resultLongitude, String eventName, String eventGenre,
			String eventDescription, String startTime, String endTime,
			String minAge, String specialOffer, String locationID,
			String locationName, String locationLatitude,
			String locationLongitude, String locationDescription,
			String addressStreet, String addressNumber, String addressCity,
			String addressPostcode, String locationWebsite,
			Location currentLocation, boolean isFavorite, boolean isExpanded) {
		this.resultTime = resultTime;
		this.resultRadius = resultRadius;
		this.resultLatitude = resultLatitude;
		this.resultLongitude = resultLongitude;

		this.eventName = eventName;
		this.eventGenre = eventGenre;
		this.eventDescription = eventDescription;
		this.startTime = startTime;
		this.endTime = endTime;
		this.minAge = minAge;
		this.specialOffer = specialOffer;

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

		this.currentLocation = currentLocation;
		this.isFavorite = isFavorite;
		this.isExpanded = isExpanded;
	}

}
