package model;

import java.util.ArrayList;

public class Location extends Point {

	public float exposure;

	public Location(float x, float y) {
		super(x, y);
	}

	public float exposure(Sensor sensor) {
		return this.euclidDistanceTo(new Location(sensor.x, sensor.y)) > sensor.r ? 0 : 1;
	}

	public float sumExposure(ArrayList<Sensor> listSensors) {
		float sumE = 0;
		for (Sensor sensor : listSensors)
			sumE += this.exposure(sensor);
		return sumE;
	}

}
