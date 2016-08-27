package com.example.khumalo.appui;

import com.firebase.client.Firebase;

/**
 * Created by KHUMALO on 8/27/2016.
 */
public class InitializeApplication extends android.app.Application  {


    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
