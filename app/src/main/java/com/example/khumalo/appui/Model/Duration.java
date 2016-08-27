package com.example.khumalo.appui.Model;


public class Duration {
    public String text;
    public int value;

    public Duration(String text, int value) {
        this.text = text;
        this.value = value;
    }
    
    public String getDurationText(){
    	return text;
    }
    
    public int getDurationValue(){
    	return value;
    }
    
      
}