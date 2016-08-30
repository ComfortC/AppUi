package com.example.khumalo.appui.DriverModel;

/**
 * Created by KHUMALO on 8/24/2016.
 */
public class DriverProfile {

    private String name;
    private String surname;
   // private String Area;

    public DriverProfile() {
    }

    public DriverProfile(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}
