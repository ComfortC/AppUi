package com.example.khumalo.appui.DriverModel;

/**
 * Created by KHUMALO on 8/24/2016.
 */
public class DriverLocation {

    private Double latitude;
    private Double longitude;

    public DriverLocation() {
    }

    public DriverLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
