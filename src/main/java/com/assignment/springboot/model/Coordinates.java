package com.assignment.springboot.model;

public class Coordinates {


    private String lat;
    private String lng;

    public Coordinates() {

    }
    public Coordinates(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }
    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
