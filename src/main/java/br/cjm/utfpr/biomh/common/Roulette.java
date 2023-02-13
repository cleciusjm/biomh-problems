package br.cjm.utfpr.biomh.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparingDouble;

public class Roulette<T> {

	private final List<Part<T>> parts = new ArrayList<>();

	public Roulette(List<T> resultList, List<Double> chances) {
		for (int i = 0; i < resultList.size(); i++) {
			parts.add(new Part<>(resultList.get(i), chances.get(i)));
		}
		parts.sort(comparingDouble((Part<T> p) -> p.getChance()).reversed());
	}

	public T getResult() {
		double result = Math.random();
		double area = parts.get(0).getChance();
		for (int i = 0; i < parts.size() - 1; i++) {
			if (area > result)
				return parts.get(i).getItem();
			else
				area += parts.get(i + 1).getChance();
		}
		return parts.get(parts.size() - 1).getItem();
	}

	private static class Part<T> {
		private final T item;
		private final Double chance;

		public Part(T item, Double chance) {
			this.item = item;
			this.chance = chance;
		}

		public T getItem() {
			return item;
		}

		public Double getChance() {
			return chance;
		}
	}
}
