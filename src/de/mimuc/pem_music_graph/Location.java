package de.mimuc.pem_music_graph;

public class Location {

	public String name;
	public String genre;
	public String subgenre;
	public String street;
	public int housenumber;
	public String city;
	public int postcode;
	public String phonenumber;
	public String emailAddress;
	public String openingHours;
	public String ageRestriction;
	public String furtherInformation;
	public String latitude;
	public String longitude;

	public Location(String name, String genre, String subgenre, String street,
			int housenumber, String city, int postcode, String phonenumber,
			String emailAddress, String openingHours, String ageRestriction,
			String furtherInformation, String latitude, String longitude) {
		this.name = name;
		this.genre = genre;
		this.subgenre = subgenre;
		this.street = street;
		this.housenumber = housenumber;
		this.city = city;
		this.postcode = postcode;
		this.phonenumber = phonenumber;
		this.emailAddress = emailAddress;
		this.openingHours = openingHours;
		this.ageRestriction = ageRestriction;
		this.furtherInformation = furtherInformation;
		this.latitude = latitude;
		this. longitude = longitude;
	}

}
