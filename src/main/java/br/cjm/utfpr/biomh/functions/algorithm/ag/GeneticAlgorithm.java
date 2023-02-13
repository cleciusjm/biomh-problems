package br.cjm.utfpr.biomh.functions.algorithm.ag;

import br.cjm.utfpr.biomh.functions.FunctionsFitnessFunction;
import br.cjm.utfpr.biomh.functions.FunctionsSolution;
import br.cjm.utfpr.biomh.functions.algorithm.Agent;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class GeneticAlgorithm implements Callable<FunctionsSolution> {
	private static final double INAPT_SURVIVE_RATE = 0.1;
	private static final double MUTATION_RATE = 0.5;

	private int iterations;
	private int populationSize;
	private FunctionsFitnessFunction fitnessFunction;
	private int chromosomeSize;
	private double upperBound;
	private double lowerBound;

	public GeneticAlgorithm(FunctionsFitnessFunction fitnessFunction, int iterations, int population, int chromosomeSize, double upperBound,
							double lowerBound) {
		this.fitnessFunction = fitnessFunction;
		this.iterations = iterations;
		this.populationSize = population;
		this.chromosomeSize = chromosomeSize;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}

	@Override
	public FunctionsSolution call() {
		long start = System.currentTimeMillis();
		Agent best = randomChromosome();
		List<Agent> population = IntStream.range(0, populationSize).mapToObj(i -> randomChromosome()).collect(toList());
		double[] avgFitnessHistory = new double[iterations];
		int execIterations = 0;
		for (int i = 0; i < iterations; i++) {
			execIterations++;
			Optional<Agent> lbest = population.stream().sorted(Comparator.comparingDouble(Agent::fitness).reversed()).findFirst();
			if (lbest.isPresent() && best.fitness() < lbest.get().fitness()) {
				best = lbest.get();
			}
			DoubleSummaryStatistics popStats = population.stream().mapToDouble(Agent::fitness).summaryStatistics();
			double avg = avgFitnessHistory[i] = popStats.getAverage();

			if (isConverged(population, avg))
				break;

			population.removeIf(c -> c.fitness() < avg && !surviveChance(c));
			if (population.isEmpty())
				break;

			for (int j = 0; j < (populationSize - population.size()); j++) {
				population.add(crossOver(randomChoose(population), randomChoose(population)));
			}
			for (int j = 0; j < (MUTATION_RATE * population.size()); j++) {
				Agent toMutate = randomChoose(population);
				population.removeIf(toMutate::equals);
				population.add(mutate(toMutate));
			}
		}
		return new FunctionsSolution(this.getClass(), best, avgFitnessHistory, execIterations, start, System.currentTimeMillis());
	}

	private boolean isConverged(List<Agent> population, double avg) {
		for (Agent a : population) {
			if (a.fitness() != avg) {
				return false;
			}
		}
		return true;
	}

	private Agent mutate(Agent c) {
		Random rand = new Random();
		int mutationNumber = rand.nextInt(c.size());
		double[] mutatedVal = Arrays.copyOf(c.values(), c.size());
		for (int i = 0; i < mutationNumber; i++) {
			int mutationIndex = (int) (rand.nextDouble() * c.size());
			mutatedVal[mutationIndex] = (rand.nextDouble() * (upperBound - lowerBound)) + lowerBound;
		}
		return new Agent(mutatedVal, fitnessFunction);
	}

	private Agent crossOver(Agent c1, Agent c2) {
		Random rand = new Random();
		double[] child = new double[chromosomeSize];
		for (int i = 0; i < child.length; i++) {
			child[i] = rand.nextBoolean() ? c1.val(i) : c2.val(i);
		}
		return new Agent(child, fitnessFunction);
	}

	private Agent randomChoose(List<Agent> population) {
		return population.get((int) Math.random() * population.size());
	}

	private boolean surviveChance(Agent c) {
		return Math.random() < INAPT_SURVIVE_RATE;
	}

	private Agent randomChromosome() {
		Random rand = new Random();
		double[] val = new double[chromosomeSize];
		for (int i = 0; i < val.length; i++) {
			val[i] = (rand.nextDouble() * (upperBound - lowerBound)) + lowerBound;
		}
		return new Agent(val, fitnessFunction);
	}

}
