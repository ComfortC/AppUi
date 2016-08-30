package com.example.khumalo.appui.ClientModel;

/**
 * Created by KHUMALO on 8/30/2016.
 */
public class ClientProfile {
    private String name;
    private String surname;
    // private String Area;


    public ClientProfile() {
    }

    public ClientProfile(String name, String surname) {
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
