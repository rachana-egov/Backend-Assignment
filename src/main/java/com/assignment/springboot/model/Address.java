package com.assignment.springboot.model;

public class Address {
    public String city;
    public String street_name;
    public String street_address;
    public String zip_code;
    public String state;
    public String country;
    public Coordinates coordinates;

    public Address(String city, String street_name, String street_address, String zip_code, String state, String country, Coordinates coordinates) {
        this.city = city;
        this.street_name = street_name;
        this.street_address = street_address;
        this.zip_code = zip_code;
        this.state = state;
        this.country = country;
        this.coordinates = coordinates;
    }

    public Address() {

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet_name() {
        return street_name;
    }

    public void setStreet_name(String street_name) {
        this.street_name = street_name;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public String getZipCode() {
        return zip_code;
    }

    public void setZipCode(String zipCode) {
        this.zip_code = zipCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
}
