package model;

import java.lang.Math;
import java.util.ArrayList;

public class Location {
	public float x;
	public float y;
	public float exposure;
	public Location(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public double euclidDistanceTo(Location loc2) {
		return Math.sqrt(Math.pow((this.x - loc2.x), 2) + Math.pow((this.y - loc2.y), 2));
	} 
	
	public float exposure(Sensor sensor) {
		double distance = this.euclidDistanceTo(new Location(sensor.x, sensor.y));
		return distance>sensor.r?0:1;
	}
	
	public float sumExposure(ArrayList<Sensor> listSensors) {
		float sumE = 0;
		for (Sensor sensor: listSensors) {
			sumE += this.exposure(sensor);
		}
		return sumE;
	}
}