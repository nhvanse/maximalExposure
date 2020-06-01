package geneticAlgorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

import model.*;
import data.SensorNetwork;

public class Individual {
	// input
	int geneLength;
	SensorNetwork net;

	// output
	ArrayList<Location> path;
	float exposure;

	public Individual(int geneLength, SensorNetwork net) {
		this.geneLength = geneLength;
		this.net = net;
		this.path = new ArrayList<Location>();
		this.exposure = 0;
	}

	public boolean timeCondition(Location nextLoc) {
		Location currLoc = path.get(path.size() - 1);
		float deltaT = net.limitTime / (geneLength - 1);
		float currTime = (path.size() - 1) * deltaT;
		float nextShostestTime = (float) (currLoc.euclidDistanceTo(nextLoc) + nextLoc.euclidDistanceTo(net.dest))
				/ net.maxSpeed;

		return nextShostestTime < net.limitTime - currTime;
	}

	private float computeExposure() {
		float deltaT = net.limitTime / (geneLength - 1);
		float sumExposureAtLocation = 0;
		for (Location loc : this.path) {
			sumExposureAtLocation += loc.sumExposure(net.listSensors);
		}
		return (sumExposureAtLocation - net.dest.sumExposure(net.listSensors)) * deltaT;
	}

	public void randomInitial() {
		float deltaT = net.limitTime / (geneLength - 1);

		path.add(net.start);
		Location currLoc = net.start;
		Location nextLoc;

		Random rand = new Random();
		while (path.size() < geneLength - 1) {
			float distance = rand.nextFloat() * (net.maxSpeed * deltaT); // 0 <= distance <= maxDistance
			float deltaX, deltaY;

			if (path.size() > 1) {
				int currIndex = path.size() - 1;
				int prevIndex = currIndex - 1;
				float dX = (path.get(currIndex).x - path.get(prevIndex).x);
				float dY = (path.get(currIndex).y - path.get(prevIndex).y);
				float dis = (float) Math.sqrt(dX * dX + dY * dY);
				double phi;
				// góc của đoạn đường tiếp theo xấp xỉ góc của đoạn đường trước đó
				phi = Math.acos(dX / dis) + Math.pow(-1, rand.nextInt(2)) * rand.nextFloat() * Math.PI / 12;
				if (dY < 0) phi += Math.PI;
//				if (dY < 0) phi = -phi;
				
				
				deltaX = (float) (Math.cos(phi) * distance);
				deltaY = (float) (Math.sin(phi) * distance);
			} else {
				double phi = rand.nextDouble() * Math.PI;
				deltaX = (float) (Math.cos(phi) * distance);
				deltaY = (float) (Math.sin(phi) * distance);
			}

			if (currLoc.x + deltaX > net.wOfField || currLoc.x + deltaX < 0)
				deltaX = -deltaX;
			if (currLoc.y + deltaY > net.hOfField || currLoc.y + deltaY < 0)
				deltaY = -deltaY;
//			
			nextLoc = new Location(currLoc.x + deltaX, currLoc.y + deltaY);

			if (timeCondition(nextLoc)) {
				path.add(nextLoc);
				currLoc = nextLoc;
			} else {
				break;
			}
		}

		// almost out of time
		while (path.size() < geneLength - 1) {
			float nextX = currLoc.x + (net.dest.x - currLoc.x) / (geneLength - path.size());
			float nextY = currLoc.y + (net.dest.y - currLoc.y) / (geneLength - path.size());
			nextLoc = new Location(nextX, nextY);
			path.add(nextLoc);
			currLoc = nextLoc;
		}
		path.add(net.dest);
		currLoc = net.dest;
		this.exposure = this.computeExposure();
	}

	public void makeMutation() { // change a random location by symmetrical one if can increase exposure)
		Random rand = new Random();
		int i = rand.nextInt(geneLength - 2) + 1;
		Location currLoc = path.get(i);
		Location prevLoc = path.get(i - 1);
		Location nextLoc = path.get(i + 1);
		Location medianLoc = new Location((prevLoc.x + nextLoc.x) / 2, (prevLoc.y + nextLoc.y) / 2);

		Location changedLoc = new Location((2 * medianLoc.x - currLoc.x), (2 * medianLoc.y - currLoc.x));
		if (net.exposureAt(changedLoc) > net.exposureAt(currLoc)) {
			path.set(i, changedLoc);
		}
		this.exposure = this.computeExposure();
	}

	public Individual makeHybrid(Individual individ2) { // one crossover hybrid
		float deltaT = net.limitTime / (geneLength - 1);

		Random rand = new Random();
		int indexHybridGene1 = rand.nextInt(geneLength-1);
		int indexHybridGene2 = indexHybridGene1 + 1;

		Location loc1 = this.path.get(indexHybridGene1);
		Location loc2 = individ2.path.get(indexHybridGene2);

		float maxDistance = (indexHybridGene2 - indexHybridGene1) * deltaT * net.maxSpeed;
		while (loc1.euclidDistanceTo(loc2) > maxDistance) {
			indexHybridGene2++;
			maxDistance = (indexHybridGene2 - indexHybridGene1) * deltaT * net.maxSpeed;
			
			if (indexHybridGene2 < this.geneLength) {
				loc2 = individ2.path.get(indexHybridGene2);
			} else {
				break;
			}

		}

		if (indexHybridGene2 == this.geneLength) {
			return this;
		} else {
			Individual newIndividual = new Individual(geneLength, net);
			Location currLoc = this.path.get(0);
			for (int i = 0; i < geneLength; i++) {
				if (i <= indexHybridGene1) {
					currLoc = this.path.get(i);
					newIndividual.path.add(currLoc);
				} else if (i >= indexHybridGene2) {
					currLoc = individ2.path.get(i);
					newIndividual.path.add(currLoc);
				} else {
					float nextX = currLoc.x + (individ2.path.get(indexHybridGene2).x - currLoc.x)
							/ (indexHybridGene2 - newIndividual.path.size());
					float nextY = currLoc.y + (individ2.path.get(indexHybridGene2).y - currLoc.y)
							/ (indexHybridGene2 - newIndividual.path.size());
					currLoc = new Location(nextX, nextY);
					newIndividual.path.add(currLoc);
				}
			}
			newIndividual.exposure = newIndividual.computeExposure();
			return newIndividual;
		}
	}

	public void printInfo() {
		System.out.println("GeneLength: " + this.geneLength);
//		net.printInfo();
		System.out.print("Path: ");
		for (Location loc : path) {
			System.out.printf("(%10f, %10f), ", loc.x, loc.y);
		}
		System.out.println();
		System.out.println("Exposure: " + this.computeExposure());
		System.out.println();
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

		for (Sensor sensor : net.listSensors) {
			writer.write(sensor.x + " " + sensor.y + " " + sensor.r);
			writer.newLine();
		}

		writer.write(net.maxSpeed + "");
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

		for (Location loc : finalpath) {
			writer.write(loc.x + " " + loc.y);
			writer.newLine();
		}

		writer.flush();
		writer.close();
		System.out.println("Completely saved!");
	}

	public static void main(String[] args) throws Exception {
		SensorNetwork net = new SensorNetwork();
		net.initialFromFile("./input/200.txt");
		int geneLength = 500;

		Individual individ = new Individual(geneLength, net);
		individ.randomInitial();
		individ.printInfo();

		Individual individ2 = new Individual(geneLength, net);
		individ2.randomInitial();
		individ2.printInfo();
		Individual individ3 = individ.makeHybrid(individ2);
		individ3.printInfo();

//
		saveToFile("./output/output1.txt", net, individ.path, individ.exposure);
		saveToFile("./output/output2.txt", net, individ2.path, individ2.exposure);
		saveToFile("./output/output3.txt", net, individ3.path, individ3.exposure);
	}
}
