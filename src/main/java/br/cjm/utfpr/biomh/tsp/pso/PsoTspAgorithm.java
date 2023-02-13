package br.cjm.utfpr.biomh.tsp.pso;

import br.cjm.utfpr.biomh.tsp.City;
import br.cjm.utfpr.biomh.tsp.TspSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class PsoTspAgorithm implements Callable<TspSolution> {
	private final static double GLOBAL_INFLUENCE = 0.5;
	private final static double INERTIA = 0.1;

	private int iterations;
	private int populationSize;
	private int speedLimit;
	private List<City> cities;

	public PsoTspAgorithm(List<City> cities, int iterations, int population) {
		this.cities = cities;
		this.iterations = iterations;
		this.populationSize = population;
		this.speedLimit = cities.size() / 3;
	}

	@Override
	public TspSolution call() {
		long start = System.currentTimeMillis();
		Random rand = new Random();
		Particle best = Particle.create(cities);
		Particle[] population = IntStream.range(0, populationSize).mapToObj(i -> Particle.create(cities)).collect(toList())
				.toArray(new Particle[populationSize]);
		int execIterations = 0;
		Particle[] pbest = Arrays.copyOf(population, populationSize);
		double[][] speed = new double[population.length][cities.size()];
		double[] avgFitnessHistory = new double[iterations];

		for (int i = 0; i < iterations; i++) {
			execIterations++;

			for (int j = 0; j < population.length; j++) {
				Particle a = population[j];
				if (a.fitness() > best.fitness()) {
					best = a;
				}
				if (a.fitness() > pbest[j].fitness()) {
					pbest[j] = a;
				}
				int[] movement = new int[cities.size()];
				for (int x = 0; x < movement.length; x++) {
					speed[j][x] = INERTIA * speed[j][x] + (1 - GLOBAL_INFLUENCE) * rand.nextDouble() * (pbest[j].val(x) - a.val(x))
							+ GLOBAL_INFLUENCE * rand.nextDouble() * (best.val(x) - a.val(x));
					if (speed[j][x] > speedLimit) {
						speed[j][x] = speedLimit;
					} else if (speed[j][x] < -speedLimit) {
						speed[j][x] = -speedLimit;
					}
					movement[x] = (int) (a.val(x) + speed[j][x]);
				}
				population[j] = a.move(movement, cities);
			}

			avgFitnessHistory[i] = Arrays.stream(population).mapToDouble(Particle::fitness).average().orElse(0.0);

			if (isConverged(population, avgFitnessHistory[i]))
				break;

		}
		return new TspSolution(this.getClass(), best.steps, best.distance, avgFitnessHistory, execIterations, start,
				System.currentTimeMillis());
	}

	private boolean isConverged(Particle[] population, double avg) {
		for (Particle c : population) {
			if (c.fitness() != avg) {
				return false;
			}
		}
		return true;
	}

	static class Particle {

		private final int[] steps;

		private final double distance;

		private final double fitness;

		public Particle(int[] steps, List<City> cities) {
			this.steps = steps;
			double tmp = 0.0;
			for (int i = 0; i < steps.length; i++) {
				int next = i == steps.length - 1 ? steps[0] : steps[i + 1];
				tmp += cities.get(steps[i]).distanceTo(cities.get(next));
			}
			this.distance = tmp;
			this.fitness = 1.0 / this.distance;
		}

		public Particle move(int[] movement, List<City> cities) {
			if (movement.length != this.steps.length) {
				throw new IllegalArgumentException();
			}
			boolean[] changed = new boolean[movement.length];
			int[] result = Arrays.copyOf(steps, steps.length);
			for (int i = 0; i < movement.length; i++) {
				int tmp = result[i];
				result[i] = movement[i] + this.steps[i];
				changed[i] = true;
				if (result[i] > cities.size()) {
					result[i] = result[i] % cities.size();
				} else if (result[i] < 0) {
					result[i] = tmp;
					changed[i] = false;
				}
				if (result[i] != tmp) {
					for (int j = 0; j < result.length; j++) {
						if (result[i] == result[j]) {
							result[j] = tmp;
							changed[j] = true;
							break;
						}
					}
				}
			}
			return new Particle(result, cities);
		}

		public double val(int x) {
			return steps[x];
		}

		public double fitness() {
			return this.fitness;
		}

		static Particle create(List<City> cities) {
			int[] steps = new int[cities.size()];
			List<City> pending = new ArrayList<>(cities);
			pending.remove(0);
			Random rand = new Random();
			for (int i = 1; i < cities.size(); i++) {
				City result = pending.get((int) (rand.nextDouble() * pending.size()));
				steps[i] = cities.indexOf(result);
				pending.remove(result);
			}
			return new Particle(steps, cities);
		}

		@Override
		public String toString() {
			return new StringBuilder("Ant[f=").append(this.fitness()).append("|").append(this.steps).append("]").toString();
		}
	}

}
