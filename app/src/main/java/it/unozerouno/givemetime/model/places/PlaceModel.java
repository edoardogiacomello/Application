package it.unozerouno.givemetime.model.places;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

import it.unozerouno.givemetime.controller.fetcher.places.PlaceFetcher.PlaceResult;
import it.unozerouno.givemetime.model.constraints.ComplexConstraint;


public class PlaceModel{
	private String placeId;
	private String name;
	private String address;
	private String formattedAddress;
	private String country;
	private String phoneNumber;
	private String icon;
	private Location location;
	private int visitCounter;
	private List<ComplexConstraint> openingTime;
	//When adding attributes remember to edit also constructors and overrides
	
	

	public PlaceModel(String placeId, String name, String address,
			String country) {
		super();
		this.placeId = placeId;
		this.name = name;
		this.address = address;
		this.country = country;
		openingTime = new ArrayList<ComplexConstraint>();
		location = new Location("Places");
	}
	
	public PlaceModel(PlaceResult placeResult) {
		this.placeId = placeResult.getPlaceID();
		this.name = placeResult.getName();
		this.address = placeResult.getAddress();
		this.country = placeResult.getCountry();
		openingTime = new ArrayList<ComplexConstraint>();
		location = new Location("Places");
	}
	
	
	

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFormattedAddress() {
		return formattedAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<ComplexConstraint> getOpeningTime() {
		return openingTime;
	}

	public void setOpeningTime(List<ComplexConstraint> openingTime) {
		this.openingTime = openingTime;
	}
	public void setVisitCounter(int visitCounter){
		this.visitCounter = visitCounter;
	}

	public int getVisitCounter() {
		return visitCounter;
	}
	
	
	@Override
	public PlaceModel clone() {
		PlaceModel copy = new PlaceModel(placeId, name, address, country);
		copy.setFormattedAddress(formattedAddress);
		copy.setCountry(country);
		copy.setPhoneNumber(phoneNumber);
		copy.setIcon(icon);
		copy.setLocation(location);
		copy.setVisitCounter(visitCounter);
		copy.setOpeningTime(openingTime);
		return copy;
	}

}
