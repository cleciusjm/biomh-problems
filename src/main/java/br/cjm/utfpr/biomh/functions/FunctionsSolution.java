package br.cjm.utfpr.biomh.functions;

import br.cjm.utfpr.biomh.functions.algorithm.Agent;

import java.util.Arrays;
import java.util.concurrent.Callable;

public class FunctionsSolution implements Comparable<FunctionsSolution> {

	private final Class<? extends Callable<FunctionsSolution>> algorithm;
	private final Agent best;
	private final double[] avgFitnessHistory;
	private final long start;
	private final long end;
	private int execIterations;

	public FunctionsSolution(Class<? extends Callable<FunctionsSolution>> algorithm, Agent best, double[] avgFitnessHistory, int execIterations,
							 long start, long end) {
		this.algorithm = algorithm;
		this.best = best;
		this.avgFitnessHistory = avgFitnessHistory;
		this.execIterations = execIterations;
		this.start = start;
		this.end = end;
	}

	public Class<? extends Callable<FunctionsSolution>> getAlgorithm() {
		return algorithm;
	}

	public double[] getAvgFitnessHistory() {
		return avgFitnessHistory;
	}

	public Agent getBest() {
		return best;
	}

	public double getBestFitness() {
		return best.fitness();
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
	public int compareTo(FunctionsSolution o) {
		return Double.compare(this.getBestFitness(), o.getBestFitness());
	}

	@Override
	public String toString() {
		return String.format("%s: best %s com fitness %s em %d ms e %d iteracões", this.algorithm.getSimpleName(),
				Arrays.toString(best.values()), best.fitness(), this.getTime(), this.execIterations);
	}

}
