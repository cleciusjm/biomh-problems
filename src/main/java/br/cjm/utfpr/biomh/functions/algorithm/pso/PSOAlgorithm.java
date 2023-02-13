package br.cjm.utfpr.biomh.functions.algorithm.pso;

import br.cjm.utfpr.biomh.functions.FunctionsFitnessFunction;
import br.cjm.utfpr.biomh.functions.FunctionsSolution;
import br.cjm.utfpr.biomh.functions.algorithm.Agent;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class PSOAlgorithm implements Callable<FunctionsSolution> {
	private final static double GLOBAL_INFLUENCE = 0.5;
	private final static double INERTIA = 0.1;

	private int iterations;
	private int populationSize;
	private FunctionsFitnessFunction fitnessFunction;
	private int particleSize;
	private double upperBound;
	private double lowerBound;
	private double speedLimit;

	public PSOAlgorithm(FunctionsFitnessFunction fitnessFunction, int iterations, int population, int particleSize, double upperBound,
						double lowerBound) {
		this.fitnessFunction = fitnessFunction;
		this.iterations = iterations;
		this.populationSize = population;
		this.particleSize = particleSize;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
		this.speedLimit = ((upperBound - lowerBound)) / 100.0;
	}

	@Override
	public FunctionsSolution call() {
		long start = System.currentTimeMillis();
		Random rand = new Random();
		Agent best = randomChromosome();
		Agent[] population = IntStream.range(0, populationSize).mapToObj(i -> randomChromosome()).collect(toList())
				.toArray(new Agent[populationSize]);
		int execIterations = 0;
		Agent[] pbest = Arrays.copyOf(population, populationSize);
		double[][] speed = new double[population.length][particleSize];
		double[] avgFitnessHistory = new double[iterations];

		for (int i = 0; i < iterations; i++) {
			execIterations++;

			for (int j = 0; j < population.length; j++) {
				Agent a = population[j];
				if (a.fitness() > best.fitness()) {
					best = a;
				}
				if (a.fitness() > pbest[j].fitness()) {
					pbest[j] = a;
				}
				double[] movement = new double[particleSize];
				for (int x = 0; x < movement.length; x++) {
					speed[j][x] = INERTIA * speed[j][x] + (1 - GLOBAL_INFLUENCE) * rand.nextDouble() * (pbest[j].val(x) - a.val(x))
							+ GLOBAL_INFLUENCE * rand.nextDouble() * (best.val(x) - a.val(x));
					if (speed[j][x] > speedLimit) {
						speed[j][x] = speedLimit;
					} else if (speed[j][x] < -speedLimit) {
						speed[j][x] = -speedLimit;
					}
					movement[x] = a.val(x) + speed[j][x];
				}
				population[j] = a.move(movement, upperBound, lowerBound);
			}

			avgFitnessHistory[i] = Arrays.stream(population).mapToDouble(Agent::fitness).average().orElse(0.0);
			if (isConverged(population, avgFitnessHistory[i]))
				break;

		}
		return new FunctionsSolution(this.getClass(), best, avgFitnessHistory, execIterations, start, System.currentTimeMillis());
	}

	private boolean isConverged(Agent[] population, double avg) {
		for (Agent c : population) {
			if (c.fitness() != avg) {
				return false;
			}
		}
		return true;
	}

	private Agent randomChromosome() {
		Random rand = new Random();
		double[] val = new double[particleSize];
		for (int i = 0; i < val.length; i++) {
			val[i] = (rand.nextDouble() * (upperBound - lowerBound)) + lowerBound;
		}
		return new Agent(val, fitnessFunction);
	}

}
