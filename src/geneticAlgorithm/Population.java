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
        randomInitial(numOfIndividual, false);
    }

    public void randomInitial(int numOfIndividual, boolean verbose) {
        if (verbose) System.out.println("Random initial.");
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
        developPopulation(maxNumberOfIndividual, false);
    }

    public void developPopulation(int maxNumberOfIndividual, boolean verbose) {
        if (verbose) System.out.println("Developing population by make hybridization.");
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
        makeMutationAll(false);
    }

    public void makeMutationAll(boolean verbose) { // make mutation 100%
        if (verbose) System.out.println("Making mutation all individual.");
        for (Individual individ : this.listIndividual) {
            individ.makeMutation();
        }
    }

    public void makeSelection() {
        makeSelection(false);
    }

    public void makeSelection(boolean verbose) { // keep half of Population
        if (verbose) System.out.println("Making selection.");
        this.listIndividual.sort((a, b) -> Float.compare(a.exposure, b.exposure));

        for (int i = this.listIndividual.size() / 2 - 1; i >= 0; i--) {
            this.listIndividual.remove(i);
        }
    }

    public void writeToFile(String fileName) throws Exception {
        writeToFile(fileName, false);
    }

    public void writeToFile(String fileName, boolean verbose) throws Exception {
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
        if (verbose) System.out.println("Completely saved population!");
    }

    public static float[] GA(String inputFile, int geneLength, int maxNumOfIndividual, String outputFile) throws Exception {
        return GA(inputFile, geneLength, maxNumOfIndividual, outputFile, false);
    }

    public static float[] GA(String inputFile, int geneLength, int maxNumOfIndividual, String outputFile, boolean verbose) throws Exception {
        long startTime = System.currentTimeMillis();

        SensorNetwork net = new SensorNetwork();
        net.initialFromFile(inputFile);

        net.maxSpeed = 5;
        net.limitTime = 100;
        net.maxE = 50;
        net.wOfField = 100;
        net.hOfField = 100;


        int numOfInitIndividual = maxNumOfIndividual;
        int maxCheckGA = 10;


        int checkGA = 0;
        float maxGlobalExposure = 0;
        float maxLocalExposure = 0;

        Population pop = new Population(geneLength, net);
        pop.randomInitial(numOfInitIndividual);

        int iter = 0;

        while (checkGA < maxCheckGA) {
            ++iter;
            if (verbose) System.out.println("generation " + iter);

            pop.makeMutationAll();
            pop.developPopulation(maxNumOfIndividual);
            pop.makeSelection();


            maxLocalExposure = pop.getBestIndividual().exposure;

            if (verbose) System.out.println("exposure: " + maxLocalExposure + "\n");

            if (maxGlobalExposure < maxLocalExposure) {
                maxGlobalExposure = maxLocalExposure;
                checkGA = 0;
            } else {
                checkGA += 1;
            }
        }

        Individual.saveToFile(outputFile, net, pop.getBestIndividual().path,
                pop.getBestIndividual().exposure);

        long endTime = System.currentTimeMillis();
        float runTime = (float) ((endTime - startTime) / 1000.0);

        return new float[]{pop.getBestIndividual().exposure, (float) iter, runTime};
    }


    public static void main(String[] args) throws Exception {
        int geneLength = 200;
        int maxNumOfIndividual = 5000;


        String inputFolder = "./input/";
        String outputFolder = "./output/";


        File logFile = new File("./logs/log_time_" + System.currentTimeMillis() + ".txt");
        logFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));

        int[] listNumberSensors = {10, 20, 50, 100, 200};
        int numOfTestEachCase = 10;

        for (int numberSensors : listNumberSensors) {
            File outputSubFolder = new File(outputFolder + numberSensors);
            outputSubFolder.mkdir();

            System.out.println(numberSensors + " sensors:");
            writer.write(numberSensors + " sensors:\n");

            for (int i = 1; i <= numOfTestEachCase; i++) {
                String inputFile = inputFolder + numberSensors + "/data_" + numberSensors + "_" + i + ".txt";
                String outputFile = outputSubFolder.toString() + "/" + i + ".txt";
                float[] result = GA(inputFile, geneLength, maxNumOfIndividual, outputFile);
                float exposure = result[0];
                int iters = (int) result[1];
                float runTime = result[2];
                System.out.printf("\t file %2d: (exposure : %10f), %3d generations, %8.3f seconds.\n", i, exposure, iters, runTime);
                writer.write(String.format("\t file %2d: (exposure : %10f), %3d generations, %8.3f seconds.\n", i, exposure, iters, runTime));
            }
        }
        writer.flush();
        writer.close();
    }
}
