package br.cjm.utfpr.biomh.functions;

import br.cjm.utfpr.biomh.functions.f3.F3Runner;
import br.cjm.utfpr.biomh.functions.schwefel.SchewefelRunner;
import br.cjm.utfpr.biomh.functions.rastrigin.RastringinRunner;

public class FunctionsMain {

	public static void main(String[] args) {
		System.out.println("Problema 1 (Schewefel): ");
		new SchewefelRunner().call().forEach(System.out::println);
		System.out.println("Problema 2 (Rastringin): ");
		new RastringinRunner().call().forEach(System.out::println);
		System.out.println("Problema 3: ");
		new F3Runner().call().forEach(System.out::println);
	}

}
