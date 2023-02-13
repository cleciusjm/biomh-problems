package br.cjm.utfpr.biomh.functions.algorithm;

import br.cjm.utfpr.biomh.functions.FunctionsFitnessFunction;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.util.Arrays;

public class Agent {

	private final double[] values;
	private final double fitness;

	private final FunctionsFitnessFunction fitnessFunction;

	public Agent(double[] values, FunctionsFitnessFunction fitnessFunction) {
		this.values = values;
		this.fitnessFunction = fitnessFunction;
		this.fitness = fitnessFunction.evaluate(values);

	}

	public double[] values() {
		return values;
	}

	public double val(int i) {
		return values[i];
	}

	public double fitness() {
		return fitness;
	}

	public FunctionsFitnessFunction getFitnessFunction() {
		return fitnessFunction;
	}

	public int size() {
		return this.values.length;
	}

	public Agent move(double[] movement, double upperBound, double lowerBound) {
		if (movement.length != this.values.length) {
			throw new IllegalArgumentException();
		}
		double[] result = new double[this.values.length];
		for (int i = 0; i < movement.length; i++) {
			result[i] = movement[i] + this.values[i];
			if (result[i] > upperBound) {
				result[i] = upperBound;
			} else if (result[i] < lowerBound) {
				result[i] = lowerBound;
			}
		}
		return new Agent(result, fitnessFunction);
	}

	public double distanceTo(Agent a) {
		return Math.round(new EuclideanDistance().compute(this.values, a.values));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Agent) {
			Agent o = (Agent) obj;
			return Arrays.equals(this.values, o.values);
		}
		return false;
	}

}
