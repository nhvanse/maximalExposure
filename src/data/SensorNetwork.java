package data;

import java.util.ArrayList;
import java.util.Random;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Scanner;

import model.Location;
import model.Sensor;

// Package model nen chua nhung material, nhung class nho nhat cua chuong trinh thoi nhe
// Nen de cai nay ra 1 package rieng, de quan ly, logic hon
//
// Class nay code tot, on dinh
public class SensorNetwork {

	public float wOfField, hOfField; // width and height of sensor network field.
	public int numOfSensors;
	public ArrayList<Sensor> listSensors;
	public float speed;
	public float limitTime;
	public float maxE; // maximum exposure on each location
	public Location start;
	public Location dest;

	// Luoi
	public static class Grid {
		public Location vertices[][]; // vertices of grid
		public int L; // numbers of rows and columns of grid
		public int K;
	}

	public Grid grid;

	public void randomInitial(float wOfField, float hOfField, int numOfSensors, float RmaxOfSensors, float speed, float limitTime, float maxE) {
		this.wOfField = wOfField;
		this.hOfField = hOfField;
		this.numOfSensors = numOfSensors;
		this.speed = speed;
		this.limitTime = limitTime;
		this.maxE = maxE;

		Random rand = new Random();
		this.start = new Location((float) (Math.round(wOfField * rand.nextFloat() / 0.5) * 0.5), 0);
		this.dest = new Location((float) (Math.round(wOfField * rand.nextFloat() / 0.5) * 0.5), hOfField);

		this.listSensors = new ArrayList<Sensor>();
		for (int i = 0; i < numOfSensors; i++)
			this.listSensors.add(new Sensor(wOfField * rand.nextFloat(), hOfField * rand.nextFloat(), RmaxOfSensors * rand.nextFloat()));
	}

	public void initialFromFile(String file) throws Exception {
		Scanner sc = new Scanner(new BufferedReader(new FileReader(file)));

		String[] line = sc.nextLine().trim().split(" ");
		this.wOfField = Float.parseFloat(line[0]);
		this.hOfField = Float.parseFloat(line[1]);

		line = sc.nextLine().trim().split(" ");
		this.numOfSensors = Integer.parseInt(line[0]);

		this.listSensors = new ArrayList<Sensor>();
		for (int i = 0; i < this.numOfSensors; i++) {
			line = sc.nextLine().trim().split(" ");
			float x = Float.parseFloat(line[0]);
			float y = Float.parseFloat(line[1]);
			float z = Float.parseFloat(line[2]);
			this.listSensors.add(new Sensor(x, y, z));
		}

		line = sc.nextLine().trim().split(" ");
		this.speed = Float.parseFloat(line[0]);

		line = sc.nextLine().trim().split(" ");
		this.start = new Location(Float.parseFloat(line[0]), Float.parseFloat(line[1]));

		line = sc.nextLine().trim().split(" ");
		this.dest = new Location(Float.parseFloat(line[0]), Float.parseFloat(line[1]));

		line = sc.nextLine().trim().split(" ");
		this.limitTime = Float.parseFloat(line[0]);

		line = sc.nextLine().trim().split(" ");
		this.maxE = Float.parseFloat(line[0]);

		sc.close();

	}

	public void saveToFile(String fileName) throws Exception {
		File newFile = new File(fileName);
		newFile.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		writer.write(this.wOfField + " " + this.hOfField);
		writer.newLine();

		writer.write(this.numOfSensors + "");
		writer.newLine();

		for (int i = 0; i < this.numOfSensors; i++) {
			Sensor sensor = this.listSensors.get(i);
			writer.write(sensor.x + " " + sensor.y + " " + sensor.r);
			writer.newLine();
		}

		writer.write(this.speed + "");
		writer.newLine();

		writer.write(this.start.x + " " + this.start.y);
		writer.newLine();

		writer.write(this.dest.x + " " + this.dest.y);
		writer.newLine();

		writer.write(this.limitTime + "");
		writer.newLine();

		writer.write(this.maxE + "");

		writer.flush();
		writer.close();
		System.out.println("Completely saved!");
	}

	public void printInfo() {
		System.out.println("===Infomation of sensor network===");
		System.out.println("Width: " + this.wOfField + "\t Height: " + this.hOfField);
		System.out.println("Number of sensors: " + this.numOfSensors);
		System.out.println("List sensors:");
		for (int i = 0; i < numOfSensors; i++) {
			Sensor sensor = this.listSensors.get(i);
			System.out.printf("\tSensor %3d:  %10f %10f %10f\n", (i + 1), sensor.x, sensor.y, sensor.r);
		}
		System.out.println();
		System.out.println("Speed: " + this.speed);
		System.out.println("Start: " + this.start.x + "  " + this.start.y);
		System.out.println("Dest: " + this.dest.x + "  " + this.dest.y);
		System.out.println("Limit time: " + this.limitTime);
		System.out.println("Max exposure of each location: " + this.maxE);
	}

	public void makeGrid(float deltaS) {
		int K = Math.round(this.wOfField / deltaS); // number of columns
		int L = Math.round(this.hOfField / deltaS); // number of rows
		Location[][] vertices = new Location[L + 1][K + 1];
		for (int i = 0; i <= L; i++) {
			for (int j = 0; j <= K; j++) {
				Location newLoc = new Location(j * deltaS, i * deltaS);
				newLoc.exposure = newLoc.sumExposure(this.listSensors);
				vertices[i][j] = newLoc;
			}
		}
		this.grid = new Grid();
		this.grid.K = K;
		this.grid.L = L;
		this.grid.vertices = vertices;
	}

	// Ham main test, kha tot ^
	public static void main(String[] args) throws Exception {
		SensorNetwork sNet = new SensorNetwork();
		int numberSensors = 200;
		sNet.randomInitial(100, 100, numberSensors, 7, 5, 100, numberSensors - 2);
		sNet.saveToFile("./input/" + numberSensors + ".txt");
//		sNet.initialFromFile("./input/"+numberSensors+".txt");
		sNet.printInfo();
	}

}


