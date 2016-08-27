package com.example.khumalo.appui.Model;

import java.util.*;
import java.io.File;
import java.text.DecimalFormat;


public class Leg {
	public Distance legDistance;
	public Duration legDuration;
	public List<Step> steps;
	
	public Leg(Distance legDistance, Duration legDuration, List<Step> steps){
		this.legDistance = legDistance;
		this.legDuration = legDuration;
		this.steps = steps;
	}

	public String getLegDistance(){
		return legDistance.getTextDistance();
	}
	
	public String getLegDuration(){
		return legDuration.getDurationText();
	}
	
	public List<Step> getSteps(){
		return steps;
	}
	
	public String getRemainingDistance(int StepNumber){
		
		int totalDistanceToStep = getTotalDistanceToThisStep(StepNumber);
		int remainingDistance = legDistance.getValueDistance()-totalDistanceToStep;
		return convertValueDistanceToText(remainingDistance);
	}
	
	
	public String getRemainingTime(int StepNumber){
		int totalTimeToStep = getTotalTimeToThisStep(StepNumber);
		int remainingTime = legDuration.getDurationValue()-totalTimeToStep;
		return convertValueDurationToText(remainingTime);
	}
	
	
	
	private int getTotalTimeToThisStep(int StepNumber){
		int totalTime = 0;
		for(int i=0; i<StepNumber; i++ ){
			totalTime+= steps.get(i).getStepDuration();
		}
		return totalTime;
		
	}
	
	
	
	private  String convertValueDurationToText(int totalTime){
		double timeInMins= ((double)totalTime)/60;
		DecimalFormat df = new DecimalFormat("#");
		
		return (timeInMins>=2)? df.format(timeInMins)+" mins":df.format(timeInMins)+" min";
		
	}
	
	
	
	private int getTotalDistanceToThisStep(int StepNumber){
		int totalDistance = 0;
		for(int i=0; i<StepNumber; i++ ){
			totalDistance+= steps.get(i).getStepDistance();
		}
		return totalDistance;
		
	}
	
	private  String convertValueDistanceToText(int totalDistance){
		double distanceInKm= ((double)totalDistance)/1000;
		DecimalFormat df = new DecimalFormat("#.#");
		
		return df.format(distanceInKm)+" km";
	}
	
}
