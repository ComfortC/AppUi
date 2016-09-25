package com.example.khumalo.appui.DriverModel;

/**
 * Created by KHUMALO on 8/24/2016.
 */
public class DriverProfile {

    private String name;
    private String surname;
    private String licenseNumber;
    private int PhoneNumber;
    private String imageUrl;
   // private String Area;

    public DriverProfile() {
    }

    public DriverProfile(String name, String surname, String licenseNumber, int PhoneNumber,String imageUrl) {
        this.name = name;
        this.surname = surname;
        this.licenseNumber = licenseNumber;
        this.PhoneNumber = PhoneNumber;
        this.imageUrl = imageUrl;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public int getPhoneNumber() {
        return PhoneNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}
