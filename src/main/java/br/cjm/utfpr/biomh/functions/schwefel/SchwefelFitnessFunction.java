package br.cjm.utfpr.biomh.functions.schwefel;

import br.cjm.utfpr.biomh.functions.FunctionsFitnessFunction;

public class SchwefelFitnessFunction implements FunctionsFitnessFunction {
	private static final double D = 2.0;

	public double evaluate(double[] position) {
		double x = position[0];
		double y = position[1];
		double val = (D * 418.9829) - somat(1, D, x, y);
		return val;
	}

	private double somat(int init, double end, double x, double y) {
		double acc = 0.0;
		for (int i = init; i < end; i++) {
			acc += x * Math.sin(Math.sqrt(Math.abs(x)));
		}
		return acc;
	}

}
