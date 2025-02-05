package com.planify.server.solver;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.planify.server.models.Planning;

public class SolverExecutor {
	private static ExecutorService exec = Executors.newFixedThreadPool(3);
	private static final int MAX_ATTEMPTS = 3;
	private static int nbGene = 0;
	
	
    static final String RESET = "\u001B[0m";
    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";
	
	public static void generatePlanning(Planning planning) {
		generatePlanning(planning, 0, nbGene);
		nbGene ++;
	}
	
	private static void generatePlanning(Planning planning, int nbAttempts, int nbGene) {
		Runnable task = new Runnable() {
    		@Override
    		public void run() {
    			try {
    				System.out.println(GREEN + nbGene + " Launch background execution for " + planning.getCalendar().getTaf().getName() + " (planningId :" + planning.getId() + ", attempt : " + nbAttempts + ")" + RESET);
    				SolverMain.generatePlanning(planning);
    			}
    			catch (Throwable e) {
    				System.out.println(RED + nbGene +  " Unhandled Error or Exception for " + planning.getCalendar().getTaf().getName() + " (planningId :" + planning.getId() + ", attempt : " + nbAttempts + ")" + RESET);
    				if (nbAttempts < MAX_ATTEMPTS)
    					generatePlanning(planning, nbAttempts + 1, nbGene);
    				else
    					System.out.println(RED + nbGene + " Generation Aborted." + RESET);
    			}
    		}
    	};
		exec.execute(task);
	}
	
	public static void generatePlannings(Planning[] planningsToGenerate) {
		generatePlannings(planningsToGenerate, new Planning[] {});
	}
	
	public static void generatePlannings(Planning[] planningsToGenerate, Planning[] planningsGenerated) {
		generatePlannings(planningsToGenerate, planningsGenerated, 0, nbGene);
		nbGene ++;
	}
	
	private static void generatePlannings(Planning[] planningsToGenerate, Planning[] planningsGenerated, int nbAttempts, int nbGene) {
		Runnable task = new Runnable() {
    		@Override
    		public void run() {
    			try {
    				System.out.println(GREEN + nbGene + " Launch background execution for [" +
    										Arrays.stream(planningsToGenerate).map(p -> p.getCalendar().getTaf().getName() + "(" + p.getId() + ")").reduce("", String::concat) +
    										"], [" +
    										Arrays.stream(planningsGenerated).map(p -> p.getCalendar().getTaf().getName() + "(" + p.getId() + ")").reduce("", String::concat) +
    										"] (attempt : " + nbAttempts + ")" + RESET);
    				SolverMain.generatePlannings(planningsToGenerate, planningsGenerated);
    			}
    			catch (Throwable e) {
    				System.out.println(RED + nbGene + " Launch background execution for " + Arrays.stream(planningsToGenerate).map(p -> p.getCalendar().getTaf().getName() + "(" + p.getId() + ")").reduce("", String::concat) + " (attempt : " + nbAttempts + ")" + RESET);
    				if (nbAttempts < MAX_ATTEMPTS)
    					generatePlannings(planningsToGenerate, planningsGenerated, nbAttempts + 1, nbGene);
    				else
    					System.out.println(RED + nbGene + " Generation Aborted." + RESET);
    			}
    		}
    	};
		exec.execute(task);
	}
}
