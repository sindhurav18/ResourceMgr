package action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
// import java.util.Arrays;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import action.AntCO;






import com.amazonaws.services.sqs.model.DeleteMessageRequest;

import action.AntColonyOptimization.Record;
import action.SolutionWriter;
import action.AntColonyOptimization.WalkedWay;
import cern.jet.random.Uniform;

class SolutionWriter {

	/**
	 * @param args
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {

		int[] arr = new int[] { 1 };

		final BufferedReader br = new BufferedReader(new FileReader(new File("berlin52.tsp")));

		final ArrayList<Record> records = new ArrayList<Record>();

		boolean readAhead = false;
		String line;
		while ((line = br.readLine()) != null) {

			if (line.equals("EOF")) {
				break;
			}

			if (readAhead) {
				String[] split = line.split(" ");
				records.add(new AntColonyOptimization.Record(Double.parseDouble(split[1]), Double
						.parseDouble(split[2])));
			}

			if (line.equals("NODE_COORD_SECTION")) {
				readAhead = true;
			}
		}

		br.close();

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("my.tsp")));

		for (int j = 0; j < arr.length; j++) {
			int i = arr[j];
			writer.write(records.get(i).x + " " + records.get(i).y + "\n");
		}
		writer.write(records.get(arr[0]).x + " " + records.get(arr[0]).y + "\n");
		writer.flush();
		writer.close();
	}
}

class Agent implements Callable<WalkedWay> {

	private final AntColonyOptimization instance;
	private double distanceWalked = 0.0d;
	private final int start;
	private final boolean[] visited;
	private final int[] way;
	private int toVisit;
	private final Random random = new Random(System.nanoTime());

	// private final Normal random = new Normal(0.0d, 1.0d, new
	// MersenneTwister(new Date()));

	public Agent(AntColonyOptimization instance, int start) {
		super();
		this.instance = instance;
		//System.out.print("lol"+instance.matrix.length);
		this.visited = new boolean[instance.matrix.length];
		
		//System.out.println("start= "+start);
		
		visited[start] = true;
		toVisit = visited.length - 1;
		this.start = start;
		this.way = new int[visited.length];
	}

	// TODO really needs improvement
	private final int getNextProbableNode(int y) {
		if (toVisit > 0) {
			int danglingUnvisited = -1;
			final double[] weights = new double[visited.length];

			double columnSum = 0.0d;
			for (int i = 0; i < visited.length; i++) {
				columnSum += Math.pow(instance.readPheromone(y, i),
						AntColonyOptimization.ALPHA)
						* Math.pow(instance.invertedMatrix[y][i],
								AntColonyOptimization.BETA);
			}

			double sum = 0.0d;
			for (int x = 0; x < visited.length; x++) {
				if (!visited[x]) {
					weights[x] = calculateProbability(x, y, columnSum);
					sum += weights[x];
					danglingUnvisited = x;
				}
			}

			if (sum == 0.0d)
				return danglingUnvisited;

			// weighted indexing stuff
			double pSum = 0.0d;
			for (int i = 0; i < visited.length; i++) {
				pSum += weights[i] / sum;
				weights[i] = pSum;
			}

			final double r = random.nextDouble();
			for (int i = 0; i < visited.length; i++) {
				if (!visited[i]) {
					if (r <= weights[i]) {
						return i;
					}
				}
			}

		}
		return -1;
	}

	// test method
	@SuppressWarnings("unused")
	private final int calculateChoice(double[] probabilityDistr, Random rnd) {
		double rndNumber = rnd.nextDouble();
		int counter = -1;

		while (rndNumber > 0) {
			rndNumber -= probabilityDistr[++counter];
		}

		return counter;
	}

	/*
	 * (pheromones ^ ALPHA) * ((1/length) ^ BETA) divided by the sum of all
	 * rows.
	 */
	private final double calculateProbability(int row, int column, double sum) {
		final double p = Math.pow(instance.readPheromone(column, row),
				AntColonyOptimization.ALPHA)
				* Math.pow(instance.invertedMatrix[column][row],
						AntColonyOptimization.BETA);
		return p / sum;
	}

	@Override
	public final WalkedWay call() throws Exception {

		int lastNode = start;
		int next = start;
		int i = 0;
		while ((next = getNextProbableNode(lastNode)) != -1) {
			way[i] = lastNode;
			i++;
			distanceWalked += instance.matrix[lastNode][next];
			final double phero = (AntColonyOptimization.Q / (distanceWalked));
			instance.adjustPheromone(lastNode, next, phero);
			visited[next] = true;
			lastNode = next;
			toVisit--;
		}
		distanceWalked += instance.matrix[lastNode][start];
		way[i] = lastNode;

		return new WalkedWay(way, distanceWalked);
	}
}

public final class AntColonyOptimization {

	// greedy
	public static final double ALPHA = -0.2d;
	// rapid selection
	public static final double BETA = 9.6d;

	// heuristic parameters
	public static final double Q = 0.0001d; // somewhere between 0 and 1
	public static final double PHEROMONE_PERSISTENCE = 0.3d; // between 0 and 1
	public static final double INITIAL_PHEROMONES = 0.8d; // can be anything

	// use power of 2
	public static final int numOfAgents = 2048 * 20;
	private static final int poolSize = Runtime.getRuntime()
			.availableProcessors();
	
	public static int[][] resource= new int[5][5];
	
	private Uniform uniform;

	private final ExecutorService threadPool = Executors
			.newFixedThreadPool(poolSize);

	private final ExecutorCompletionService<WalkedWay> agentCompletionService = new ExecutorCompletionService<WalkedWay>(
			threadPool);

	final double[][] matrix;
	final double[][] invertedMatrix;
	private final double[][] pheromones;
	private final Object[][] mutexes;

	public AntColonyOptimization() throws IOException {
		// read the matrix
		matrix = readMatrixFromFile();
		invertedMatrix = invertMatrix();
		pheromones = initializePheromones();
		mutexes = initializeMutexObjects();
		// (double min, double max, int seed)
		uniform = new Uniform(0, matrix.length - 1,
				(int) System.currentTimeMillis());
	}

	private final Object[][] initializeMutexObjects() {
		final Object[][] localMatrix = new Object[matrix.length][matrix.length];
		int rows = matrix.length;
		for (int columns = 0; columns < matrix.length; columns++) {
			for (int i = 0; i < rows; i++) {
				localMatrix[columns][i] = new Object();
			}
		}

		return localMatrix;
	}

	final double readPheromone(int x, int y) {
		 double p;
		 synchronized (mutexes[x][y]) {
		 p = pheromones[x][y];
		 }
		return p;
		//return pheromones[x][y];
	}

	final void adjustPheromone(int x, int y, double newPheromone) {
		synchronized (mutexes[x][y]) {
			final double result = calculatePheromones(pheromones[x][y],
					newPheromone);
			if (result >= 0.0d) {
				pheromones[x][y] = result;
			} else {
				pheromones[x][y] = 0;
			}
		}
	}

	private final double calculatePheromones(double current, double newPheromone) {
		final double result = (1 - AntColonyOptimization.PHEROMONE_PERSISTENCE)
				* current + newPheromone;
		return result;
	}

	final void adjustPheromone(int[] way, double newPheromone) {
		synchronized (pheromones) {
			for (int i = 0; i < way.length - 1; i++) {
				pheromones[way[i]][way[i + 1]] = calculatePheromones(
						pheromones[way[i]][way[i + 1]], newPheromone);
			}
			pheromones[way[way.length - 1]][way[0]] = calculatePheromones(
					pheromones[way.length - 1][way[0]], newPheromone);
		}
	}

	private final double[][] initializePheromones() {
		final double[][] localMatrix = new double[matrix.length][matrix.length];
		int rows = matrix.length;
		for (int columns = 0; columns < matrix.length; columns++) {
			for (int i = 0; i < rows; i++) {
				localMatrix[columns][i] = INITIAL_PHEROMONES;
			}
		}

		return localMatrix;
	}

	private final double[][] readMatrixFromFile() throws IOException {

		final BufferedReader br = new BufferedReader(new FileReader(new File(
				"berlin52.tsp")));

		final LinkedList<Record> records = new LinkedList<Record>();

		boolean readAhead = false;
		String line;
		while ((line = br.readLine()) != null) {

			if (line.equals("EOF")) {
				break;
			}

			if (readAhead) {
				String[] split = line.trim().split(" ");
				records.add(new Record(Double.parseDouble(split[1].trim()),
						Double.parseDouble(split[2].trim())));
			}

			if (line.equals("NODE_COORD_SECTION")) {
				readAhead = true;
			}
		}

		br.close();

		final double[][] localMatrix = new double[records.size()][records
				.size()];

		int rIndex = 0;
		for (Record r : records) {
			int hIndex = 0;
			for (Record h : records) {
				localMatrix[rIndex][hIndex] = calculateEuclidianDistance(r.x,
						r.y, h.x, h.y);
			/*	if( rIndex != hIndex)
					System.out.print(localMatrix[rIndex][hIndex] +"	");
				else
					System.out.print("000.0000000000000"); */
				hIndex++;
			}
			System.out.println();
			rIndex++;
		}

		return localMatrix;
	}

	private final double[][] invertMatrix() {
		double[][] local = new double[matrix.length][matrix.length];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				local[i][j] = invertDouble(matrix[i][j]);
			}
		}
		return local;
	}

	private final double invertDouble(double distance) {
		if (distance == 0)
			return 0;
		else
			return 1.0d / distance;
	}

	private final double calculateEuclidianDistance(double x1, double y1,
			double x2, double y2) {
		return Math
				.abs((Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2))));
	}

	final String start() throws InterruptedException, ExecutionException {

		WalkedWay bestDistance = null;

		int agentsSend = 0;
		int agentsDone = 0;
		int agentsWorking = 0;
		for (int agentNumber = 0; agentNumber < numOfAgents; agentNumber++) {
			agentCompletionService.submit(new Agent (this, getGaussianDistributionRowIndex()));
			agentsSend++;
			agentsWorking++;
			while (agentsWorking >= poolSize) {
				WalkedWay way = agentCompletionService.take().get();
				if (bestDistance == null
						|| way.distance < bestDistance.distance) {
					bestDistance = way;
					System.out.println("Agent returned with new best distance of: "+ way.distance +" Available processors:"+ poolSize);
				}
				agentsDone++;
				agentsWorking--;
			}
		}
		final int left = agentsSend - agentsDone;
		System.out.println("Waiting for " + left + " agents to finish their random walk!");

		for (int i = 0; i < left; i++) {
			WalkedWay way = agentCompletionService.take().get();
			if (bestDistance == null || way.distance < bestDistance.distance) {
				bestDistance = way;
				System.out.println("Agent returned with new best distance of: "
						+ way.distance +"Available processors:"+ poolSize);
				
			}
		}

		threadPool.shutdownNow();
		System.out.println("Found best so far: " + bestDistance.distance);
		final int reserved[]=new int[bestDistance.way.length];
		//printing the nodes
		int i;
		for(i=0;i<bestDistance.way.length;i++){ 
			if(reserved[i]==0)
			bestDistance.way[i]=bestDistance.way[i]+1;
		// System.out.println(bestDistance.way[i]);
		}
		reserved[bestDistance.way.length - 1]=1;
		int allocateHostID=bestDistance.way[bestDistance.way.length - 1];
		String allocatedHostID = null;
		if(allocateHostID==1)
			allocatedHostID="i-63b02569" ;
		else if (allocateHostID==2)
			allocatedHostID="i-84903888";
		else if (allocateHostID==3)
			allocatedHostID="i-c97cd4c5";
		else if (allocateHostID==4)
			allocatedHostID="i-f3bc29f9";
		else if (allocateHostID==5)
			allocatedHostID="t-84903889";
	/*	for(int j=0;j<bestDistance.way.length;j++){
			if(reserved[j]!=0)
				reserved[bestDistance.way.length]=reserved[bestDistance.way.length-1];
			System.out.println(reserved[bestDistance.way.length]);
		}*/
		System.out.println("Reserved instance : "+allocatedHostID);
		
		return allocatedHostID;

	}

	private final int getGaussianDistributionRowIndex() {
		return uniform.nextInt();
	}

	static class Record {
		double x;
		double y;

		public Record(double x, double y) {
			super();
			this.x = x;
			this.y = y;
		}
	}

	static class WalkedWay {
		int[] way;
		double distance;

		public WalkedWay(int[] way, double distance) {
			super();
			this.way = way;
			this.distance = distance;
		}
	}
	
	
			

	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException,
			InterruptedException, ExecutionException {

		long start = System.currentTimeMillis();
		AntColonyOptimization antColonyOptimization = new AntColonyOptimization();
		antColonyOptimization.start();
		SolutionWriter solutionWriter= new SolutionWriter();
		System.out.println("Took: " + (System.currentTimeMillis() - start)
				+ " ms!");
	}

}