package br.cjm.utfpr.biomh.tsp;

import java.util.Arrays;
import java.util.concurrent.Callable;

public class TspSolution implements Comparable<TspSolution> {

	private final Class<? extends Callable<TspSolution>> algorithm;
	private final int[] best;
	private final double bestDistance;
	private final double[] avgFitnessHistory;
	private final int execIterations;
	private final long start;
	private final long end;

	public TspSolution(Class<? extends Callable<TspSolution>> algorith, int[] best, double bestDistance, double[] avgFitnessHistory,
					   int execIterations, long start, long end) {
		this.algorithm = algorith;
		this.best = best;
		this.bestDistance = bestDistance;
		this.avgFitnessHistory = avgFitnessHistory;
		this.execIterations = execIterations;
		this.start = start;
		this.end = end;
	}

	public Class<? extends Callable<TspSolution>> getAlgorithm() {
		return algorithm;
	}

	public double[] getAvgFitnessHistory() {
		return avgFitnessHistory;
	}

	public int[] getBest() {
		return best;
	}

	public double getBestDistance() {
		return bestDistance;
	}

	public double getBestFitness() {
		return 1.0 / bestDistance;
	}

	public int getExecIterations() {
		return execIterations;
	}

	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	public long getTime() {
		return end - start;
	}

	@Override
	public int compareTo(TspSolution o) {
		return Double.compare(this.getBestFitness(), o.getBestFitness());
	}

	@Override
	public String toString() {
		return String.format("%s: best fitness %s em %d ms e %d iteracões - %s", this.algorithm.getSimpleName(), bestDistance,
				this.getTime(), this.execIterations, Arrays.toString(best));
	}
}
