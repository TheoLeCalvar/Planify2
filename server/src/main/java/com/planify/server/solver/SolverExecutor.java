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
	private static SolverServices services;
	
    static final String RESET = "\u001B[0m";
    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";
	
    public static void setServices(SolverServices services) {
		SolverExecutor.services = services;
	}
    
    private static Planning createPlanningForGeneration(Planning planning) {
		System.out.println("SolverExecutor2" + planning.toString());
    	return services.getPlanningService().createPlanningForGeneration(planning);
    }
    
    private static Planning[] createPlanningsForGeneration(Planning[] plannings) {
    	return services.getPlanningService().createPlanningsForGeneration(plannings);
    }
    
	public static void generatePlanning(Planning planning) {
		System.out.println("SolverExecutor" + planning.toString());
		generatePlanning(createPlanningForGeneration(planning), nbGene, SolverMain::generatePlanning);
		nbGene ++;
	}
	
	public static void generatePlanningWithoutSync(Planning planning) {
		generatePlanning(createPlanningForGeneration(planning), nbGene, SolverMain::generatePlanningWithoutSync);
		nbGene ++;
	}
	
	private static void generatePlanning(Planning planning, int nbGene, Function<Planning, Boolean> generate) {
		Runnable task = new Runnable() {
    		@Override
    		public void run() {
    			for (int iAttempt = 1; iAttempt <= MAX_ATTEMPTS; iAttempt ++)
	    			try {
	    				planning.startProcessing();
	    				services.getPlanningService().save(planning);
	    				System.out.println(GREEN + nbGene + " Launch background execution for " + planning.getCalendar().getTaf().getName() + " (planningId :" + planning.getId() + ", attempt : " + iAttempt + ")" + RESET);
	    				generate.apply(planning);
	    				planning.endProcessing();
	    				services.getPlanningService().save(planning);
	    				return;
	    			}
	    			catch (Throwable e) {
	    				System.out.println(RED + nbGene +  " Unhandled Error or Exception for " + planning.getCalendar().getTaf().getName() + " (planningId :" + planning.getId() + ", attempt : " + iAttempt + ")" + RESET);
	    				e.printStackTrace();
	    			}
    			System.out.println(RED + nbGene + " Generation Aborted." + RESET);
				planning.endProcessing();
				services.getPlanningService().save(planning);
    		}
    	};
		exec.execute(task);
	}
	
	public static void generatePlannings(Planning[] planningsToGenerate) {
		generatePlannings(createPlanningsForGeneration(planningsToGenerate), new Planning[] {});
	}
	
	public static void generatePlannings(Planning[] planningsToGenerate, Planning[] planningsGenerated) {
		generatePlannings(createPlanningsForGeneration(planningsToGenerate), planningsGenerated, nbGene);
		nbGene ++;
	}
	
	private static void generatePlannings(Planning[] planningsToGenerate, Planning[] planningsGenerated, int nbGene) {
		Runnable task = new Runnable() {
    		@Override
    		public void run() {
    			for (int iAttempt = 1; iAttempt <= MAX_ATTEMPTS; iAttempt ++)
	    			try {
	    				for (Planning planning : planningsToGenerate) {planning.startProcessing(); services.getPlanningService().save(planning);}
	    				System.out.println(GREEN + nbGene + " Launch background execution for [" +
	    										Arrays.stream(planningsToGenerate).map(p -> p.getCalendar().getTaf().getName() + "(" + p.getId() + ")").reduce("", String::concat) +
	    										"], [" +
	    										Arrays.stream(planningsGenerated).map(p -> p.getCalendar().getTaf().getName() + "(" + p.getId() + ")").reduce("", String::concat) +
	    										"] (attempt : " + iAttempt + ")" + RESET);
	    				SolverMain.generatePlannings(planningsToGenerate, planningsGenerated);
	    				for (Planning planning : planningsToGenerate) {planning.endProcessing(); services.getPlanningService().save(planning);}
	    				return;
	    			}
	    			catch (Throwable e) {
	    				System.out.println(RED + nbGene + " Launch background execution for " + Arrays.stream(planningsToGenerate).map(p -> p.getCalendar().getTaf().getName() + "(" + p.getId() + ")").reduce("", String::concat) + " (attempt : " + iAttempt + ")" + RESET);
	    				e.printStackTrace();
	    			}
    			
    			System.out.println(RED + nbGene + " Generation Aborted." + RESET);
				for (Planning planning : planningsToGenerate) planning.endProcessing();
				return;
    		}
    	};
		exec.execute(task);
	}
}
