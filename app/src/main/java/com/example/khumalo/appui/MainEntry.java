package com.example.khumalo.appui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.khumalo.appui.Login.LoginActivity;
import com.example.khumalo.appui.Utils.Constants;
import com.example.khumalo.appui.Utils.Utils;

import java.util.Random;


public class MainEntry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean(Constants.isLoggedIn, false)) {
            Boolean isChecked = sp.getBoolean(Constants.USER_STATUS,false);
            if(isChecked){
                Intent intent = new Intent(this,Rider.class);
                startActivity(intent);
                this.finish();
            }else{
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                this.finish();
            }
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

    }

}
