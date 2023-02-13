package br.cjm.utfpr.biomh.tsp;

import br.cjm.utfpr.biomh.tsp.aco.AcoTspAgorithm;
import br.cjm.utfpr.biomh.tsp.greedy.GreedyTspAgorithm;
import br.cjm.utfpr.biomh.tsp.pso.PsoTspAgorithm;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class TspMain {

	private static final int ITERATIONS = 300;

	public static void main(String[] args) throws IOException {
		List<City> cities = Collections.unmodifiableList(CitiesLoader.loadCitiesDjiBouti());
		GreedyTspAgorithm greedyTspAlgorithm = new GreedyTspAgorithm(cities);
		AcoTspAgorithm acoTspAgorithm = new AcoTspAgorithm(cities, ITERATIONS, cities.size());
		PsoTspAgorithm psoTspAgorithm = new PsoTspAgorithm(cities, ITERATIONS, cities.size() * 10);
		FinalSolution greedySolution = new FinalSolution(Collections.singletonList(greedyTspAlgorithm.call()));
		FinalSolution acoSolution = new FinalSolution(range(0, 30).mapToObj(i -> acoTspAgorithm.call()).collect(toList()));
		FinalSolution psoSolution = new FinalSolution(range(0, 30).mapToObj(i -> psoTspAgorithm.call()).collect(toList()));
		System.out.println("GREEDY:" + greedySolution);
		System.out.println("ACO:" + acoSolution);
		System.out.println("PSO:" + psoSolution);
	}

	static class FinalSolution {
		double[] avgFitness = new double[ITERATIONS];
		double bestFitness = 0.0;
		double bestDistance = 0.0;
		int[] bestSolution;
		private long execTime;
		private int iterations;

		public FinalSolution(List<TspSolution> solutions) {
			if (solutions.isEmpty())
				throw new IllegalArgumentException();
			for (TspSolution sol : solutions) {
				if (bestFitness < sol.getBestFitness()) {
					this.bestDistance = sol.getBestDistance();
					this.bestSolution = sol.getBest();
					this.bestFitness = sol.getBestFitness();
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
			return String.format("Iterations[%d] in %d ms with Best[fitness=%f | distance=%.1f | sol=%s]\nFitnessHistory [%s]",
					this.iterations, this.execTime, this.bestFitness, this.bestDistance, Arrays.toString(this.bestSolution),
					Arrays.stream(this.avgFitness).boxed().map(v -> String.format("%.20f", v)).collect(joining(", ")));
		}

	}
}
