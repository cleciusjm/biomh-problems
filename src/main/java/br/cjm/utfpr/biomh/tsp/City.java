package br.cjm.utfpr.biomh.tsp;

import org.apache.commons.math3.ml.distance.EuclideanDistance;

public class City {

	private final double x;

	private final double y;

	public City(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double distanceTo(City city) {
		return Math.round(new EuclideanDistance().compute(new double[]{this.x, this.y}, new double[]{city.x, city.y}));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof City) {
			City o = (City) obj;
			return this.x == o.x && this.y == o.y;
		}
		return false;
	}

}
