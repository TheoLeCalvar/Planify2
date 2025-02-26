package com.planify.server.solver;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import com.planify.server.models.Planning;
import com.planify.server.models.Result;

public class SolverExecutor {
	private static ExecutorService exec = Executors.newFixedThreadPool(3);
	private static final int MAX_ATTEMPTS = 3;
	private static int nbGene = 0;
	
	
    static final String RESET = "\u001B[0m";
    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";
	
    
	public static void generatePlanning(Planning planning) {
		generatePlanning(new Planning(planning), 0, nbGene, SolverMain::generatePlanning);
		nbGene ++;
	}
	
	public static void generatePlanningWithoutSync(Planning planning) {
		generatePlanning(new Planning(planning), 0, nbGene, SolverMain::generatePlanningWithoutSync);
		nbGene ++;
	}
	
	private static void generatePlanning(Planning planning, int nbAttempts, int nbGene, Function<Planning, Boolean> generate) {
		Runnable task = new Runnable() {
    		@Override
    		public void run() {
    			try {
    				planning.startProcessing();
    				System.out.println(GREEN + nbGene + " Launch background execution for " + planning.getCalendar().getTaf().getName() + " (planningId :" + planning.getId() + ", attempt : " + nbAttempts + ")" + RESET);
    				generate.apply(planning);
    				planning.endProcessing();
    			}
    			catch (Throwable e) {
    				System.out.println(RED + nbGene +  " Unhandled Error or Exception for " + planning.getCalendar().getTaf().getName() + " (planningId :" + planning.getId() + ", attempt : " + nbAttempts + ")" + RESET);
    				if (nbAttempts < MAX_ATTEMPTS)
    					generatePlanning(planning, nbAttempts + 1, nbGene, generate);
    				else {
    					System.out.println(RED + nbGene + " Generation Aborted." + RESET);
    					planning.endProcessing();
    				}
    			}
    		}
    	};
		exec.execute(task);
	}
	
	public static void generatePlannings(Planning[] planningsToGenerate) {
		generatePlannings(Planning.planningsToGenerate(planningsToGenerate), new Planning[] {});
	}
	
	public static void generatePlannings(Planning[] planningsToGenerate, Planning[] planningsGenerated) {
		generatePlannings(Planning.planningsToGenerate(planningsToGenerate), planningsGenerated, 0, nbGene);
		nbGene ++;
	}
	
	private static void generatePlannings(Planning[] planningsToGenerate, Planning[] planningsGenerated, int nbAttempts, int nbGene) {
		Runnable task = new Runnable() {
    		@Override
    		public void run() {
    			try {
    				for (Planning planning : planningsToGenerate) planning.startProcessing();
    				System.out.println(GREEN + nbGene + " Launch background execution for [" +
    										Arrays.stream(planningsToGenerate).map(p -> p.getCalendar().getTaf().getName() + "(" + p.getId() + ")").reduce("", String::concat) +
    										"], [" +
    										Arrays.stream(planningsGenerated).map(p -> p.getCalendar().getTaf().getName() + "(" + p.getId() + ")").reduce("", String::concat) +
    										"] (attempt : " + nbAttempts + ")" + RESET);
    				SolverMain.generatePlannings(planningsToGenerate, planningsGenerated);
    				for (Planning planning : planningsToGenerate) planning.endProcessing();
    			}
    			catch (Throwable e) {
    				System.out.println(RED + nbGene + " Launch background execution for " + Arrays.stream(planningsToGenerate).map(p -> p.getCalendar().getTaf().getName() + "(" + p.getId() + ")").reduce("", String::concat) + " (attempt : " + nbAttempts + ")" + RESET);
    				if (nbAttempts < MAX_ATTEMPTS)
    					generatePlannings(planningsToGenerate, planningsGenerated, nbAttempts + 1, nbGene);
    				else {
    					System.out.println(RED + nbGene + " Generation Aborted." + RESET);
        				for (Planning planning : planningsToGenerate) planning.endProcessing();
    				}
    			}
    		}
    	};
		exec.execute(task);
	}
}
