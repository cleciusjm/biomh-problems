package br.cjm.utfpr.biomh.functions;

import br.cjm.utfpr.biomh.functions.algorithm.Agent;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class FunctionsFinalSolution {
	private String name;
	private double[] avgFitness;
	private Agent best;
	private long execTime;
	private int iterations;

	public FunctionsFinalSolution(List<FunctionsSolution> solutions, int iterations) {
		avgFitness = new double[iterations];
		if (solutions.isEmpty())
			throw new IllegalArgumentException();
		this.name = solutions.get(0).getAlgorithm().getSimpleName();
		for (FunctionsSolution sol : solutions) {
			if (best == null || best.fitness() < sol.getBestFitness()) {
				this.best = sol.getBest();
			}
			this.execTime += sol.getTime();
			this.iterations += sol.getExecIterations();
			for (int i = 0; i < avgFitness.length; i++) {
				avgFitness[i] += sol.getAvgFitnessHistory()[Math.min(i, sol.getExecIterations() - 1)];
			}
		}
		this.execTime /= solutions.size();
		this.iterations /= solutions.size();
		for (int i = 0; i < avgFitness.length; i++) {
			avgFitness[i] /= solutions.size();
		}
	}

	@Override
	public String toString() {
		return String.format("%s: Iterations[%d] in %d ms with Best[fitness=%f | sol=%s]\nFitnessHistory [%s]", name, this.iterations,
				this.execTime, this.best.fitness(), Arrays.toString(this.best.values()),
				Arrays.stream(this.avgFitness).boxed().map(v -> String.format("%.20f", v)).collect(joining(", ")));
	}

}