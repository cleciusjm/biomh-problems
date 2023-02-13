package br.cjm.utfpr.biomh.functions.f3;

import br.cjm.utfpr.biomh.functions.algorithm.ag.GeneticAlgorithm;
import br.cjm.utfpr.biomh.functions.algorithm.clonalg.ClonalgAlgorithm;
import br.cjm.utfpr.biomh.functions.algorithm.pso.PSOAlgorithm;
import br.cjm.utfpr.biomh.functions.FunctionsFinalSolution;
import br.cjm.utfpr.biomh.functions.FunctionsSolution;
import br.cjm.utfpr.biomh.functions.algorithm.optainet.OptAiNetAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class F3Runner implements Callable<List<FunctionsFinalSolution>> {
	private static final int AGENTS = 100;
	private static final int ITERATIONS = 300;
	private static final double UPPER_BOUND = 2.0;
	private static final double LOWER_BOUND = -2.0;
	private static final int ENTITY_LENGTH = 2;

	public List<FunctionsFinalSolution> call() {
		List<FunctionsFinalSolution> results = new ArrayList<>();

		results.add(run(new GeneticAlgorithm(new F3FitnessFunction(), ITERATIONS, AGENTS, ENTITY_LENGTH, UPPER_BOUND, LOWER_BOUND)));
		results.add(run(new ClonalgAlgorithm(new F3FitnessFunction(), ITERATIONS, AGENTS, ENTITY_LENGTH, UPPER_BOUND, LOWER_BOUND)));
		results.add(run(new OptAiNetAlgorithm(new F3FitnessFunction(), ITERATIONS, AGENTS, ENTITY_LENGTH, UPPER_BOUND, LOWER_BOUND)));
		results.add(run(new PSOAlgorithm(new F3FitnessFunction(), ITERATIONS, AGENTS, ENTITY_LENGTH, UPPER_BOUND, LOWER_BOUND)));
		return results;
	}

	private FunctionsFinalSolution run(Callable<FunctionsSolution> algorithm) {
		try {
			List<FunctionsSolution> solutions = new ArrayList<>();
			for (int i = 0; i < 30; i++) {
				solutions.add(algorithm.call());
			}
			return new FunctionsFinalSolution(solutions, ITERATIONS);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
