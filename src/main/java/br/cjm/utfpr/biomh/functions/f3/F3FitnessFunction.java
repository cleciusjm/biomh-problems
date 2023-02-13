package br.cjm.utfpr.biomh.functions.f3;

import br.cjm.utfpr.biomh.functions.FunctionsFitnessFunction;

public class F3FitnessFunction implements FunctionsFitnessFunction {

	@Override
	public double evaluate(double[] position) {
		double x = position[0];
		double y = position[1];
		return Math.pow(x, -(Math.pow(x, 2) + Math.pow(y, 2)));
	}

}
