package com.planify.server.solver;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;

import com.planify.server.models.Calendar;
import com.planify.server.models.TAF;

public class SolverMain {
	public static void generateTaf(TAF taf, Calendar cal) {
		Model model = new Model();
		Solver solver = model.getSolver();
		taf.initialiseVars(model);
		setConstraints(model, taf);
		solver.setSearch(taf.getStrategy());
		solver.findOptimalSolution(obj, false, null);
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		
	}
}
