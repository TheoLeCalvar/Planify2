package com.planify.server.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import com.planify.server.models.Planning;
import com.planify.server.models.Result;
import com.planify.server.models.constraints.ConstraintSynchroniseWithTAF;

/**
 * Class to launch the resolution of the model. It can run up to 3 resolutions at the same time
 * in parallel thread. If more than 3 resolutions are asked to be launched, the three first one
 * are launched and the others wait an available thread.
 * 
 * @author Nathan RABIER
 *
 */
public class SolverExecutor {
	private static ExecutorService exec = Executors.newFixedThreadPool(3); //The executor for the three threads.
	private static final int MAX_ATTEMPTS = 3; //The max number of time to launch again the solving if an error occurs.
	private static int nbGene = 0; //The total number of generation launched.
	private static SolverServices services;
	
    static final String RESET = "\u001B[0m";
    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";
	
    /**
     * Set the SolverServices object to have access to the different services.
     * @param services The SolverServices object.
     */
    public static void setServices(SolverServices services) {
		SolverExecutor.services = services;
	}
    
    /**
     * Create a copy of the Planning planning with status = WAITING_TO_BE_PROCESSED and return it to be used in the generation
     * @param planning The planning to copy.
     * @return The copied planning.
     */
    private static Planning createPlanningForGeneration(Planning planning) {
		return services.getPlanningService().createPlanningForGeneration(planning);
    }
    
    /**
     * Create a copy of the Plannings plannings with status = WAITING_TO_BE_PROCESSED and return them to be used in the generation
     * /!\ cSyncs in new plannings are not updated !
     * @param plannings The plannings to copy.
     * @return The copied plannings.
     */
    private static Planning[] createPlanningsForGeneration(Planning[] plannings) {
    	return services.getPlanningService().createPlanningsForGeneration(plannings);
    }
    
    /**
	 * Generate the planning.
	 * @param plan The planning to generate.
	 * @return The results generated (also stored automatically in the database).
	 */
	public static void generatePlanning(Planning planning){
		//If no synchronization, then generate without synchronization.
		if (!planning.isSynchronise() || planning.getConstraintsSynchronisation().isEmpty()) {
			System.out.println("Task Generate Planning " + planning.getId());
			generatePlanning(createPlanningForGeneration(planning), nbGene);
			nbGene ++;
			return;
		}
		// Simple BFS to go through the graph of synchronizations.
		List<Planning> planningsToConsider = new ArrayList<Planning>();
		planningsToConsider.add(planning);
		List<Planning> planningsToGenerateLst = new ArrayList<Planning>();
		planningsToGenerateLst.add(planning);
		List<Planning> planningsGenerated = new ArrayList<Planning>();
		while (!planningsToConsider.isEmpty()) {
			Planning plan = planningsToConsider.removeFirst();
			for (ConstraintSynchroniseWithTAF cSync : plan.getConstraintsSynchronisation()) {
				// Avoid having two plannings of the same TAF.
				if (!(cSyncInPlannings(cSync, planningsToGenerateLst) || cSyncInPlannings(cSync, planningsGenerated))) {
					Planning otherPlanning = cSync.getOtherPlanning();
					if (otherPlanning.isGenerated()) {
						planningsGenerated.add(otherPlanning);
					}
					else {
						planningsToConsider.add(otherPlanning);
						planningsToGenerateLst.add(otherPlanning);
					}
				}
			}
		}
		// Create copy of the planningsToGenerate, to not modify the initial plannings that are used as config objects.
		Planning[] planningsToGenerate = createPlanningsForGeneration(planningsToGenerateLst.stream().toArray(Planning[]::new));
		System.out.println("Task Generate Plannings :[" + Arrays.stream(planningsToGenerate).map(p -> p.getId() + ", ").reduce("", String::concat) + "]"); //Not perfect to print (", " at the end), but it work
		System.out.println("Using the Plannings generated :[" + planningsGenerated.stream().map(p -> p.getId() + ", ").reduce("", String::concat) + "]"); //Same same.
		generatePlannings(planningsToGenerate, planningsGenerated.stream().toArray(Planning[]::new), nbGene);
		nbGene ++;
	}
	
	/**
	 * Test if the taf of the otherPlanning in a cSync is the taf of one of the Plannings in plannings.
	 * @param cSync The cSync (ConstraintSynchroniseWithTAF).
	 * @param plannings The plannings.
	 * @return true if it's the case, false otherwise.
	 */
	private static boolean cSyncInPlannings(ConstraintSynchroniseWithTAF cSync, List<Planning> plannings) {
		return plannings.stream().anyMatch(p -> p.getCalendar().getTaf().getId() == cSync.getOtherPlanning().getCalendar().getTaf().getId());
	}
	
	/**
     * Generate a config planning by copying it and solve the copy. It will ignore synchronizations.
     * @param planning The config planning
     */
	public static void generatePlanningWithoutSync(Planning planning) {
		generatePlanning(createPlanningForGeneration(planning), nbGene);
		nbGene ++;
	}
	
	/**
	 * Generate the planning Planning, without any synchronizations.
	 * @param planning The Planning to be generated.
	 * @param nbGene The number of the generation.
	 */
	private static void generatePlanning(Planning planning, int nbGene) {
		// Create a runnable object that will run the resolution.
		Runnable task = new Runnable() {
    		@Override
    		public void run() {
    			//TODO Add in SolverMain (generatePlanningWithoutSync and generatePlannings)
    			//     something to start from the last solution found if one exist (using scheduledLessons)
    			//     to deal with errors more efficiently and start from the last solution found and not totally from scratch.
    			for (int iAttempt = 1; iAttempt <= MAX_ATTEMPTS; iAttempt ++)
	    			try {
	    				System.out.println(GREEN + nbGene + " Launch background execution for " + planning.getCalendar().getTaf().getName() + " (planningId :" + planning.getId() + ", attempt : " + iAttempt + ")" + RESET);
	    				SolverMain.generatePlanningWithoutSync(planning);
	    				return; //If no error, stop.
	    			}
	    			catch (Throwable e) {
	    				System.out.println(RED + nbGene +  " Unhandled Error or Exception for " + planning.getCalendar().getTaf().getName() + " (planningId :" + planning.getId() + ", attempt : " + iAttempt + ")" + RESET);
	    				e.printStackTrace();
	    			}
    			System.out.println(RED + nbGene + " Generation Aborted." + RESET);
				planning.endProcessing();
				planning.setMessageGeneration("Erreur lors de la génération.");
				services.getPlanningService().save(planning);
    		}
    	};
    	//Launch the resolution in an another Thread.
		exec.execute(task);
	}
	
	/**
	 * Generate the plannings considering synchronizations only between them.
	 * @param planningsToGenerate The plannings to generate (The resolution is done on a copy).
	 */
	public static void generatePlannings(Planning[] planningsToGenerate) {
		generatePlannings(createPlanningsForGeneration(planningsToGenerate), new Planning[] {});
	}
	
	/**
	 * Generate the Plannings planningsToGenerate, by considerings synchronisations only between them and the plannings already generated (planningsGenerated).
	 * @param planningsToGenerate The plannings to generate (The resolution is done on a copy).
	 * @param planningsGenerated The plannings already generated to consider in the synchronisations.
	 */
	public static void generatePlannings(Planning[] planningsToGenerate, Planning[] planningsGenerated) {
		generatePlannings(createPlanningsForGeneration(planningsToGenerate), planningsGenerated, nbGene);
		nbGene ++;
	}
	
	/**
	 * Generate the Plannings planningsToGenerate, coonsidering synchronisations between them and also with planningsGenerated.
	 * @param planningsToGenerate The plannings to be generated.
	 * @param planningsGenerated The plannings already generated.
	 * @param nbGene the number of the resolution.
	 */
	private static void generatePlannings(Planning[] planningsToGenerate, Planning[] planningsGenerated, int nbGene) {
		Runnable task = new Runnable() {
    		@Override
    		public void run() {
    			for (int iAttempt = 1; iAttempt <= MAX_ATTEMPTS; iAttempt ++)
	    			try {
	    				System.out.println(GREEN + nbGene + " Launch background execution for [" +
	    										Arrays.stream(planningsToGenerate).map(p -> p.getCalendar().getTaf().getName() + "(" + p.getId() + ")").reduce("", String::concat) +
	    										"], [" +
	    										Arrays.stream(planningsGenerated).map(p -> p.getCalendar().getTaf().getName() + "(" + p.getId() + ")").reduce("", String::concat) +
	    										"] (attempt : " + iAttempt + ")" + RESET);
	    				SolverMain.generatePlannings(planningsToGenerate, planningsGenerated);
	    				return;
	    			}
	    			catch (Throwable e) {
	    				System.out.println(RED + nbGene + " Launch background execution for " + Arrays.stream(planningsToGenerate).map(p -> p.getCalendar().getTaf().getName() + "(" + p.getId() + ")").reduce("", String::concat) + " (attempt : " + iAttempt + ")" + RESET);
	    				e.printStackTrace();
	    			}
    			
    			System.out.println(RED + nbGene + " Generation Aborted." + RESET);
				for (Planning planning : planningsToGenerate) {
					planning.endProcessing();
					planning.setMessageGeneration("Erreur lors de la génération.");
					services.getPlanningService().save(planning);
				}
				return;
    		}
    	};
		exec.execute(task);
	}
}
