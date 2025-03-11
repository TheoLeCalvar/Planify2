package com.planify.server.solver;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.nary.automata.FA.CostAutomaton;
import org.chocosolver.solver.constraints.nary.automata.FA.FiniteAutomaton;
import org.chocosolver.solver.constraints.nary.automata.FA.ICostAutomaton;
import org.chocosolver.solver.search.limits.FailCounter;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainLast;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.ConflictHistorySearch;
import org.chocosolver.solver.search.strategy.selectors.variables.DomOverWDeg;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.ESat;
import org.chocosolver.util.tools.ArrayUtils;

import com.planify.server.models.Antecedence;
import com.planify.server.models.Calendar;
import com.planify.server.models.Day;
import com.planify.server.models.Lesson;
import com.planify.server.models.Planning;
import com.planify.server.models.Result;
import com.planify.server.models.ScheduledLesson;
import com.planify.server.models.Sequencing;
import com.planify.server.models.Slot;
import com.planify.server.models.Synchronization;
import com.planify.server.models.TAF;
import com.planify.server.models.UE;
import com.planify.server.models.Week;
import com.planify.server.models.constraints.ConstraintsOfUE;


public class SolverMain {
	//Services
	private static SolverServices services;
	
	private Planning planning;
	private BijectiveHashMap<Long, Integer> idMSlot;
	private HashMap<Long, IntVar> slotVarLesson;
	private HashMap<Long, IntVar> slotVarUe;
	private BijectiveHashMap<Long, Integer> idMLesson;
	private HashMap<Long, IntVar> lessonVarSlot;
	private HashMap<Long, IntVar> lessonVarSlotGlobal;
	private HashMap<Long, IntVar> lessonVarDay;
	private HashMap<Long, IntVar> lessonVarWeek;
	private BijectiveHashMap<Long, Integer> idMUe;
	private BijectiveHashMap<Long, Integer> idMDay;
	private BijectiveHashMap<Long, Integer> idMWeek;
	private IntVar obj;
	private Model model;
	
	private HashMap<Long, Integer> IdMSlotGlobal;

	private SolverMain(Planning planning) {
		this.planning = planning;
		idMSlot = new BijectiveHashMap<Long, Integer>();
		slotVarLesson = new HashMap<Long, IntVar>();
		slotVarUe = new HashMap<Long, IntVar>();
		idMLesson = new BijectiveHashMap<Long, Integer>();
		lessonVarSlot = new HashMap<Long, IntVar>();
		lessonVarSlotGlobal = new HashMap<Long, IntVar>();
		lessonVarDay = new HashMap<Long, IntVar>();
		lessonVarWeek = new HashMap<Long, IntVar>();
		idMUe = new BijectiveHashMap<Long, Integer>();
		idMDay = new BijectiveHashMap<Long, Integer>();
		idMWeek = new BijectiveHashMap<Long, Integer>();
	}
	
	private SolverMain(Planning planning, Model model) {
		this(planning);
		
		this.model = model;
		int nbSlots = this.getNumberOfSlotsWUD();
		int nbLessons = this.getNumberOfLessons();
		this.initialiseVars(nbSlots, nbLessons, isUesNeeded(planning), isDaysNeeded(planning), isWeeksNeeded(planning));
		this.setConstraints();
		this.obj = this.setPreferences();
	}
	
	private SolverMain(Planning planning, Model model, HashMap<Long, Integer> idToIdMGlobal) {
		this(planning);
		
		this.model = model;
		int nbSlots = this.getNumberOfSlotsWUD();
		int nbLessons = this.getNumberOfLessons();
		this.initialiseVars(nbSlots, nbLessons, isUesNeeded(planning), isDaysNeeded(planning), isWeeksNeeded(planning));
		this.initialiseSync(idToIdMGlobal);
		this.setConstraints();
		this.obj = this.setPreferences();
	}
	
	private IntVar getObj() {return this.obj;};
	private Model getModel() {return this.model;};
	private Planning getPlanning() {return this.planning;}
	private Calendar getCalendar() {return this.getPlanning().getCalendar();}
	private TAF getTaf() {return this.getCalendar().getTaf();}
	private List<UE> getUes() {return this.getTaf().getUes();}
	private List<Lesson> getLessons() {return this.getUes().stream().flatMap(u -> u.getLessons().stream()).toList();}
	private List<Slot> getSlotsOrderedWUD() {return services.getCalendarService().getSlotsOrderedWithoutUnavailableDays(getCalendar().getId());}
	private List<Day> getDaysOrderedWU() {return services.getCalendarService().getDaysSortedWithoutUnavailable(getCalendar().getId());}
	private List<Week> getWeeksOrderedWU() {return services.getCalendarService().getWeeksSortedWithoutUnavailable(getCalendar().getId());}
	private List<Slot> getSlotsByDayOrdered(Day day) {return services.getDayService().findSlotsDayByCalendarSorted(day, getCalendar());}
	private List<Slot> getSlotsByDay(Day day) {return services.getDayService().findSlotsDayByCalendar(day, getCalendar());}
	private List<Slot> getSlotsByWeekWUD(Week week) {return services.getSlotService().findSlotsByWeekAndCalendarWithoutUnavailableDays(week, getCalendar());}
	private int getNumberOfSlotsWUD() {return getSlotsOrderedWUD().size();}
	private int getNumberOfLessons() {return services.getTafService().numberOfLessons(getTaf().getId());}
	private ConstraintsOfUE getConstraintsOfUe(UE ue) {return getPlanning().getConstraintsOfUEs().stream().filter(c -> c.getUe().getId() == ue.getId()).findAny().get();}
	
	private static boolean isUesNeeded(Planning planning) {return planning.isLessonGrouping()
															|| planning.isMiddayGrouping()
															|| planning.isUEInterlacing()
															|| planning.isLessonCountInWeek()
															|| planning.isMaxTimeWithoutLesson();}
	
	private static boolean isDaysNeeded(Planning planning) {return planning.isMaxTimeWithoutLesson() && planning.isMaxTimeWLUnitInDays();}
	
	private static boolean isWeeksNeeded(Planning planning) {return planning.isMaxTimeWithoutLesson() && planning.isMaxTimeWLUnitInWeeks()
															|| planning.isSpreadingUe();}
	
	private Integer getIdMSlot(Slot slot) {return idMSlot.getValue(slot.getId());}
	private Integer[] getIdMSlot(Slot[] slots) {return IntStream.range(0, slots.length).mapToObj(i -> getIdMSlot(slots[i])).toArray(Integer[]::new);}
	private Long getIdSlot(Integer idM) {return idMSlot.getKey(idM);}
	private Long[] getIdSlot(Integer[] idMs) {return IntStream.range(0, idMs.length).mapToObj(i -> getIdSlot(idMs[i])).toArray(Long[]::new);}
	
	private IntVar getSlotVarLesson(Slot slot) {return slotVarLesson.get(slot.getId());}
	private IntVar[] getSlotVarLesson(Slot[] slots) {return IntStream.range(0, slots.length).mapToObj(i -> getSlotVarLesson(slots[i])).toArray(IntVar[]::new);}
	
	private IntVar getSlotVarUe(Slot slot) {return slotVarUe.get(slot.getId());}
	private IntVar[] getSlotVarUe(Slot[] slots) {return IntStream.range(0, slots.length).mapToObj(i -> getSlotVarUe(slots[i])).toArray(IntVar[]::new);}
	
	private Integer getIdMLesson(Lesson lesson) {return idMLesson.getValue(lesson.getId());}
	private Integer[] getIdMLesson(Lesson[] lessons) {return IntStream.range(0, lessons.length).mapToObj(i -> getIdMLesson(lessons[i])).toArray(Integer[]::new);}
	private Long getIdLesson(Integer idM) {return idMLesson.getKey(idM);}
	private Long[] getIdLesson(Integer[] idMs) {return IntStream.range(0, idMs.length).mapToObj(i -> getIdLesson(idMs[i])).toArray(Long[]::new);}
	
	private IntVar getLessonVarSlot(Lesson lesson) {return lessonVarSlot.get(lesson.getId());}
	private IntVar[] getLessonVarSlot(Lesson[] lessons) {return IntStream.range(0, lessons.length).mapToObj(i -> getLessonVarSlot(lessons[i])).toArray(IntVar[]::new);}

	private IntVar getLessonVarSlotGlobal(Lesson lesson) {return lessonVarSlotGlobal.get(lesson.getId());}
	private IntVar[] getLessonVarSlotGlobal(Lesson[] lessons) {return IntStream.range(0, lessons.length).mapToObj(i -> getLessonVarSlotGlobal(lessons[i])).toArray(IntVar[]::new);}
	
	private IntVar getLessonVarDay(Lesson lesson) {return lessonVarDay.get(lesson.getId());}
	private IntVar[] getLessonVarDay(Lesson[] lessons) {return IntStream.range(0, lessons.length).mapToObj(i -> getLessonVarDay(lessons[i])).toArray(IntVar[]::new);}
	
	private IntVar getLessonVarWeek(Lesson lesson) {return lessonVarWeek.get(lesson.getId());}
	private IntVar[] getLessonVarWeek(Lesson[] lessons) {return IntStream.range(0, lessons.length).mapToObj(i -> getLessonVarWeek(lessons[i])).toArray(IntVar[]::new);}
	
	private Integer getIdMUe(UE ue) {return idMUe.getValue(ue.getId());}
	private Integer[] getIdMUe(UE[] ues) {return IntStream.range(0, ues.length).mapToObj(i -> getIdMUe(ues[i])).toArray(Integer[]::new);}
	private Long getIdUe(Integer idM) {return idMUe.getKey(idM);}
	private Long[] getIdUe(Integer[] idMs) {return IntStream.range(0, idMs.length).mapToObj(i -> getIdUe(idMs[i])).toArray(Long[]::new);}
	
	private Integer getIdMDay(Day day) {return idMDay.getValue(day.getId());}
	private Integer[] getIdMDay(Day[] days) {return IntStream.range(0, days.length).mapToObj(i -> getIdMDay(days[i])).toArray(Integer[]::new);}
	private Long getIdDay(Integer idM) {return idMDay.getKey(idM);}
	private Long[] getIdDay(Integer[] idMs) {return IntStream.range(0, idMs.length).mapToObj(i -> getIdDay(idMs[i])).toArray(Long[]::new);}
	
	private Integer getIdMWeek(Week week) {return idMWeek.getValue(week.getId());}
	private Integer[] getIdMWeek(Week[] weeks) {return IntStream.range(0, weeks.length).mapToObj(i -> getIdMWeek(weeks[i])).toArray(Integer[]::new);}
	private Long getIdWeek(Integer idM) {return idMWeek.getKey(idM);}
	private Long[] getIdWeek(Integer[] idMs) {return IntStream.range(0, idMs.length).mapToObj(i -> getIdWeek(idMs[i])).toArray(Long[]::new);}
	
	private Integer getIdMSlotGlobal(Slot slot) {return IdMSlotGlobal.get(slot.getId());}
	private Integer[] getIdMSlotGlobal(Slot[] slots) {return IntStream.range(0, slots.length).mapToObj(i -> getIdMSlotGlobal(slots[i])).toArray(Integer[]::new);}
	
	/**
	 * Set the object SolverServices used by the solver to gather data from the backend.
	 * @param services The instance of SolverService.
	 */
	public static void setServices(SolverServices services) {
		SolverMain.services = services;
		SolverExecutor.setServices(services);
	}
	
	/**
	 * Generate the planning without considering synchronizations using the parameters in the object planning.
	 * @param planning The planning to generate.
	 * @return The results generated (also stored automatically in the database).
	 */
	public static boolean generatePlanningWithoutSync(Planning planning) {
		planning.startProcessing();
		planning.setMessageGeneration("Début de la génération et recherche d'une première solution.");
		services.getPlanningService().save(planning);
		Model model = new Model();
		Solver solver = model.getSolver();
		SolverMain solMain = new SolverMain(planning, model);
		IntVar obj = solMain.getObj();
		setStrategy(solMain);
		solver.showSolutions();
		if (obj != null) model.setObjective(false, obj);
		Solution solution = solveModelPlanning(solMain, planning.getMaxSolveDuration());
		//System.out.println(model);
		solver.printShortStatistics();
		planning.endProcessing();
		planning.setSolutionOptimal(solver.isObjectiveOptimal());
		planning.setMessageGeneration(planning.getScheduledLessons().isEmpty() ? "Aucune solution trouvée." : "Génération réussie en " + solver.getTimeCount() +" s !");
		services.getPlanningService().save(planning);
		if (solution == null)
			return false;
		System.out.println(solMain.showSolutionsDebug(solution));
		System.out.println(solMain.makeSolutionString(solution));
		return true;
	}
	
	/**
	 * Used for testing.
	 * Generate the planning without considering synchronizations using the parameters in the object planning.
	 * @param planning The planning to generate.
	 * @return The results generated in a json format (not stored automatically in the database).
	 */
	public static String generatePlanningString(Planning planning) {
		planning.setMessageGeneration("Début de la génération et recherche d'une première solution.");
		services.getPlanningService().save(planning);
		Model model = new Model();
		Solver solver = model.getSolver();
		SolverMain solMain = new SolverMain(planning, model);
		IntVar obj = solMain.getObj();
		setStrategy(solMain);
		//solver.verboseSolving(1000);
		solver.showSolutions();
		//solver.showDecisions();
		if (obj != null) model.setObjective(false, obj);
		Solution solution = solveModelPlanning(solMain, planning.getMaxSolveDuration());
		//solution = solver.findSolution();
		System.out.println(Arrays.deepToString(model.getVars()));
		solver.printShortStatistics();
		planning.endProcessing();
		planning.setSolutionOptimal(solver.isObjectiveOptimal());
		planning.setMessageGeneration(planning.getScheduledLessons().isEmpty() ? "Aucune solution trouvée." : "Génération réussie !");
		services.getPlanningService().save(planning);
		if (solution == null)
			return "";
		System.out.println(solMain.showSolutionsDebug(solution));
		System.out.println(solMain.makeSolutionString(solution));
		return solMain.makeSolutionString(solution);
	}
	
	/**
	 * Generate the plannings planningsToGenerate considering the synchronizations only between them.
	 * @param planningsToGenerate The plannings to generate.
	 * @return The results generated (also stored automatically in the database for each planning).
	 */
	public static boolean generatePlannings(Planning[] planningsToGenerate) {
		return generatePlannings(planningsToGenerate, new Planning[] {});
	}
	
	/**
	 * Generate the plannings planningsToGenerate considering the synchronizations only between them and with the fixes plannings planningsGenerated.
	 * @param planningsToGenerate The plannings to generate.
	 * @param planningsGenerated The plannings already generated to consider in the synchronizations.
	 * @return The results generated (also stored automatically in the database for each planning to generate).
	 */
	public static boolean generatePlannings(Planning[] planningsToGenerate, Planning[] planningsGenerated) {
		for (Planning planning : planningsToGenerate) {
			planning.startProcessing();
			planning.setMessageGeneration("Début de la génération et recherche d'une première solution.");
			services.getPlanningService().save(planning);
		}
		Model model = new Model();
		Solver solver = model.getSolver();
		IntVar[] objs = new IntVar[planningsToGenerate.length];
		SolverMain[] solMains = new SolverMain[planningsToGenerate.length];
		HashMap<Long, Integer> idToIdMGlobal = getIdToIdMGlobalPlannings(planningsToGenerate);
		for (int i = 0; i < planningsToGenerate.length; i ++) {
			solMains[i] = new SolverMain(planningsToGenerate[i], model, idToIdMGlobal);
			IntVar obj = solMains[i].getObj();
			objs[i] = obj != null ? obj : model.intVar(0);
		}
		setSynchronisationConstraints(model, solMains, planningsGenerated);
		IntVar globObj = model.sum("globObj", objs);
		setStrategy(solMains, model);
		//solver.showSolutions();
		if (!globObj.isInstantiatedTo(0)) model.setObjective(false, globObj);
		Solution solution = solveModelPlannings(model, solMains, planningsToGenerate[0].getMaxSolveDuration());
		//System.out.println(model);
		solver.printShortStatistics();
		for (Planning planning : planningsToGenerate) {
			planning.endProcessing();
			planning.setSolutionOptimal(solver.isObjectiveOptimal());
			planning.setMessageGeneration(planning.getScheduledLessons().isEmpty() ? "Aucune solution trouvée." : "Génération réussie !");
			services.getPlanningService().save(planning);
		}
		if (solution == null)
			return false;
		for (int i = 0; i < planningsToGenerate.length; i ++) {
			System.out.println(solMains[i].showSolutionsDebug(solution));
		}
		return true;
	}
	
	private static String formatLimitTime(LocalTime limitTime) {
		return limitTime.getHour() + "h " + limitTime.getMinute() + "m " + limitTime.getSecond() + "s";
	}
	
	private static Solution solveModelPlanning(SolverMain solMain, LocalTime limitTime) {
		System.out.println("Start Solving !");
		Model model = solMain.getModel();
		Solver solver = model.getSolver();
		Solution s = new Solution(model);
		if (limitTime != null) solver.limitTime(formatLimitTime(limitTime));
		//s.limitSearch(() -> { /*todo return true if you want to stop search*/ }); //Can be useful to stop the search from the front-end.
		boolean noObjective = model.getObjective() == null;
		while (solver.solve()) {
		     s.record();
		     List<Result> results = solMain.makeSolution(s);
		     solMain.getPlanning().setMessageGeneration("Amélioration de la solution trouvée. (" + solver.getTimeCount() + " s depuis le début de la génération.)");
		     services.getPlanningService().addScheduledLessons(solMain.getPlanning(), results);
		     if (noObjective) return solver.isFeasible() == ESat.TRUE ? s : null;
		}
		return solver.isFeasible() == ESat.TRUE ? s : null;
	}
	
	private static Solution solveModelPlannings(Model model, SolverMain[] solMains, LocalTime limitTime) {
		System.out.println("Start Solving !");
		Solver solver = model.getSolver();
		Solution s = new Solution(model);
		if (limitTime != null) solver.limitTime(formatLimitTime(limitTime));
		//s.limitSearch(() -> { /*todo return true if you want to stop search*/ }); //Can be useful to stop the search from the front-end.
		boolean noObjective = model.getObjective() == null;
		while (solver.solve()) {
		     s.record();
		     for (SolverMain solMain : solMains) {
			     List<Result> results = solMain.makeSolution(s);
			     solMain.getPlanning().setMessageGeneration("Amélioration de la solution trouvée. (" + solver.getTimeCount() + " s depuis le début de la génération.)");
			     services.getPlanningService().addScheduledLessons(solMain.getPlanning(), results);
		     }
		     if (noObjective) return solver.isFeasible() == ESat.TRUE ? s : null;
		}
		return solver.isFeasible() == ESat.TRUE ? s : null;
	}
	
	/**
	 * Initialise the variables of this.planning for the synchronisations.
	 * @param idToIdMGlobal
	 */
	private void initialiseSync(HashMap<Long, Integer> idToIdMGlobal) {
		this.IdMSlotGlobal = idToIdMGlobal;
		int[] valGlobs = getValsSlotGlobal(getSlotsOrderedWUD().toArray(Slot[]::new));
		for (Lesson lesson : getLessons())
				lessonVarSlotGlobal.put(lesson.getId(), getModel().intVar(nameLesson(lesson) + "-VarSlotGlobal", valGlobs));
	}
	
	 /**
	  * Get the globalId of each slot as int (and not Integer).
	  * @param slots The slots.
	  * @return The globalIds.
	  */
	private int[] getValsSlotGlobal(Slot[] slots) {
		ArrayList<Integer> vals = new ArrayList<Integer>();
		for (Slot slot : slots)
			vals.add(getIdMSlotGlobal(slot));
		return vals.stream().mapToInt(i -> (int) i).toArray();
	}
	
	/**
	 * Initialise the variables of this.planning for the constraints (except synchronisations).
	 * @param nbSlots The number of slots in the calendar of this.planning.
	 * @param nbLessons The number of slots in the taf of this.planning.
	 * @param varUe
	 * @param varDay
	 * @param varWeek
	 */
	private void initialiseVars(int nbSlots, int nbLessons, boolean varUe, boolean varDay, boolean varWeek) {
		initialiseWeeks();
		initialiseDays();
		initialiseSlots(nbLessons, varUe, getUes().size());
		initialiseUes();
		initialiseLessons(nbSlots,
				varDay, varDay ? getValsIdMDays() : null,
				varWeek, varWeek ? getWeeksOrderedWU().size() : 0);
	}
	
	/**
	 * Initialise the values corresponding to each week for the variables.
	 */
	private void initialiseWeeks() {
		Integer idMW = 1;
		List<Week> weeks = getWeeksOrderedWU();
		for (Week week : weeks) {
			idMWeek.put(week.getId(), idMW);
			idMW ++;
		}
	}
	
	/**
	 * Initialise the values corresponding to each day for the variables.
	 */
	private void initialiseDays() {
		List<Day> days = getDaysOrderedWU();
		Integer idMFirstWeek = getIdMWeek(days.getFirst().getWeek());
		for (Day day : days) {
			idMDay.put(day.getId(), 5 * (getIdMWeek(day.getWeek()) - idMFirstWeek) + day.getNumber());
		}
	}
	
	/**
	 * Initialise the variables and the values corresponding to each slot
	 * @param nbLessons The number of Lessons in the TAF.
	 * @param varUe activate or desactivate the creation of variables slot-VarUe.
	 * @param nbUes The number of UEs in the TAF.
	 */
	private void initialiseSlots(int nbLessons, boolean varUe, int nbUes) {
		Integer idMS = 1;
		for (Slot slot : getSlotsOrderedWUD()) {
			idMSlot.put(slot.getId(), idMS);
			slotVarLesson.put(slot.getId(), getModel().intVar(nameSlot(slot) + "-VarLesson", 0, nbLessons));
			if (varUe) slotVarUe.put(slot.getId(), getModel().intVar(nameSlot(slot) + "-VarUe", 0, nbUes));
			idMS ++;
		}
	}
	
	/**
	 * Initialise the values corresponding to each Ue for the variables.
	 */
	private void initialiseUes() {
		Integer idMU = 1;
		for (UE ue : getUes()) {
			idMUe.put(ue.getId(), idMU);
			idMU ++;
		}
	}
	
	/**
	 * Initialise the variables and the values corresponding to each lesson.
	 * @param nbSlots The number of slots in the Calendar.
	 * @param varDay Activate or desativate the creation of variables lesson-varDay.
	 * @param valsDays The values for the variables lesson-varDay.
	 * @param varWeek Activate or desactivate the creation of variables lesson-varWeek
	 * @param nbWeeks The number of weeks in the Calendar.
	 */
	private void initialiseLessons(int nbSlots, boolean varDay, int[] valsDays, boolean varWeek, int nbWeeks) {
		Integer idML = 1;
		for (Lesson lesson : getLessons()) {
			idMLesson.put(lesson.getId(), idML);
			lessonVarSlot.put(lesson.getId(), getModel().intVar(nameLesson(lesson) + "-VarSlot", 1, nbSlots));
			if (varDay) lessonVarDay.put(lesson.getId(), getModel().intVar(nameLesson(lesson) + "-VarDay", valsDays));
			if (varWeek) lessonVarWeek.put(lesson.getId(), getModel().intVar(nameLesson(lesson) + "-VarWeek", 1, nbWeeks));
			idML ++;
		}
	}
	
	/**
	 * Get the values (idM, id Model) for the variables corresponding to each day.
	 * @return The idM of each day.
	 */
	private int[] getValsIdMDays() {
		return getArrayInt(idMDay.valueSet().toArray(Integer[]::new));
	}
	
	/**
	 * Generate the correspondence between a slot and his idMGlobal.
	 * It link a slot to an id using a growing order (First slot will be idMGlobal 1, the second idMGlobal 2 etc)
	 * Slots happening at the same time have the same idMGlobal.
	 * @param plannings The plannings to consider in the idMGlobal.
	 * @return The correspondence.
	 */
	private static HashMap<Long, Integer> getIdToIdMGlobalPlannings(Planning[] plannings){
		HashMap<Long, Integer> idToIdMGlob = new HashMap<Long, Integer>();
		List<List<Slot>> slots = IntStream.range(0, plannings.length).mapToObj(i -> services.getCalendarService().getSlotsOrderedWithoutUnavailableDays(plannings[i].getCalendar().getId())).toList();
		int[] iSlots = new int[plannings.length];
		int[] lengthSlots = slots.stream().mapToInt(s -> s.size()).toArray();
		Integer idMGlob = 1;
		while (testiLengths(iSlots, lengthSlots)) {
			List<Integer> iMins = new ArrayList<Integer>();
			for (int i = 0; i < plannings.length; i ++) {
				System.out.println(i);
				System.out.println(iSlots[i] < lengthSlots[i]);
				if (iSlots[i] < lengthSlots[i]) {
					if (iMins.size() == 0) {
						iMins.add(i);
					}
					else {
						int comparison;
						try {
							comparison = slots.get(i).get(iSlots[i]).compareTo(slots.get(iMins.get(0)).get(iSlots[iMins.get(0)]));
						}
						//If the two slots don't have the same duration
						catch (ClassCastException e) {
							//We take the one starting the earlier (But it's not necessary to make this choice, it's just a practical choice to have the idGlobs increasing with time)
							comparison = slots.get(i).get(iSlots[i]).getStart().compareTo(slots.get(iMins.get(0)).get(iSlots[iMins.get(0)]).getStart());
							//If they start at the same moment, we take the one ending earlier.
							if (comparison == 0)
								comparison = slots.get(i).get(iSlots[i]).getEnd().compareTo(slots.get(iMins.get(0)).get(iSlots[iMins.get(0)]).getEnd());
						}
						if (comparison < 0) {
							//System.out.println("Lower " + iSlots[i]);
							iMins.clear();
							iMins.add(i);
						}
						if (comparison == 0) {
							//System.out.println("Equal " + iSlots[i]);
							iMins.add(i);
						}
					}
				}
			}
			for (int i : iMins) {
				idToIdMGlob.put(slots.get(i).get(iSlots[i]).getId(), idMGlob);
				iSlots[i] ++;
			}
			idMGlob ++;
		}
		return idToIdMGlob;
	}
	
	/**
	 * Test if for each i, is[i] < lengths[i].
	 * @param is
	 * @param lengths
	 * @return 
	 */
	private static boolean testiLengths(int[] is, int[] lengths) {
		for (int i = 0; i < is.length; i ++)
			if (is[i] < lengths[i])
				return true;
		return false;
	}
	
	/**
	 * Set the constraints (Except synchronizations).
	 */
	private void setConstraints() {
		setConstraintLinkLessonsSlots(isUesNeeded(getPlanning()));
		setConstraintLinkSlotGlobalDayWeek(this.IdMSlotGlobal != null, isDaysNeeded(getPlanning()), isWeeksNeeded(getPlanning()));
		setConstraintSequences();
		setConstraintAntecedences();
		setConstraintGlobalUnavailability();
		setConstraintLecturerUnavailability();
		if (getPlanning().isMiddayBreak()) setConstraintLunchBreak();
		if (getPlanning().isUEInterlacing()) setConstraintNoInterweaving();
		if (getPlanning().isLessonCountInWeek()) setConstraintMinMaxLessonUeInWeek(); //Idée pour essayer d'améliorer les performances si besoin : essayer de faire une stratégie de recherche sur les variables du nombre de cours de l'UE considéré.
		if (getPlanning().isSpreadingUe()) setConstraintMinMaxWeeksUe();
	}
	
	private void setConstraintLinkLessonsSlots(boolean ue) {
		Model model = getModel();
		Slot[] slots = getSlotsOrderedWUD().stream().toArray(Slot[]::new);
		Lesson[] lessons = getLessons().stream().toArray(Lesson[]::new);
		int nbSlots = slots.length;
		int nbLessons = lessons.length;
		IntVar[][] slotsV = IntStream.range(0, nbSlots).mapToObj(i -> (ue) ? new IntVar[] {getSlotVarLesson(slots[i]), getSlotVarUe(slots[i])}: new IntVar[] {getSlotVarLesson(slots[i])}).toArray(IntVar[][]::new);
		IntVar[] lessonsV = new IntVar[nbSlots];
		IntVar[][] sortedSlotsV = new IntVar[nbSlots][(ue)?2:1];
		IntVar zero = model.intVar("zero", 0);
		for (int i = 0; i < nbSlots - nbLessons; i ++) {
			lessonsV[i] = model.intVar("DummyKeySort1 " + i, 1, nbSlots);
			sortedSlotsV[i][0] = zero;
			if (ue) sortedSlotsV[i][1] = zero;
		}
		for (int i = nbSlots - nbLessons; i < nbSlots; i ++) {
			lessonsV[i] = getLessonVarSlot(lessons[i - nbSlots + nbLessons]);
			sortedSlotsV[i][0] = model.intVar("SortedSlotsV " + i, i - nbSlots + nbLessons + 1);
			if (ue) sortedSlotsV[i][1] = model.intVar("SortedSlotsVUe" + i, getIdMUe(services.getLessonService().findById(getIdLesson(i - nbSlots + nbLessons + 1)).get().getUe()));
		}
		model.keySort(slotsV, lessonsV, sortedSlotsV, 1).post();
	}
	
	private void setConstraintLinkSlotGlobalDayWeek(boolean global, boolean day, boolean week) {
		Model model = getModel();
		if (!(global || day || week)) return;
		List<Slot> slots = getSlotsOrderedWUD();
		int[] globInt = new int[] {};
		if (global) globInt = slots.stream().mapToInt(s -> getIdMSlotGlobal(s)).toArray();
		int[] dayInt = new int[] {};
		System.out.println(slots.stream().map(s -> s.toString()).reduce("", String::concat));
		System.out.println(idMDay.keySet().toString());
		if (day) dayInt = slots.stream().mapToInt(s -> getIdMDay(s.getDay())).toArray();
		int[] weekInt = new int[] {};
		if (week) weekInt = slots.stream().mapToInt(s -> getIdMWeek(s.getDay().getWeek())).toArray();
		for (Lesson lesson : getLessons()) {
			if (global) model.element(getLessonVarSlotGlobal(lesson), globInt, getLessonVarSlot(lesson),1).post();
			if (day) model.element(getLessonVarDay(lesson), dayInt, getLessonVarSlot(lesson),1).post();
			if (week) model.element(getLessonVarWeek(lesson), weekInt, getLessonVarSlot(lesson),1).post();
		}
	}
	
	private void setConstraintSequences() {
		Model model = getModel();
		List<Sequencing> sequences = services.getSequencingService().getSequencingOf(getTaf().getId());
		List<List<Lesson>> aglomerateSequences = new ArrayList<List<Lesson>>();
		while (sequences.size() > 0) {
			Sequencing firstSequencing = sequences.removeFirst();
			Lesson previous = firstSequencing.getPreviousLesson();
			Lesson next = firstSequencing.getNextLesson();
			aglomerateSequences.add(new ArrayList<Lesson>());
			while (previous != null) {
				aglomerateSequences.getLast().add(0, previous);
				int i;
				for (i = 0; i < sequences.size() && sequences.get(i).getNextLesson().getId() != previous.getId(); i ++);
				if (i != sequences.size()) previous = sequences.remove(i).getPreviousLesson();
				else previous = null;
			}
			while (next != null) {
				aglomerateSequences.getLast().add(next);
				int i;
				for (i = 0; i < sequences.size() && sequences.get(i).getPreviousLesson().getId() != next.getId(); i ++);
				if (i != sequences.size()) next = sequences.remove(i).getNextLesson();
				else next = null;
			}
		}
		//System.out.println("Sequencings : " + aglomerateSequences);
		for (Day day : getDaysOrderedWU()) {
			List<Slot> slots = getSlotsByDayOrdered(day);
			Integer[] idMSlots = getIdMSlot(slots.stream().toArray(Slot[]::new));
			for (List<Lesson> sequence : aglomerateSequences) {
				int lengthSequence = sequence.size();
				for (int iLesson = 0; iLesson < lengthSequence - 1; iLesson ++)
					model.arithm(getLessonVarSlot(sequence.get(iLesson + 1)), "=", getLessonVarSlot(sequence.get(iLesson)), "+", 1).post();
				
				for (int iLesson = 0; iLesson < lengthSequence; iLesson ++) {
					for (int iSlotBegin = 0; iSlotBegin < iLesson; iSlotBegin ++)
						model.arithm(getLessonVarSlot(sequence.get(iLesson)), "!=", idMSlots[iSlotBegin]).post();
					for (int iSlotEnd = idMSlots.length - 1; iSlotEnd > idMSlots.length - lengthSequence + iLesson; iSlotEnd --)
						model.arithm(getLessonVarSlot(sequence.get(iLesson)), "!=", idMSlots[iSlotEnd]).post();
				}
			}
		}
	}
	
	private void setConstraintAntecedences() {
		Model model = getModel();
		List<Antecedence> antecedences = services.getAntecedenceService().getAntecedencesByIdTaf(getTaf().getId());
	    for (Antecedence antecedence : antecedences) {
	    	Lesson previousLesson = antecedence.getPreviousLesson();
	        Lesson nextLesson = antecedence.getNextLesson();
	        IntVar previousSlot = getLessonVarSlot(previousLesson);
	        IntVar nextSlot = getLessonVarSlot(nextLesson);
	        
	        model.arithm(nextSlot, ">", previousSlot).post();
		}
	}
	
	private void setConstraintGlobalUnavailability() {
		for (Slot slot : getSlotsOrderedWUD())
			if (services.getGlobalUnavailabilityService().findBySlot(slot).filter(g -> g.getStrict()).isPresent())
				getModel().arithm(this.getSlotVarLesson(slot), "=", 0).post();
	}

	private void setConstraintLecturerUnavailability() {
		for (Lesson lesson : getLessons())
			for (Slot slot : services.getLessonService().findLessonLecturersUnavailabilitiesByLessonAndCalendarWUD(lesson, getPlanning().getCalendar()))
				getModel().arithm(getLessonVarSlot(lesson), "!=", getIdMSlot(slot)).post();
	}
	
	private void setConstraintLunchBreak() {
		Model model = getModel();
		LocalTime startLunch = getPlanning().getStartMiddayBreak();
		LocalTime endLunch = getPlanning().getEndMiddayBreak();
		for (Day day : getDaysOrderedWU()) {
			List<Slot> possibleSlotsForLunchTime = new ArrayList<Slot>();
			List<Slot> slotsDay = getSlotsByDayOrdered(day);
			//If the end of the slots possible this day is before the end of the lunchBreak (or conversely with the beginning),
			//then we don't need to have a lunch break in the timetable.
			if (!(endLunch.isAfter(slotsDay.getLast().getEnd().toLocalTime()) || startLunch.isBefore(slotsDay.getFirst().getStart().toLocalTime())))
				for (Slot slot: slotsDay) {
					if (!(startLunch.isAfter(slot.getEnd().toLocalTime()) || endLunch.isBefore(slot.getStart().toLocalTime())))
						possibleSlotsForLunchTime.add(slot);
				}
			if (possibleSlotsForLunchTime.size() != 0) {
				boolean lunchBreakAlreadyFixed = false;
				for (Slot slot : possibleSlotsForLunchTime) // If one of the slots is unavailable, then it is considered as the lunch break.
					if (services.getGlobalUnavailabilityService().findBySlot(slot).filter(g -> g.getStrict()).isPresent())
						lunchBreakAlreadyFixed = true;
				if (!lunchBreakAlreadyFixed) {
					IntVar count = model.intVar("Count Lunch Day-" + getIdMDay(day),1, possibleSlotsForLunchTime.size());
					model.count(0, possibleSlotsForLunchTime.stream().map(s -> getSlotVarLesson(s)).toArray(IntVar[]::new), count).post();
				}
			}
		}
	}
	
	private void setConstraintNoInterweaving() {
		Model model = getModel();
		HashMap<Long, FiniteAutomaton> automatons = new HashMap<Long,FiniteAutomaton>();
		List<UE> ues = getUes();
		int nbUe = ues.size();
		for (UE ue : ues) {
			String ueString = "<" + getIdMUe(ue) + ">";
			automatons.put(ue.getId(), new FiniteAutomaton("[^" + ueString + "]*[" + ueString + "|0]*[^" + ueString + "]*", 0, nbUe));
		}
		for (Day day : getDaysOrderedWU()) {
			//System.out.println(services.getDayService().findSlotsDayByCalendar(day, cal));
			IntVar[] varsDay = getSlotsByDayOrdered(day).stream().map(s -> getSlotVarUe(s)).toArray(IntVar[]::new);
			//System.out.println(Arrays.deepToString(varsDay));
			if (varsDay.length > 0)
				for (UE ue : ues) {
					//System.out.println(getIdMUe(ue));
					model.regular(varsDay, automatons.get(ue.getId())).post();
				}
		}
	}
	
	private void setConstraintMinMaxLessonUeInWeek() {
		Model model = getModel();
		for (Week week : getWeeksOrderedWU()) {
			IntVar[] varSlots = getSlotVarUe(getSlotsByWeekWUD(week).stream().toArray(Slot[]::new));
			for (UE ue : getUes()) {
				ConstraintsOfUE cUe = getConstraintsOfUe(ue);
				int min = cUe.getMinLessonInWeek();
				int max = cUe.getMaxLessonInWeek();
				int[] valsCnt = new int[max - min + 2];
				valsCnt[0] = 0;
				for (int i = 1; i <= max - min + 1; i ++)
					valsCnt[i] = min + i - 1;
				//System.out.println(valsCnt[valsCnt.length - 1]);
				IntVar cnt = model.intVar("Cnt min max " + ue.getName() + ", Week :" + week.getNumber(), valsCnt);
				model.count(getIdMUe(ue), varSlots, cnt).post();
			}
		}
	}
	
	private HashMap<Integer, List<IntVar>> setConstraintSortedLessonsUeVarDayOrWeek() {
		Model model = getModel();
		HashMap<Integer, List<IntVar>> sortedLessonsVar = new HashMap<Integer, List<IntVar>>();
		for (UE ue : getUes()) {
			ConstraintsOfUE cUe = getConstraintsOfUe(ue);
			if (cUe.isMaxTimeWithoutLesson()) {
				IntVar[][] vars;
				int[] valsVars;
				if (cUe.isMaxTimeWLUnitInWeeks()) {
					vars = ue.getLessons().stream().map(l -> new IntVar[] {getLessonVarWeek(l)}).toArray(IntVar[][]::new);
					valsVars = IntStream.range(1,1 + getWeeksOrderedWU().size()).toArray();
				}
				else {
					vars = ue.getLessons().stream().map(l -> new IntVar[] {getLessonVarDay(l)}).toArray(IntVar[][]::new);
					valsVars =  this.getValsIdMDays();
				}
				IntVar[][] sortedVars = IntStream.range(0,vars.length).mapToObj(i -> new IntVar[] {model.intVar("sortedVarLessUe " + ue.getName() + " (" + i + ")", valsVars)}).toArray(IntVar[][]::new);
				IntVar[] permutations = model.intVarArray("Perm SortedLessUe " + ue.getName(), vars.length, 1, vars.length);
				model.keySort(vars, permutations, sortedVars, 1).post();
				sortedLessonsVar.put(getIdMUe(ue), Arrays.stream(sortedVars).map(t -> t[0]).toList());
			}
		}
		return sortedLessonsVar;
	}
	
	private void setConstraintMinMaxWeeksUe() {
		Model model = getModel();
		for (UE ue : getUes()) {
			ConstraintsOfUE cUe = getConstraintsOfUe(ue);
			int min = cUe.getMinSpreading() - 1;
			int max = cUe.getMaxSpreading() - 1;
			IntVar[] vars = getLessonVarWeek(ue.getLessons().stream().toArray(Lesson[]::new));
			IntVar distance = model.intVar("Distance Weeks " + ue.getName(), min, max);
			model.arithm(distance, "=", model.max("Max Week " + ue.getName(), vars), "-", model.min("Min Week " + ue.getName(), vars)).post();
		}
	}
	
	private static void setSynchronisationConstraints(Model model, SolverMain[] solMains, Planning[] planningsGenerated) {
		for (int i = 0; i < solMains.length; i ++) {
			for (Synchronization sync : services.getSynchronizationService().getSynchronizationsByIdTaf(solMains[i].getPlanning().getCalendar().getTaf().getId())) {
				SolverMain solverMain1 = getSolMainTaf(solMains, sync.getLesson1().getUe().getTaf());
				SolverMain solverMain2 = getSolMainTaf(solMains, sync.getLesson2().getUe().getTaf());
				//If lesson1 is not in a planning we need to generate
				if (solverMain1 == null) {
					Planning planning1 = getPlanningTaf(planningsGenerated, sync.getLesson1().getUe().getTaf());
					//If lesson1 is in a planning already generated (if not we don't want to consider the sync)
					if (planning1 != null) {
						Slot slotLesson2 = getSlotFromLessonAndFixedPlanning(sync.getLesson1(), planning1.getScheduledLessons(), solMains[i]);
						if (slotLesson2 != null)
							model.arithm(solMains[i].getLessonVarSlot(sync.getLesson2()), "=", solMains[i].getIdMSlot(slotLesson2)).post();
						else
							System.out.println("[setSynchronisationConstraints] Slot or ScheduledLesson not found for lesson : " + sync.getLesson1());
					}
				}
				//If lesson2 is not in a planning we need to generate
				else if (solverMain2 == null) {
					Planning planning2 = getPlanningTaf(planningsGenerated, sync.getLesson2().getUe().getTaf());
					//If lesson2 is in a planning already generated (if not we don't want to consider the sync)
					if (planning2 != null) {
						Slot slotLesson1 = getSlotFromLessonAndFixedPlanning(sync.getLesson2(), planning2.getScheduledLessons(), solMains[i]);
						if (slotLesson1 != null)
							model.arithm(solMains[i].getLessonVarSlot(sync.getLesson1()), "=", solMains[i].getIdMSlot(slotLesson1)).post();
						else
							System.out.println("[setSynchronisationConstraints] Slot or ScheduledLesson not found for lesson : " + sync.getLesson2());
					}
				}
				//If the two lessons are in plannings we wants to generate (We test only with solverMain1 to break symmetry and do not have two constraint for each sync).
				else if (solverMain1.getPlanning().getId() == solMains[i].getPlanning().getId()) {
					model.arithm(solMains[i].getLessonVarSlotGlobal(sync.getLesson1()), "=", solverMain2.getLessonVarSlotGlobal(sync.getLesson2())).post();
				}
			}
		}
	}
	

	public static SolverMain getSolMainTaf(SolverMain[] solMains, TAF taf) {
		for (SolverMain solMain : solMains)
			if (solMain.getTaf().getId() == taf.getId())
				return solMain;
		return null;
	}
	
	public static Planning getPlanningTaf(Planning[] plannings, TAF taf) {
		for (Planning planning : plannings)
			if (planning.getCalendar().getTaf().getId() == taf.getId())
				return planning;
		return null;
	}
	
	public static Slot getSlotFromLessonAndFixedPlanning(Lesson lesson, List<ScheduledLesson> scheduledLessons, SolverMain solMain) {
		for (ScheduledLesson scheduledLesson : scheduledLessons)
			if (lessonEqualScheduledLesson(lesson, scheduledLesson))
				return getSlotFromFixedTime(scheduledLesson.getStart(), scheduledLesson.getEnd(), solMain);
		return null;
	}
	
	public static Slot getSlotFromFixedTime(LocalDateTime start, LocalDateTime end, SolverMain solMain) {
		Slot slotTime = new Slot(0, null, null, start, end);
		for (Slot slot : solMain.getSlotsOrderedWUD())
			if (slot.compareTo(slotTime) == 0)
				return slot;
		return null;
	}
	
	public static boolean lessonEqualScheduledLesson(Lesson lesson, ScheduledLesson scheduledLesson) {
		return lesson.getUe().getName().equals(scheduledLesson.getUE()) &&
				lesson.getName().equals(scheduledLesson.getTitle()) &&
				lesson.getDescription().equals(scheduledLesson.getDescription()) &&
				lesson.getLessonLecturers().size() == scheduledLesson.getLecturers().size() &&
				lesson.getLessonLecturers().stream().map(ll -> 
						scheduledLesson.getLecturers().stream().map(sl -> ll.getUser().getFullName().equals(sl)).reduce(false, Boolean::logicalOr)).reduce(true, Boolean::logicalAnd);
	}
	
	/**
	 * Set the preferences and return the objective variable
	 * @return The objective variable.
	 */
	private IntVar setPreferences() {
		ArrayList<IntVar> preferences = new ArrayList<IntVar>();
		if (getPlanning().isGlobalUnavailability())  preferences.add(setPreferencesGlobal().mul(getPlanning().getWeightGlobalUnavailability()).intVar());
		if (getPlanning().isLecturersUnavailability()) preferences.add(setPreferencesLecturers().mul(getPlanning().getWeightLecturersUnavailability()).intVar());
		if (getPlanning().isMiddayGrouping()) preferences.add(setPreferenceCenteredLessons().mul(getPlanning().getWeightMiddayGrouping()).intVar());
		if (getPlanning().isLessonGrouping()) preferences.add(setPreferenceRegroupLessonsByNbSlots().mul(getPlanning().getWeightLessonGrouping()).intVar());
		if (getPlanning().isMaxTimeWithoutLesson()) preferences.add(setPreferenceMaxBreakWithoutLessonUe().mul(getPlanning().getWeightMaxTimeWithoutLesson()).intVar()); //Maybe change the mul factor to have something proportionnal with the valMax (i.e. having a fixed cost when the break is the double than the prefered max because now the cost is of one for each unit of time)
		if (getPlanning().isLessonBalancing()) preferences.add(setPreferenceBalancedLesson().mul(getPlanning().getWeightLessonBalancing()).intVar());
		return (preferences.isEmpty()) ? null : getModel().sum("Preferences", preferences.stream().filter(v -> v != null).toArray(IntVar[]::new));
	}
	
	private IntVar setPreferencesGlobal() {
		Model model = getModel();
		List<Slot> slots = getSlotsOrderedWUD();
		List<BoolVar> penalties = new ArrayList<>();
		
		for (Slot slot : slots) {
			if (services.getGlobalUnavailabilityService().findBySlot(slot).filter(g -> !g.getStrict()).isPresent()) {
				BoolVar lessonWhenNotPrefered = model.boolVar("");
				model.reification(lessonWhenNotPrefered, model.arithm(this.getSlotVarLesson(slot), "!=", 0));
		        penalties.add(lessonWhenNotPrefered);
			}
		}
		IntVar[] penaltiesArray = penalties.toArray(new IntVar[penalties.size()]);
		
		IntVar totalNonPreferred = model.intVar("totalNonPreferred", 0, penaltiesArray.length);
		model.sum(penaltiesArray, "=", totalNonPreferred).post();
		return totalNonPreferred;
	}

	private IntVar setPreferencesLecturers() {
		Model model = getModel();
		ArrayList<IntVar> isNotPreferredVars = new ArrayList<IntVar>();
	    for (Lesson lesson : getLessons()) {
	    	for (Slot slot : services.getLessonService().findNotPreferedSlotsByLessonAndCalendar(lesson, getCalendar())) {
	    		BoolVar isNotPreferredVar = model.boolVar("NotPreferred_" + lesson.getId() + "_" + slot.getId());
	    		model.reification(isNotPreferredVar, model.arithm(getLessonVarSlot(lesson), "=", getIdMSlot(slot)));
	    		isNotPreferredVars.add(isNotPreferredVar);
	    	}
	    }
	    return model.sum("NotPreferredAllocations", isNotPreferredVars.stream().toArray(IntVar[]::new));
	}
	
	private IntVar setPreferenceCenteredLessons() {
		Model model = getModel();
		ArrayList<IntVar> penaltiesNotCentered = new ArrayList<IntVar>();
		List<Day> days = getDaysOrderedWU();
		int nbMaxSlotsDay = days.stream().mapToInt(l -> l.getSlots().size()).max().orElse(0);
		int nbUes = getUes().size();
		System.out.println(nbUes);
		FiniteAutomaton automaton = this.automatonPreferenceNoInterweaving(IntStream.range(1, nbUes + 1).toArray(), new int[] {0});
		System.out.println(automaton.run(new int[] {0,0,0,1}));
		System.out.println(automaton.run(new int[] {1,0,0,0}));
		int[][][] costsForward = new int[nbMaxSlotsDay][nbUes + 1][2];
		int[][][] costsBackward = new int[nbMaxSlotsDay][nbUes + 1][2];
		for (int i = 0; i < nbMaxSlotsDay; i ++)
			for (int j = 0; j < nbUes + 1; j ++)
				for (int k = 0; k < 2; k ++) {
					costsForward[i][j][k] = (k == 1 && j == 0) ? 1 : 0;
					costsBackward[i][j][k] = (k == 0 && j != 0) ? i : 0;
				}
		ICostAutomaton cAutoForward = CostAutomaton.makeSingleResource(automaton, costsForward, 0, nbMaxSlotsDay - 1);
		ICostAutomaton cAutoBackward = CostAutomaton.makeSingleResource(automaton, costsBackward, 0, nbMaxSlotsDay - 1);
		for (Day day : days) {
			IntVar[] vars = getSlotsByDayOrdered(day).stream().map(s -> getSlotVarUe(s)).toArray(IntVar[]::new);
			System.out.println(vars.length);
			if (vars.length > 0) {
				IntVar costForward = model.intVar("CostForwardCentered day " + getIdMDay(day), 0, vars.length - 1);
				IntVar costBackward = model.intVar("CostBackwardCentered day " + getIdMDay(day), 0, vars.length - 1);
				IntVar costDay = model.intVar("CostDayCentered day " + getIdMDay(day), 0, vars.length - 2);
				IntVar[] varsReversed = new IntVar[vars.length];
				for (int i = 0; i < vars.length; i ++) varsReversed[vars.length - 1 - i] = vars[i];
				model.costRegular(vars, costForward, cAutoForward).post();
				model.costRegular(varsReversed, costBackward, cAutoBackward).post();
				model.arithm(costDay, "=", costForward, "-", costBackward).post();
				penaltiesNotCentered.add(costDay);
			}
		}
		return model.sum("preferenceCenteredLesson", penaltiesNotCentered.stream().toArray(IntVar[]::new));
	}
	
	private IntVar setPreferenceCenteredLessons2() {
		Model model = getModel();
		ArrayList<IntVar> penaltyNotCentered = new ArrayList<IntVar>();
		for (Day day : getDaysOrderedWU()) {
			List<Slot> slots = getSlotsByDayOrdered(day);
			Integer idMCenteredSlot = getIdMSlot(getCenteredSlot(slots));
			for (Slot slot : slots) {
				Integer idMSlot = getIdMSlot(slot);
				if (idMSlot != idMCenteredSlot) {
					penaltyNotCentered.add(model.arithm(getSlotVarLesson(slot), "!=", 0).reify().mul(Math.abs(idMSlot - idMCenteredSlot)).intVar());
				}
			}
		}
		
		return model.sum("preferenceCenteredLesson", penaltyNotCentered.stream().toArray(IntVar[]::new));
	}
	

	private Slot getCenteredSlot(List<Slot> slots) {
		LocalTime centeredTime = LocalTime.of(12, 0);
		Slot centeredSlot = slots.getFirst();
		long deltaCenteredSlot = centeredSlot.getStart().toLocalTime().until(centeredTime, ChronoUnit.MINUTES);
		for (Slot slot : slots)
			if (slot.getStart().toLocalTime().until(centeredTime, ChronoUnit.MINUTES) < deltaCenteredSlot){
				deltaCenteredSlot = slot.getStart().toLocalTime().until(centeredTime, ChronoUnit.MINUTES);
				centeredSlot = slot;
			}
		return centeredSlot;
	}
	
	private IntVar setPreferenceBalancedLesson() {
		Model model = getModel();
		ArrayList<IntVar> penalties = new ArrayList<>();		
		
	    int totalCourses = getNumberOfLessons(); 
	    List<Day> days = getDaysOrderedWU();
	    int totalDays = days.size();
	    int averageCoursesPerDay = totalCourses/ totalDays; //return an int (eclidean division)
	    //IntVar averageCoursesPerDay = model.intVar("AverageCoursesPerDay", totalCourses / totalDays);
	    
	    for (Day day : days) {
	    	List<Slot> slots = getSlotsByDayOrdered(day);
	    	
	    	IntVar nbSlotEmpty = model.count("VarNbSlotEmpty-Day " + day.getId(), 0, getSlotVarLesson(slots.stream().toArray(Slot[]::new)));
	    	int nbTotalSlots = slots.size();
	    	IntVar nbSlotNotEmpty = nbSlotEmpty.sub(nbTotalSlots).neg().intVar();
	    	
	        penalties.add(model.abs(nbSlotNotEmpty.sub(averageCoursesPerDay).intVar()));	    	 
	    }
	    return model.sum("penaltiesBalancing",penalties.toArray(new IntVar[0]));
	}
	
	private IntVar setPreferenceRegroupLessonsByNbSlots() {
		Model model = getModel();
		List<Day> days = getDaysOrderedWU(); 
		List<List<Slot>> slotDays = days.stream().map(d -> getSlotsByDay(d)).toList();
		int nbMaxSlotsDay = slotDays.stream().mapToInt(l -> l.size()).max().orElse(0);
		if (nbMaxSlotsDay == 0) return null;
		List<IntVar> distancesFromPreferedValues = new ArrayList<IntVar>();
 		List<UE> ues = getUes();

		System.out.println(ues.size());
		System.out.println(ues.stream().map(u -> u.getId()));
		System.out.println(getPlanning().getConstraintsOfUEs().size());
		System.out.println(getPlanning().getConstraintsOfUEs().stream().map(cUe -> cUe.getUe().getId()));
 		int[][] costs = ues.stream().map(ue -> getCostsTblRegroupLessons(getConstraintsOfUe(ue).getLessonGroupingNbLessons(), nbMaxSlotsDay)).toArray(int[][]::new);
 		int iDay = 0;
		int[] idUes = getArrayInt(getIdMUe(ues.stream().toArray(UE[]::new)));
		for (List<Slot> slots : slotDays) {
			List<IntVar> cnts = new ArrayList<IntVar>();
 			for (UE ue : ues) {
 				cnts.add(model.intVar("Count Ue " + ue.getName() + "(" + getIdMUe(ue) + ") in Day " + getIdMDay(days.get(iDay)),
						0, slots.size()));
 				distancesFromPreferedValues.add(
						model.element("Distance Ue " + ue.getName() + "(" + getIdMUe(ue) + ") in Day " + getIdMDay(days.get(iDay)),
									costs[getIdMUe(ue) - 1], cnts.getLast(), 0));
 			}
 			model.globalCardinality(getSlotVarUe(slots.stream().toArray(Slot[]::new)), idUes, cnts.stream().toArray(IntVar[]::new), false).post();
 			iDay ++;
 		}
 		IntVar preferenceNoInterweaving = setPreferenceNoInterweaving(nbMaxSlotsDay).mul(2).intVar();
 		IntVar preferenceNbLessons = model.sum("preferenceNbLessons", distancesFromPreferedValues.stream().toArray(IntVar[]::new));
		return model.sum("preferenceRegroupLessons", preferenceNoInterweaving, preferenceNbLessons);
	}
	
	private int[] getCostsTblRegroupLessons(int[] preferedVals, int nbMaxSlotsDay) {
		int[] costs = new int[nbMaxSlotsDay];
		costs[0] = 0;
		int iPreferedVal = 0;
		for (int i = 1; i < nbMaxSlotsDay; i ++) {
			int dist1 = Math.abs(preferedVals[iPreferedVal] - i);
			int dist2 = nbMaxSlotsDay;
			if (iPreferedVal + 1 < preferedVals.length) dist2 = Math.abs(preferedVals[iPreferedVal + 1] - i);
			if (dist2 < dist1) iPreferedVal ++;
			costs[i] = Math.abs(preferedVals[iPreferedVal] - i);
		}
		return costs;
	}
	
	private IntVar setPreferenceNoInterweaving() {
		return setPreferenceNoInterweaving(getDaysOrderedWU().stream().mapToInt(l -> l.getSlots().size()).max().orElse(0));
	}
	
	private IntVar setPreferenceNoInterweaving(int nbMaxSlotsDay) {
		Model model = getModel();
		if (nbMaxSlotsDay == 0) return null;
		List<Day> days = getDaysOrderedWU(); 
		List<UE> ues = getUes();
		int nbUes = ues.size();
		List<IntVar> costsDays = new ArrayList<IntVar>();
		for (UE ue : ues) {
			int idMUe = getIdMUe(ue);
			FiniteAutomaton automaton = automatonPreferenceNoInterweaving(ue, nbMaxSlotsDay, nbUes);
			int[][][] costsForward = new int[nbMaxSlotsDay][nbUes + 1][2];
			int[][][] costsBackward = new int[nbMaxSlotsDay][nbUes + 1][2];
			for (int i = 0; i < nbMaxSlotsDay; i ++)
				for (int j = 0; j < nbUes + 1; j ++)
					for (int k = 0; k < 2; k ++) {
						costsForward[i][j][k] = (j != idMUe && k == 1) ? 1 : 0;
						costsBackward[i][j][k] = (j == idMUe && k == 0) ? i : 0;
					}
			ICostAutomaton cAutoForward = CostAutomaton.makeSingleResource(automaton, costsForward, 0, nbMaxSlotsDay - 1);
			ICostAutomaton cAutoBackward = CostAutomaton.makeSingleResource(automaton, costsBackward, 0, nbMaxSlotsDay - 1);
			for (Day day : days) {
				IntVar costForward = model.intVar("CostForward " + ue.getName() + " day " + getIdMDay(day), 0, nbMaxSlotsDay - 1);
				IntVar costBackward = model.intVar("CostBackward " + ue.getName() + " day " + getIdMDay(day), 0, nbMaxSlotsDay - 1);
				IntVar costDay = model.intVar("CostDay " + ue.getName() + " day " + getIdMDay(day), 0, nbMaxSlotsDay - 2);
				IntVar[] vars = getSlotsByDayOrdered(day).stream().map(s -> getSlotVarUe(s)).toArray(IntVar[]::new);
				IntVar[] varsReversed = new IntVar[vars.length];
				for (int i = 0; i < vars.length; i ++) varsReversed[vars.length - 1 - i] = vars[i];
				model.costRegular(vars, costForward, cAutoForward).post();
				model.costRegular(varsReversed, costBackward, cAutoBackward).post();
				model.arithm(costDay, "=", costForward, "-", costBackward).post();
				costsDays.add(costDay);
			}
		}
		return model.sum("preferenceNoInterweaving", costsDays.stream().toArray(IntVar[]::new));
	}
	
	private FiniteAutomaton automatonPreferenceNoInterweaving(UE ue, int nbMaxSlotsDay, int nbUes) {
		int idMUe = getIdMUe(ue);
		return automatonPreferenceNoInterweaving(new int[] {idMUe}, IntStream.range(0, nbUes + 1).filter(val -> val != idMUe).toArray());
	}
	
	private FiniteAutomaton automatonPreferenceNoInterweaving(int[] symbolsLesson, int[] otherSymbols) {
		FiniteAutomaton automaton = new FiniteAutomaton();
		int[] alphabet = ArrayUtils.concat(symbolsLesson, otherSymbols);
		int state0 = automaton.addState();
		int state1 = automaton.addState();
		automaton.setInitialState(state0);
		automaton.setFinal(state0, state1);
		automaton.addTransition(state0, state0, otherSymbols);
		automaton.addTransition(state0, state1, symbolsLesson);
		automaton.addTransition(state1, state1, alphabet);
		return automaton;
	}
	
	private IntVar setPreferenceMaxBreakWithoutLessonUe() {
		Model model = getModel();
		List<IntVar> penalties = new ArrayList<IntVar>();
		HashMap<Integer, List<IntVar>> sortedLessonsVar = setConstraintSortedLessonsUeVarDayOrWeek();
		IntVar zero = model.intVar("Zero", 0);
		for (UE ue : getUes()) {
			ConstraintsOfUE cUe = getConstraintsOfUe(ue);
			if (cUe.isMaxTimeWithoutLesson()) {
				int valMax = cUe.getMaxTimeWLDuration() + 1;
				List<IntVar> sorteds = sortedLessonsVar.get(getIdMUe(ue));
				for (int i = 0; i < sorteds.size() - 1; i ++) {
					penalties.add(model.max("MaxBreak " + ue.getName() + " (" + i + ")",
											new IntVar[] {zero,
														sorteds.get(i + 1).add(sorteds.get(i).add(valMax).neg()).intVar()}));
				}
			}
		}
		return model.sum("PreferenceMaxBreak", penalties.stream().toArray(IntVar[]::new));
	}
	
	/**
	 * Set the strategy of the solver (Only when there is only one planning to generate (no sync with generation))
	 * @param solMain The SolverMain object corresponding to the planning to generate.
	 * @param solver The solver of the model.
	 */
	private static void setStrategy(SolverMain solMain) {
		Model model = solMain.getModel();
		Solver solver = model.getSolver();
		Solution solution = solver.defaultSolution();
		IntVar[] decisionVars = solMain.getDecisionVars(); // Total time with proof of optimality (Time to find optimal solution) on the planning planningTestEfficiency1() (in ServerApplication).
		//solver.setSearch(Search.minDomLBSearch(decisionVars)); // 496 s (158)
		//solver.setSearch(Search.minDomUBSearch(decisionVars)); // 768 s (347)
		//solver.setSearch(Search.lastConflict(Search.intVarSearch(new FirstFail(model),new IntDomainLast(solution, new IntDomainMax(), null),decisionVars), 1 )); //1306 s (631)
		//solver.setSearch(Search.lastConflict(Search.intVarSearch(new FirstFail(model),new IntDomainLast(solution, new IntDomainMin(), null),decisionVars), 1 )); //912 s (45)
		//solver.setSearch(Search.lastConflict(Search.intVarSearch(new FirstFail(model),new IntDomainMin(),decisionVars), 1 )); //638 s (36)
		//solver.setSearch(Search.lastConflict(Search.intVarSearch(new FirstFail(model),new IntDomainMax(),decisionVars), 1 )); //1992 s (166)
		
		solver.setGeometricalRestart(decisionVars.length * 30L, 1.1d, new FailCounter(model, 0), 1000);
		solver.setNoGoodRecordingFromRestarts();
		//solver.setNoGoodRecordingFromSolutions(decisionVars);
		solver.showRestarts();
		solver.setSearch(Search.activityBasedSearch(decisionVars)); // 1128 s (9)
		//solver.setSearch(Search.conflictHistorySearch(decisionVars)); // 1227 s (244)
		//solver.setSearch(Search.intVarSearch(new ConflictHistorySearch<>(decisionVars, 0),new IntDomainMax(), decisionVars)); // 2443 s (1399)
		//solver.setSearch(Search.intVarSearch(new ConflictHistorySearch<>(decisionVars, 0),new IntDomainLast(solution, new IntDomainMin(), null), decisionVars)); // 1303 s (233)
		//solver.setSearch(Search.domOverWDegSearch(decisionVars)); // 1228 s (251)
		//solver.setSearch(Search.intVarSearch(new DomOverWDeg<>(decisionVars, 0),new IntDomainMax(), decisionVars)); // 2422 s (1375)
		//solver.setSearch(Search.intVarSearch(new DomOverWDeg<>(decisionVars, 0),new IntDomainLast(solution, new IntDomainMin(), null), decisionVars)); // 1033 s (229)
		
		//solver.setGeometricalRestart(decisionVars.length * 10L, 1.2d, new FailCounter(model, 0), 500);
		//solver.setNoGoodRecordingFromRestarts();
		//solver.setNoGoodRecordingFromSolutions(decisionVars);
		//solver.showRestarts();
		//solver.setSearch(Search.activityBasedSearch(decisionVars)); // 2626 s (13)
		//solver.setSearch(Search.conflictHistorySearch(decisionVars)); //
		//solver.setSearch(Search.intVarSearch(new ConflictHistorySearch<>(decisionVars, 0),new IntDomainMax(), decisionVars)); //
		//solver.setSearch(Search.intVarSearch(new ConflictHistorySearch<>(decisionVars, 0),new IntDomainLast(solution, new IntDomainMin(), null), decisionVars)); //
		//solver.setSearch(Search.domOverWDegSearch(decisionVars)); //
		//solver.setSearch(Search.intVarSearch(new DomOverWDeg<>(decisionVars, 0),new IntDomainMax(), decisionVars)); //
		//solver.setSearch(Search.intVarSearch(new DomOverWDeg<>(decisionVars, 0),new IntDomainLast(solution, new IntDomainMin(), null), decisionVars)); //
		
		//solver.setGeometricalRestart(decisionVars.length * 1L, 1.05d, new FailCounter(model, 0), 500);// 2941 s (8)
		//solver.setGeometricalRestart(decisionVars.length * 1L, 1.2d, new FailCounter(model, 0), 500);// 2196 s (20)
		//solver.setGeometricalRestart(decisionVars.length * 10L, 1.05d, new FailCounter(model, 0), 1000);// 3284 s (18)
		//solver.setNoGoodRecordingFromRestarts();
		//solver.setNoGoodRecordingFromSolutions(decisionVars);
		//solver.showRestarts();
		//solver.setSearch(Search.activityBasedSearch(decisionVars)); // 3284 (18)
		//solver.setSearch(Search.conflictHistorySearch(decisionVars)); //
		//solver.setSearch(Search.intVarSearch(new ConflictHistorySearch<>(decisionVars, 0),new IntDomainMax(), decisionVars)); //
		//solver.setSearch(Search.domOverWDegSearch(decisionVars)); //
		//solver.setSearch(Search.intVarSearch(new DomOverWDeg<>(decisionVars, 0),new IntDomainMax(), decisionVars)); //
		
	}
	
	/*public Object[][] strategies() {
    return new Object[][]{
            {(Function<IntVar[], AbstractStrategy<IntVar>>) vars -> new ImpactBased(vars, 2, 3, 10, 0, true)},
            {(Function<IntVar[], AbstractStrategy<IntVar>>) Search::activityBasedSearch},
            {(Function<IntVar[], AbstractStrategy<IntVar>>) Search::domOverWDegSearch},
            {(Function<IntVar[], AbstractStrategy<IntVar>>) Search::conflictHistorySearch},
            {(Function<IntVar[], AbstractStrategy<IntVar>>) Search::domOverWDegRefSearch},
            {(Function<IntVar[], AbstractStrategy<IntVar>>) Search::failureRateBasedSearch},
            {(Function<IntVar[], AbstractStrategy<IntVar>>) Search::failureLengthBasedSearch},
            {(Function<IntVar[], AbstractStrategy<IntVar>>) Search::pickOnDom},
            {(Function<IntVar[], AbstractStrategy<IntVar>>) Search::pickOnFil},
            {(Function<IntVar[], AbstractStrategy<IntVar>>) Search::roundRobinSearch}};
	}
	
	@Test(groups = "10s", timeOut = 60000, dataProvider = "strategies")
	public void testCostas(Function<IntVar[], AbstractStrategy<IntVar>> strat) {
	Model model = ProblemMaker.makeCostasArrays(6);
	IntVar[] vars = model.retrieveIntVars(true);
	Solver solver = model.getSolver();
	solver.setSearch(strat.apply(vars));
	solver.setGeometricalRestart(vars.length * 3L, 1.1d, new FailCounter(model, 0), 1000);
	solver.setNoGoodRecordingFromRestarts();
	model.getSolver().showRestarts();
	solver.findAllSolutions();
	solver.printShortStatistics();
	Assert.assertEquals(solver.getSolutionCount(), 58);
	}*/
	
	/**
	 * Set the strategy of the solver (For multiple planning to generate due to sync constraint)
	 * @param solMains The SolverMain objects related to the plannings to generate.
	 * @param solver The solver of the model.
	 */
	private static void setStrategy(SolverMain[] solMains, Model model) {
		// 2 semaines, mardi mercredi, préférence globale pas premier, dernier et milieu.
		// 2 ues, [2,2,1,1,1], [3,1,1,2]
		// 45 obj.
		Solver solver = model.getSolver();
		Solution solution = solver.defaultSolution();
		IntVar[] decisionVars = ArrayUtils.flatten(IntStream.range(0, solMains.length).
									mapToObj(i -> solMains[i].getDecisionVars()).toArray(IntVar[][]::new));  
		//solver.setSearch(Search.minDomLBSearch(decisionVars));
		//solver.setSearch(Search.minDomUBSearch(decisionVars));
		solver.setGeometricalRestart(decisionVars.length * 30L, 1.1d, new FailCounter(model, 0), 1000);
		solver.setNoGoodRecordingFromRestarts();
		//solver.setNoGoodRecordingFromSolutions(decisionVars);
		solver.showRestarts();
		solver.setSearch(Search.activityBasedSearch(decisionVars));
		
	}
	
	private IntVar[] getDecisionVars() {
		return getVarDecisionSlots();
		//return ArrayUtils.concat(getVarDecisionSlots(), getVarDecisionLessons());
	}
	
	private IntVar[] getVarDecisionSlots() {
		List<IntVar> vars = new ArrayList<IntVar>();
		vars.addAll(getSlotsOrderedWUD().stream().map(s -> getSlotVarLesson(s)).toList());
		IntVar[] varsArr = convertListToArrayIntVar(vars);
		return varsArr;
	}
	
	private IntVar[] getVarDecisionLessons() {
		List<IntVar> vars = new ArrayList<IntVar>();
		vars.addAll(getLessons().stream().map(l -> getLessonVarSlot(l)).toList());
		IntVar[] varsArr = convertListToArrayIntVar(vars);
		return varsArr;
	}
	
	private IntVar[] convertListToArrayIntVar(List<IntVar> list){
		IntVar[] arr = new IntVar[list.size()];
		for (int i = 0; i < list.size(); i ++) arr[i] = list.get(i);
		return arr;
	}
	
	private IntVar[] getVarDecisionSlotsMiddle() {
		List<IntVar> vars = new ArrayList<IntVar>();
		for (Day day : getDaysOrderedWU()) {
			List<Slot> slots = getSlotsByDayOrdered(day);
			for (int i = 0; i < slots.size(); i ++) {
				if (i <= slots.size() / 2) {
					vars.add(vars.size() - i, getSlotVarLesson(slots.get(i)));
				}
				else {
					vars.add(vars.size() - slots.size() + i + 1, getSlotVarLesson(slots.get(i)));
				}
			}
		}
		return convertListToArrayIntVar(vars);
	}
	
	private int[] getArrayInt(Integer[] tbl) {
		int[] tblRes = new int[tbl.length];
		for (int i = 0; i < tbl.length; i ++)
			tblRes[i] = tbl[i].intValue();
		return tblRes;
	}
	
	private String nameSlot(Slot slot) {
		return "Slot " + idMSlot.getValue(slot.getId()) + " (" + slot.getId() + ")";
	}
	
	private String nameLesson(Lesson lesson) {
		return "Lesson " + idMLesson.getValue(lesson.getId()) + " (" + lesson.getId() + ")";
	}
	
	private List<Result> makeSolution(Solution solution) {
		List<Result> results = new ArrayList<Result>();
		for (Slot s : getSlotsOrderedWUD())
			if (solution.getIntVal(getSlotVarLesson(s)) != 0)
				results.add(new Result(s.getId(), getIdLesson(solution.getIntVal(getSlotVarLesson(s)))));
		return results;
	}
	
	private String makeSolutionString(Solution solution) {
		StringBuilder res = new StringBuilder();
		res.append("{\"slots\":[");
		getSlotsOrderedWUD().stream().forEach(s -> res.append("{\"id\":" + s.getId() + (solution.getIntVal(getSlotVarLesson(s)) != 0 ? ",\"lessonId\":" + this.getIdLesson(solution.getIntVal(getSlotVarLesson(s))) :  "") + "},"));
		res.deleteCharAt(res.length()-1);
		res.append("]}");
		return res.toString();
	}
	
	private String showSolutionsDebug(Solution solution) {
		StringBuilder res = new StringBuilder();
		res.append(getTaf().getName() + "\r\n");
		res.append("Slots : [");
		getSlotsOrderedWUD().forEach(s -> res.append("{id : " + s.getId() + 
												(IdMSlotGlobal != null ? ", idGlob : " + getIdMSlotGlobal(s) : "") +
												(solution.getIntVal(getSlotVarLesson(s)) != 0 ? ", lessonId : " + this.getIdLesson(solution.getIntVal(getSlotVarLesson(s))) :  "") +
												(isUesNeeded(getPlanning()) ? ", UeId : " + this.getIdUe(solution.getIntVal(getSlotVarUe(s))) :  "") +
												"},"));
		res.deleteCharAt(res.length() - 1);
		res.append("]\r\n");
		res.append("Lessons : [");
		getLessons().forEach(l -> res.append("{id : " + l.getId() +
												", slotId : " + this.getIdSlot(solution.getIntVal(getLessonVarSlot(l))) +
												(IdMSlotGlobal != null ? ", idGlobVar : " + solution.getIntVal(getLessonVarSlotGlobal(l)) : "") +
												(isDaysNeeded(getPlanning()) ? ", idDayVar : " + solution.getIntVal(getLessonVarDay(l)) : "") +
												(isWeeksNeeded(getPlanning()) ? ", idWeekVar : " + solution.getIntVal(getLessonVarWeek(l)) : "") +
												"},"));
		res.deleteCharAt(res.length() - 1);
		res.append("]");
		res.append("]\r\n");
		
		return res.toString();
	}	
}
