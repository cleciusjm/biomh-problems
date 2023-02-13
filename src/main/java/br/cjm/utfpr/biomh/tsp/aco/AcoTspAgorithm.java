package br.cjm.utfpr.biomh.tsp.aco;

import br.cjm.utfpr.biomh.common.Roulette;
import br.cjm.utfpr.biomh.tsp.City;
import br.cjm.utfpr.biomh.tsp.TspSolution;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class AcoTspAgorithm implements Callable<TspSolution> {

	private static final double EVAPORATION_TAX = 0.7;
	private static final double RANDOM_CHOOSE_TAX = 0.1;

	private final List<City> cities;
	private final int iterations;
	private final int populationSize;

	public AcoTspAgorithm(List<City> cities, int iterations, int populationSize) {
		this.cities = cities;
		this.iterations = iterations;
		this.populationSize = populationSize;
	}

	@Override
	public TspSolution call() {
		long start = System.currentTimeMillis();
		double[][] pheromone = new double[cities.size()][cities.size()];
		Ant best = Ant.create(cities, pheromone);
		for (int i = 0; i < pheromone.length; i++)
			for (int j = 0; j < pheromone[i].length; j++)
				pheromone[i][j] = best.fitness();
		evaporatePheromone(EVAPORATION_TAX, pheromone);
		updatePheremone(Collections.singletonList(best), pheromone);
		double[] avgFitnessHistory = new double[iterations];
		int execIterations = 0;
		for (int i = 0; i < iterations; i++) {
			execIterations++;
			List<Ant> ants = IntStream.range(0, populationSize).parallel().mapToObj(j -> Ant.create(cities, pheromone))
					.sorted(Comparator.comparing(Ant::fitness).reversed()).collect(toList());

			avgFitnessHistory[i] = ants.stream().mapToDouble(Ant::fitness).average().orElse(0.0);

			evaporatePheromone(EVAPORATION_TAX, pheromone);
			updatePheremone(ants, pheromone);
			Ant ant = ants.get(0);
			if (ant.fitness() > best.fitness()) {
				best = ant;
			}
			if (isConverged(ants, avgFitnessHistory[i]))
				break;

		}
		return new TspSolution(this.getClass(), best.steps, best.distance, avgFitnessHistory, execIterations, start,
				System.currentTimeMillis());

	}

	private boolean isConverged(List<Ant> ants, double avg) {
		for (Ant ant : ants) {
			if (ant.fitness() != avg) {
				return false;
			}
		}
		return true;
	}

	private void evaporatePheromone(double evaporationTax, double[][] pheromone) {
		if (evaporationTax > 1)
			throw new IllegalArgumentException();
		for (int i = 0; i < pheromone.length; i++)
			for (int j = 0; j < pheromone[i].length; j++)
				pheromone[i][j] *= (1 - evaporationTax);
	}

	private void updatePheremone(List<Ant> ants, double[][] pheromone) {
		for (Ant ant : ants) {
			for (int i = 1; i < ant.steps.length; i++) {
				int dest = ant.steps[i];
				int current = ant.steps[i - 1];
				pheromone[current][dest] += ant.fitness();
			}
		}
	}

	static class Ant {

		private final int[] steps;

		private final double distance;

		private final double fitness;

		public Ant(int[] steps, List<City> cities) {
			this.steps = steps;
			double tmp = 0.0;
			for (int i = 0; i < steps.length; i++) {
				int next = i == steps.length - 1 ? steps[0] : steps[i + 1];
				tmp += cities.get(steps[i]).distanceTo(cities.get(next));
			}
			this.distance = tmp;
			this.fitness = 1.0 / this.distance;
		}

		public double fitness() {
			return this.fitness;
		}

		static Ant create(List<City> cities, double[][] pheromones) {
			int[] steps = new int[cities.size()];
			List<City> pending = new ArrayList<>(cities);
			pending.remove(0);
			for (int i = 1; i < cities.size(); i++) {
				City result = selectCity(pheromones[i], pending);
				steps[i] = cities.indexOf(result);
				pending.remove(result);
			}
			return new Ant(steps, cities);
		}

		private static City selectCity(double[] pheromones, List<City> pending) {
			double sum = Arrays.stream(pheromones).sum();
			if (sum <= 0 || Math.random() < RANDOM_CHOOSE_TAX) {
				return pending.get((int) (Math.random() * pending.size()));
			} else {
				Roulette<City> roulette = new Roulette<>(pending,
						Arrays.stream(pheromones).map(v -> v / sum).boxed().collect(toList()));
				return roulette.getResult();
			}
		}

		@Override
		public String toString() {
			return "Ant[f=" + fitness() + "|" + Arrays.toString(this.steps) + "]";
		}
	}
}
