package com.example.khumalo.appui.Model;

public class Distance {
    public String text;
    public int value;

    public Distance(String text, int value) {
        this.text = text;
        this.value = value;
    }
    
    public String getTextDistance(){
    	return text;
    }
    
    public int getValueDistance(){
    	return value;
    }
}