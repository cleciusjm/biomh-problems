package br.cjm.utfpr.biomh.tsp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CitiesLoader {
	private static final String DJI_BOUTI = "/br/cjm/utfpr/biomh/tsp/dji-bouti.tsp";

	public static List<City> loadCitiesDjiBouti() throws IOException {
		return initCities(DJI_BOUTI);
	}

	private static List<City> initCities(String fileName) throws IOException {
		List<City> results = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(CitiesLoader.class.getResourceAsStream(fileName)))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(" ");

				if (tokens.length == 3) {
					results.add(new City(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2])));
				}
			}
		}

		return results;
	}
}
