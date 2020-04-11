package randomHeuristic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

import model.*;

public class RandomHeuristic {
	SensorNetwork net;
	ArrayList<Location> path;
	float exposure;
	float currTime;
	Location currLoc;

	Location vertices[][]; // vertices of grid
	float deltaS;//
	int L, K; // numbers of rows and columns of grid

	public RandomHeuristic() {
		this.net = new SensorNetwork();
		this.path = new ArrayList<Location>();
		this.currTime = 0;
		this.exposure = 0;
	}

	public void makeGrid() {
		this.K = Math.round(this.net.wOfField / this.deltaS); // number of columns
		this.L = Math.round(this.net.hOfField / this.deltaS); // number of rows
		this.vertices = new Location[L + 1][K + 1];
		for (int i = 0; i <= this.L; i++) {
			for (int j = 0; j <= this.K; j++) {
				Location newLoc = new Location(j * deltaS, i * deltaS);
				newLoc.exposure = newLoc.sumExposure(this.net.listSensors);
				this.vertices[i][j] = newLoc;
			}
		}
	}

	public boolean timeCondition(Location nextLoc) {
		int rowIndexOfDest = Math.round(this.net.dest.y / this.deltaS);
		int columnIndexOfDest = Math.round(this.net.dest.x / this.deltaS);
		double shortestTime = (Math.abs(nextLoc.x - columnIndexOfDest * this.deltaS)
				+ Math.abs(nextLoc.y - rowIndexOfDest * this.deltaS) + this.deltaS) / this.net.speed;
		return shortestTime < (this.net.limitTime - this.currTime);
	}

	public Location randomLocation() {
		int columnIndex = Math.round(this.currLoc.x / this.deltaS);
		int rowIndex = Math.round(this.currLoc.y / this.deltaS);
		int newRowIndex, newColumnIndex;

		Random rand = new Random();

		do {
			newRowIndex = rowIndex;
			newColumnIndex = columnIndex;
			int random = rand.nextInt(4);
			if (random == 0) {
				newRowIndex = rowIndex + 1;
			} else if (random == 1) {
				newColumnIndex = columnIndex + 1;
			} else if (random == 2) {
				newRowIndex = rowIndex - 1;
			} else {
				newColumnIndex = columnIndex - 1;
			}
		} while (newColumnIndex > K || newColumnIndex < 0 || newRowIndex > L || newRowIndex < 0);

		return this.vertices[newRowIndex][newColumnIndex];
	}

	public void computeShortestPath() { // find shortest path to dest
		int rowIndexOfCurrLoc = Math.round(this.currLoc.y / this.deltaS);
		int columnIndexOfCurrLoc = Math.round(this.currLoc.x / this.deltaS);
		int rowIndexOfDest = Math.round(this.net.dest.y / this.deltaS);
		int columnIndexOfDest = Math.round(this.net.dest.x / this.deltaS);

		int tempColumnIndex = columnIndexOfCurrLoc;
		int tempRowIndex = rowIndexOfCurrLoc;
		Location nextLoc;

		while (tempColumnIndex != columnIndexOfDest || tempRowIndex != rowIndexOfDest) {
			if (tempColumnIndex != columnIndexOfDest) {
				if (tempColumnIndex < columnIndexOfDest) {
					tempColumnIndex++;
				} else {
					tempColumnIndex--;
				}
				nextLoc = this.vertices[tempRowIndex][tempColumnIndex];
				this.path.add(nextLoc);
				this.currTime += this.deltaS / this.net.speed;
				this.exposure += this.currLoc.sumExposure(this.net.listSensors) * this.deltaS / this.net.speed;
				this.currLoc = nextLoc;
			}

			if (tempRowIndex != rowIndexOfDest) {
				if (tempRowIndex < rowIndexOfDest) {
					tempRowIndex++;
				} else {
					tempRowIndex--;
				}
				nextLoc = this.vertices[tempRowIndex][tempColumnIndex];
				this.path.add(nextLoc);
				this.currTime += this.deltaS / this.net.speed;
				this.exposure += this.currLoc.sumExposure(this.net.listSensors) * this.deltaS / this.net.speed;
				this.currLoc = nextLoc;
			}
		}
	}

	public static void saveToFile(String fileName, SensorNetwork net, ArrayList<Location> finalpath, float maxExposure)
			throws Exception {
		File newFile = new File(fileName);
		newFile.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		writer.write(net.wOfField + " " + net.hOfField);
		writer.newLine();

		writer.write(net.numOfSensors + "");
		writer.newLine();

		for (int i = 0; i < net.numOfSensors; i++) {
			Sensor sensor = net.listSensors.get(i);
			writer.write(sensor.x + " " + sensor.y + " " + sensor.r);
			writer.newLine();
		}

		writer.write(net.speed + "");
		writer.newLine();

		writer.write(net.start.x + " " + net.start.y);
		writer.newLine();

		writer.write(net.dest.x + " " + net.dest.y);
		writer.newLine();

		writer.write(net.limitTime + "");
		writer.newLine();

		writer.write(net.maxE + "");
		writer.newLine();

		writer.write(maxExposure + "");
		writer.newLine();

		writer.write(finalpath.size() + "");
		writer.newLine();
		for (int i = 0; i < finalpath.size(); i++) {
			Location loc = finalpath.get(i);
			writer.write(loc.x + " " + loc.y);
			writer.newLine();
		}

		writer.flush();
		writer.close();
		System.out.println("Completely saved!");
	}

	public static void main(String[] args) throws Exception {
		String dataFile = "./input/200.txt";
		
		float maxEx = 0;
		ArrayList<Location> finalpath = new ArrayList<Location>();
		
		SensorNetwork generalNet = new SensorNetwork();
		generalNet.initialFromFile(dataFile);
		int iter = 0;
		while (iter < 1000) {
			iter++;
			RandomHeuristic rh = new RandomHeuristic();
			SensorNetwork net = rh.net;
			net.initialFromFile(dataFile);
			rh.deltaS = (float) 0.5;
			rh.makeGrid();

			int rowIndexOfStart = Math.round(net.start.y / rh.deltaS);
			int columnIndexOfStart = Math.round(net.start.x / rh.deltaS);
			rh.currLoc = rh.vertices[rowIndexOfStart][columnIndexOfStart];
			rh.path.add(rh.currLoc);

			Location nextLoc = rh.randomLocation();
			while (rh.timeCondition(nextLoc)) {
				rh.currTime += rh.deltaS / net.speed;
				rh.exposure += rh.currLoc.sumExposure(net.listSensors) * rh.deltaS / net.speed;
				rh.currLoc = nextLoc;
				rh.path.add(nextLoc);
				nextLoc = rh.randomLocation();
			}

			// almost out of time
			rh.computeShortestPath();

			if (rh.exposure > maxEx) {
				maxEx = rh.exposure;
				finalpath = rh.path;
			}
		}
		System.out.println("Data file: " + dataFile);
		System.out.println("exposure :" + maxEx);
		System.out.println(finalpath.size());
		saveToFile("./output/output.txt", generalNet, finalpath, maxEx);
	}
}