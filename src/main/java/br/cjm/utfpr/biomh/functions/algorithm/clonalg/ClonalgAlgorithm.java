package br.cjm.utfpr.biomh.functions.algorithm.clonalg;

import br.cjm.utfpr.biomh.common.Roulette;
import br.cjm.utfpr.biomh.functions.FunctionsFitnessFunction;
import br.cjm.utfpr.biomh.functions.FunctionsSolution;
import br.cjm.utfpr.biomh.functions.algorithm.Agent;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class ClonalgAlgorithm implements Callable<FunctionsSolution> {
	private static final double INAPT_SURVIVE_RATE = 0.1;

	private int iterations;
	private int populationSize;
	private FunctionsFitnessFunction fitnessFunction;
	private int chromosomeSize;
	private double upperBound;
	private double lowerBound;

	public ClonalgAlgorithm(FunctionsFitnessFunction fitnessFunction, int iterations, int population, int chromosomeSize, double upperBound,
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
		int execIterations = 0;
		double[] avgFitnessHistory = new double[iterations];
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

			List<Double> proportionalFitness = population.stream().mapToDouble(Agent::fitness).map(v -> v / popStats.getSum()).boxed()
					.collect(toList());

			Roulette<Agent> roulette = new Roulette<>(population, proportionalFitness);

			List<Agent> clones = new ArrayList<>(populationSize);
			for (int j = 0; j < populationSize; j++) {
				clones.add(roulette.getResult());
			}

			for (int j = 0; j < clones.size(); j++) {
				Agent toMutate = randomChoose(clones);
				clones.removeIf(toMutate::equals);
				population.add(mutate(toMutate));
			}

			population.addAll(clones);
			population.removeIf(c -> c.fitness() < avg && !surviveChance(c));
			if (population.isEmpty())
				break;
		}
		return new FunctionsSolution(this.getClass(), best, avgFitnessHistory, execIterations, start, System.currentTimeMillis());
	}

	private boolean isConverged(List<Agent> population, double avg) {
		for (Agent c : population) {
			if (c.fitness() != avg) {
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
