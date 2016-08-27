package com.example.khumalo.appui.Model;

public class Step {
	public Distance stepDistance;
	public Duration stepDuration;
	public String stepPolyline;
	
	public Step(Distance stepDistance,Duration stepDuration,String stepPolyline){
		this.stepDistance = stepDistance;
		this.stepDuration = stepDuration;
		this.stepPolyline = stepPolyline;
	}
	
	public int getStepDistance(){
		return stepDistance.getValueDistance();
	}

	public int getStepDuration(){
		return stepDuration.getDurationValue();
	}
	
	public String getStepPolyline(){
		return stepPolyline;
	}
	
}
