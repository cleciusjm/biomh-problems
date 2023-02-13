package br.cjm.utfpr.biomh.functions.rastrigin;

import br.cjm.utfpr.biomh.functions.FunctionsFitnessFunction;

public class RastriginFitnessFunction implements FunctionsFitnessFunction {
	public double evaluate(double[] position) {
		double x = position[0];
		double y = position[1];
		double val = 20 + Math.pow(x, 2) + Math.pow(y, 2)
				- 10 * (Math.cos(2 * Math.PI * x) + Math.cos(2 * Math.PI * y));
		return val;
	}

}
