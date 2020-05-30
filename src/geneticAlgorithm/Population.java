package geneticAlgorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

import data.SensorNetwork;
import model.Location;
import model.Sensor;

public class Population {
	ArrayList<Individual> listIndividual;
	int geneLength;
	SensorNetwork net;

	public Population(int geneLength, SensorNetwork net) {
		this.geneLength = geneLength;
		this.net = net;
	}

	public void randomInitial(int numOfIndividual) {
		System.out.println("Random initial.");
		listIndividual = new ArrayList<Individual>();
		for (int i = 0; i < numOfIndividual; i++) {
			Individual individ = new Individual(geneLength, net);
			individ.randomInitial();
			listIndividual.add(individ);
		}
	}

	public Individual getBestIndividual() {
		Individual bestIndividual = this.listIndividual.get(0);
		for (Individual individ : this.listIndividual) {
			if (individ.exposure > bestIndividual.exposure) {
				bestIndividual = individ;
			}
		}
		return bestIndividual;
	}

	public void developPopulation(int maxNumberOfIndividual) {
		System.out.println("Developing population by make hybridization.");
		Random rand = new Random();
		while (this.listIndividual.size() < maxNumberOfIndividual) {
			int randomIndex = rand.nextInt(this.listIndividual.size());
			Individual fatherIndividual = this.listIndividual.get(randomIndex);
			randomIndex = rand.nextInt(this.listIndividual.size());
			Individual motherIndividual = this.listIndividual.get(randomIndex);

			Individual childrenIndividual = fatherIndividual.makeHybrid(motherIndividual);
			this.listIndividual.add(childrenIndividual);
		}
	}

	public void makeMutationAll() {
		System.out.println("Making mutation all individual.");
		Random rand = new Random();
		for (Individual individ : this.listIndividual) {
			if (rand.nextInt(10)==0) individ.makeMutation();
		}
	}

	public void makeSelection() {
		System.out.println("Making selection.");
		this.listIndividual.sort((a, b) -> Float.compare(a.exposure, b.exposure));
		
		for (int i = this.listIndividual.size() / 3 -1 ; i >= 0; i--) {
			this.listIndividual.remove(i);
		}
	}

	public void writeToFile(String fileName) throws Exception {
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

		writer.write(this.geneLength + "");
		writer.newLine();

		writer.write(this.listIndividual.size() + "");
		writer.newLine();

		for (Individual individ : this.listIndividual) {
			for (Location loc : individ.path) {
				writer.write(loc.x + "," + loc.y + " ");
			}
			writer.newLine();
		}

		writer.flush();
		writer.close();
		System.out.println("Completely saved population!");
	}

	public static void main(String[] args) throws Exception {
		SensorNetwork net = new SensorNetwork();
		net.initialFromFile("./input/200.txt");
		int geneLength = 200;
		int maxNumOfIndividual = 1000;
		int numOfInitIndividual = maxNumOfIndividual;
		int maxCheckGA = 10;

		int checkGA = 0;
		float maxGlobalExposure = 0;
		float maxLocalExposure = 0;

		Population pop = new Population(geneLength, net);
		pop.randomInitial(numOfInitIndividual);

		int iter = 0;
		
		
		while (checkGA < maxCheckGA) {
			System.out.println("generation "+ ++iter);
			pop.makeSelection();
			pop.makeMutationAll();
			pop.developPopulation(maxNumOfIndividual);
			

			maxLocalExposure = pop.getBestIndividual().exposure;

			System.out.println(maxLocalExposure + "\n");

			if (maxGlobalExposure < maxLocalExposure) {
				maxGlobalExposure = maxLocalExposure;
				checkGA = 0;
			} else {
				checkGA += 1;
			}
		}
		
		pop.writeToFile("./output/pop.txt");
		System.out.println("best: " + pop.getBestIndividual().exposure);
		Individual.saveToFile("./output/bestIndividual.txt", net, pop.getBestIndividual().path,
				pop.getBestIndividual().exposure);
		System.out.println("iter: " + iter);
	}
}
