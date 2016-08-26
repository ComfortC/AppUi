package com.example.khumalo.appui;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import java.util.Random;


public class MainEntry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Random random = new Random();
        int value = random.nextInt(2)+1;
        if(value==1){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this,Rider.class);
            startActivity(intent);
        }

    }

}
