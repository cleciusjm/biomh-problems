package br.cjm.utfpr.biomh.tsp.greedy;

import br.cjm.utfpr.biomh.tsp.City;
import br.cjm.utfpr.biomh.tsp.TspSolution;

import java.util.List;
import java.util.concurrent.Callable;

public class GreedyTspAgorithm implements Callable<TspSolution> {

	private List<City> cities;

	public GreedyTspAgorithm(List<City> cities) {
		this.cities = cities;
	}

	@Override
	public TspSolution call() {
		long start = System.currentTimeMillis();
		boolean[] selected = new boolean[cities.size()];
		int[] solution = new int[cities.size()];
		double solutionDistance = 0.0;
		solution[0] = 0;
		for (int i = 1; i < solution.length; i++) {
			int next = 0;
			double nextDistance = 0.0;
			for (int j = 0; j < solution.length; j++) {
				if (next != j && !selected[j]) {
					City current = cities.get(solution[i - 1]);
					City nextCity = cities.get(j);
					double distance = current.distanceTo(nextCity);
					if (nextDistance == 0.0 || distance < nextDistance) {
						next = j;
						nextDistance = distance;
					}
				}
			}
			solution[i] = next;
			solutionDistance += nextDistance;
			selected[next] = true;
		}
		solutionDistance += cities.get(solution[solution.length - 1]).distanceTo(cities.get(solution[0]));
		return new TspSolution(this.getClass(), solution, solutionDistance, new double[]{1.0 / solutionDistance}, 1, start,
				System.currentTimeMillis());
	}

}
