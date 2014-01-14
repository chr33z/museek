package de.mimuc.pem_music_graph.list;

import android.location.Location;

public class EventLocation {

	public String name;
	public String genre;
	public String subgenre;
	public String street;
	public String housenumber;
	public String city;
	public String postcode;
	// public String phonenumber;
	// public String emailAddress;
	public String openingHours;
	// public String ageRestriction;
	public String furtherInformation;
	public String latitude;
	public String longitude;
	public Location currentLocation;
	public String locationUri;

	public EventLocation(String name, String genre, String subgenre,
			String street, String housenumber, String city, String postcode,
			String openingHours, String furtherInformation, String latitude,
			String longitude, Location currentLocation, String locationUri) {
		this.name = name;
		this.genre = genre;
		this.subgenre = subgenre;
		this.street = street;
		this.housenumber = housenumber;
		this.city = city;
		this.postcode = postcode;
//		this.phonenumber = phonenumber;
//		this.emailAddress = emailAddress;
		this.openingHours = openingHours;
//		this.ageRestriction = ageRestriction;
		this.furtherInformation = furtherInformation;
		this.latitude = latitude;
		this.longitude = longitude;
		this.currentLocation = currentLocation;
		this.locationUri = locationUri;
	}

}
