package com.sinichi.parentingcontrolv3.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class LocationModel {

    private String id;
    private double latitude;
    private double longitude;

    public LocationModel() {}

    public LocationModel(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
