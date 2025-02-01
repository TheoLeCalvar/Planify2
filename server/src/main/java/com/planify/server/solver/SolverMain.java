package com.planify.server.solver;

import java.lang.reflect.InaccessibleObjectException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.nary.automata.FA.CostAutomaton;
import org.chocosolver.solver.constraints.nary.automata.FA.FiniteAutomaton;
import org.chocosolver.solver.constraints.nary.automata.FA.ICostAutomaton;
import org.chocosolver.solver.constraints.nary.automata.FA.utils.Counter;
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression;
import org.chocosolver.solver.search.loop.monitors.SolvingStatisticsFlow;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import com.planify.server.models.Antecedence;
import com.planify.server.models.Calendar;
import com.planify.server.models.Day;
import com.planify.server.models.Lesson;
import com.planify.server.models.LessonLecturer;
import com.planify.server.models.Planning;
import com.planify.server.models.Result;
import com.planify.server.models.Sequencing;
import com.planify.server.models.Slot;
import com.planify.server.models.Synchronization;
import com.planify.server.models.TAF;
import com.planify.server.models.UE;
import com.planify.server.models.User;
import com.planify.server.models.Week;


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
	private List<IntVar> countsDebug;
	
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
		countsDebug = new ArrayList<IntVar>();
	}
	
	private Planning getPlanning() {return this.planning;};
	
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
	
	public static void setServices(SolverServices services) {
		SolverMain.services = services;
	}
	
	public static List<Result> generatePlanning(Planning planning) {
		Model model = new Model();
		Solver solver = model.getSolver();
		SolverMain solMain = new SolverMain(planning);
		int nbSlots = solMain.getNumberOfSlots();
		int nbLessons = solMain.getNumberOfLessons();
		solMain.initialiseVars(model, nbSlots, nbLessons, true, true, true);
		solMain.setConstraints(model);
		IntVar obj = solMain.setPreferences(model);
		setStrategy(solMain, solver, planning);
		Solution solution;
		if (obj != null) solution = solver.findOptimalSolution(obj, false);
		else solution = solver.findSolution();
		//System.out.println(model);
		solver.printShortStatistics();
		if (!solution.exists())
			return new ArrayList<Result>();
		System.out.println(solMain.showSolutionsDebug(solution));
		System.out.println(solMain.makeSolutionString(solution));
		List<Result> results = solMain.makeSolution(solution);
		services.getPlanningService().addScheduledLessons(planning, results);
		return results;
	}
	
	public static String generatePlanningString(Planning planning) {
		Model model = new Model();
		Solver solver = model.getSolver();
		SolverMain solMain = new SolverMain(planning);
		int nbSlots = solMain.getNumberOfSlots();
		int nbLessons = solMain.getNumberOfLessons();
		solMain.initialiseVars(model, nbSlots, nbLessons, true, true, true);
		solMain.setConstraints(model);
		IntVar obj = solMain.setPreferences(model);
		setStrategy(solMain, solver, planning);
		Solution solution;
		System.out.println("Start Solving !");
		//solver.verboseSolving(1000);
		solver.showSolutions();
		//solver.showDecisions();
		if (obj != null) solution = solver.findOptimalSolution(obj, false);
		else solution = solver.findSolution();
		//solution = solver.findSolution();
		System.out.println(Arrays.deepToString(model.getVars()));
		solver.printShortStatistics();
		if (!solution.exists())
			return "";
		System.out.println(solMain.showSolutionsDebug(solution));
		System.out.println(solMain.makeSolutionString(solution));
		return solMain.makeSolutionString(solution);
	}
	
	public static void generatePlannings(Planning[] plannings) {
		System.out.println("Yo");
		Model model = new Model();
		Solver solver = model.getSolver();
		IntVar[] objs = new IntVar[plannings.length];
		SolverMain[] solMains = new SolverMain[plannings.length];
		System.out.println("Yo1");
		HashMap<Long, Integer> idToIdMGlobal = getIdToIdMGlobalCals(plannings);
		System.out.println("Yo2");
		for (int i = 0; i < plannings.length; i ++) {
			System.out.println("Ya" + i);
			SolverMain solMain = new SolverMain(plannings[i]);
			solMains[i] = solMain;
			int nbSlots = solMain.getNumberOfSlots();
			int nbLessons = solMain.getNumberOfLessons();
			solMain.initialiseVars(model, nbSlots, nbLessons, idToIdMGlobal);
			solMain.setConstraints(model);
			IntVar obj = solMain.setPreferences(model);
			objs[i] = obj != null ? obj : model.intVar(0);
		}
		setSynchronisationConstraints(model, solMains);
		IntVar globObj = model.sum("globObj", objs);
		setStrategy(solMains, solver, plannings);
		Solution solution = solver.findOptimalSolution(globObj, false);
		System.out.println(model);
		solver.printShortStatistics();
		if (!solution.exists())
			return;
		for (int i = 0; i < plannings.length; i ++)
			System.out.println(solMains[i].showSolutionsDebug(solution));
	}
	
	private void initialiseVars(Model model, int nbSlots, int nbLessons, HashMap<Long, Integer> idToIdMGlobal) {
		initialiseVars(model, nbSlots, nbLessons, true, true, true);
		initialiseSync(model, idToIdMGlobal);
	}
	
	private void initialiseSync(Model model, HashMap<Long, Integer> idToIdMGlobal) {
		this.IdMSlotGlobal = idToIdMGlobal;
		int[] valGlobs = getValsSlotGlobal(services.getCalendarService().getSlotsOrdered(planning.getCalendar().getId()).toArray(Slot[]::new));
		for (UE ue : planning.getCalendar().getTaf().getUes())
			for (Lesson lesson : ue.getLessons())
					lessonVarSlotGlobal.put(lesson.getId(), model.intVar(nameLesson(lesson) + "-VarSlotGlobal", valGlobs));
	}
	
	private int[] getValsSlotGlobal(Slot[] slots) {
		ArrayList<Integer> vals = new ArrayList<Integer>();
		for (Slot slot : slots)
			vals.add(getIdMSlotGlobal(slot));
		return vals.stream().mapToInt(i -> (int) i).toArray();
	}

	private void initialiseVars(Model model, int nbSlots, int nbLessons, boolean varUe, boolean varDay, boolean varWeek) {
		initialiseWeeks();
		initialiseDays();
		initialiseSlots(model, nbLessons, varUe, planning.getCalendar().getTaf().getUes().size());
		initialiseUes();
		initialiseLessons(model, nbSlots, varDay, services.getCalendarService().getDaysSorted(planning.getCalendar().getId()).size(), varWeek, services.getCalendarService().getWeeksSorted(planning.getCalendar().getId()).size());
	}
	
	private void initialiseWeeks() {
		Integer idMW = 1;
		for (Week week : services.getCalendarService().getWeeksSorted(planning.getCalendar().getId())) {
			idMWeek.put(week.getId(), idMW);
			idMW ++;
		}
	}
	
	private void initialiseDays() {
		Integer idMD = 1;
		for (Day day : services.getCalendarService().getDaysSorted(planning.getCalendar().getId())) {
			idMDay.put(day.getId(), idMD);
			idMD ++;
		}
	}
	
	private void initialiseSlots(Model model, int nbLessons, boolean varUe, int nbUes) {
		Integer idMS = 1;
		for (Slot slot : services.getCalendarService().getSlotsOrdered(planning.getCalendar().getId())) {
			idMSlot.put(slot.getId(), idMS);
			slotVarLesson.put(slot.getId(), model.intVar(nameSlot(slot) + "-VarLesson", 0, nbLessons));
			if (varUe) slotVarUe.put(slot.getId(), model.intVar(nameSlot(slot) + "-VarUe", 0, nbUes));
			idMS ++;
		}
	}
	
	private void initialiseUes() {
		Integer idMU = 1;
		for (UE ue : planning.getCalendar().getTaf().getUes()) {
			idMUe.put(ue.getId(), idMU);
			idMU ++;
		}
	}
	
	private void initialiseLessons(Model model, int nbSlots, boolean varDay, int nbDays, boolean varWeek, int nbWeeks) {
		Integer idML = 1;
		for (UE ue : planning.getCalendar().getTaf().getUes())
			for (Lesson lesson : ue.getLessons()) {
				idMLesson.put(lesson.getId(), idML);
				lessonVarSlot.put(lesson.getId(), model.intVar(nameLesson(lesson) + "-VarSlot", 1, nbSlots));
				if (varDay) lessonVarDay.put(lesson.getId(), model.intVar(nameLesson(lesson) + "-VarDay", 1, nbDays));
				if (varWeek) lessonVarWeek.put(lesson.getId(), model.intVar(nameLesson(lesson) + "-VarWeek", 1, nbWeeks));
				idML ++;
			}
		System.out.println("Lesson Var Day !!!!!!!!");
		System.out.println(varDay);
		System.out.println(lessonVarDay);
	}
	
	private int getNumberOfSlots() {
		return services.getCalendarService().getNumberOfSlots(planning.getCalendar().getId());
	}
	
	private int getNumberOfLessons() {
		return services.getTafService().numberOfLessons(planning.getCalendar().getTaf().getId());
	}
	
	private static HashMap<Long, Integer> getIdToIdMGlobalCals(Planning[] plannings){
		HashMap<Long, Integer> idToIdMGlob = new HashMap<Long, Integer>();
		List<List<Slot>> slots = IntStream.range(0, plannings.length).mapToObj(i -> services.getCalendarService().getSlotsOrdered(plannings[i].getCalendar().getId())).toList();
		int[] iSlots = new int[plannings.length];
		int[] lengthSlots = slots.stream().mapToInt(s -> s.size()).toArray();
		Integer idMGlob = 1;
		System.out.println(Arrays.toString(lengthSlots));
		while (testiLengths(iSlots, lengthSlots)) {
			System.out.println(Arrays.toString(iSlots));
			List<Integer> iMins = new ArrayList<Integer>();
			for (int i = 0; i < plannings.length; i ++) {
				System.out.println(i);
				if (iSlots[i] < lengthSlots[i]) {
					if (iMins.size() == 0) {
						System.out.println("Nothing in : " + iSlots[i]);
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
							System.out.println("Lower " + iSlots[i]);
							iMins.clear();
							iMins.add(i);
						}
						if (comparison == 0) {
							System.out.println("Equal " + iSlots[i]);
							iMins.add(i);
						}
					}
				}
			}
			System.out.println("a");
			for (int i : iMins) {
				idToIdMGlob.put(slots.get(i).get(iSlots[i]).getId(), idMGlob);
				iSlots[i] ++;
			}
			System.out.println("b");
			idMGlob ++;
		}
		return idToIdMGlob;
	}
	
	private static boolean testiLengths(int[] is, int[] lengths) {
		for (int i = 0; i < is.length; i ++)
			if (is[i] < lengths[i])
				return true;
		return false;
	}

	private void setConstraints(Model model) {
		//model.allDifferent(lessonVarSlot.values().toArray(IntVar[]::new)).post();
		//model.allDifferentExcept0(slotVarLesson.values().toArray(IntVar[]::new)).post();
		setConstraintLinkLessonsSlots(model, true);
		setConstraintLinkSlotGlobalDayWeek(model, this.IdMSlotGlobal != null, true, true);
		setConstraintSequences(model);
		setConstraintAntecedences(model);
		/*if (planning.hasConstraintGlobalUnavailability()*/ setConstraintGlobalUnavailability(model);
		/*if (planning.hasConstraint1())*/ setConstraintLecturerUnavailability(model);
		/*if (planning.hasConstraint())*/ setConstraintLunchBreak(model);
		/*if (planning.hasConstraint())*/ setConstraintNoInterweaving(model);
		/*if (planning.hasConstraint()*/ setConstraintMinMaxLessonUeInWeek(model); //Idée pour essayer d'améliorer les performances si besoin : essayer de faire une stratégie de recherche sur les variables du nombre de cours de l'UE considéré.
	}
	
	private void setConstraintLinkLessonsSlots(Model model, boolean ue) {
		Slot[] slots = services.getCalendarService().getSlotsOrdered(planning.getCalendar().getId()).stream().toArray(Slot[]::new);
		Lesson[] lessons = planning.getCalendar().getTaf().getUes().stream().flatMap(u -> u.getLessons().stream()).toArray(Lesson[]::new);
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
	
	private void setConstraintLinkSlotGlobalDayWeek(Model model, boolean global, boolean day, boolean week) {
		if (!(global || day || week)) return;
		List<Slot> slots = services.getCalendarService().getSlotsOrdered(planning.getCalendar().getId());
		int[] globInt = new int[] {};
		if (global) globInt = slots.stream().mapToInt(s -> getIdMSlotGlobal(s)).toArray();
		int[] dayInt = new int[] {};
		if (day) dayInt = slots.stream().mapToInt(s -> getIdMDay(s.getDay())).toArray();
		int[] weekInt = new int[] {};
		if (week) weekInt = slots.stream().mapToInt(s -> getIdMWeek(s.getDay().getWeek())).toArray();
		for (Lesson lesson : services.getTafService().getLessonsOfTAF(planning.getCalendar().getTaf().getId())) {
			if (global) model.element(getLessonVarSlotGlobal(lesson), globInt, getLessonVarSlot(lesson),1).post();
			if (day) model.element(getLessonVarDay(lesson), dayInt, getLessonVarSlot(lesson),1).post();
			if (week) model.element(getLessonVarWeek(lesson), weekInt, getLessonVarSlot(lesson),1).post();
			if (week) System.out.println(getLessonVarWeek(lesson));
		}
	}
	
	private void setConstraintLinkSlotGlobalDayWeek2(Model model, boolean global, boolean day, boolean week) {
		if (!(global || day || week)) return;
		Slot[] slots = services.getCalendarService().getSlotsOrdered(planning.getCalendar().getId()).stream().toArray(Slot[]::new);
		Lesson[] lessons = planning.getCalendar().getTaf().getUes().stream().flatMap(u -> u.getLessons().stream()).toArray(Lesson[]::new);
		int nbSlots = slots.length;
		int nbLessons = lessons.length;
		List<List<IntVar>> lessonsV = new ArrayList<List<IntVar>>();
		List<List<IntVar>> sortedLessonsV = new ArrayList<List<IntVar>>();
		for (int i = 1; i <= nbLessons; i ++) {
			List<IntVar> lV = new ArrayList<IntVar>();
			lV.add(getLessonVarSlot(lessons[i - 1]));
			lessonsV.add(lV);
			List<IntVar> sLV = new ArrayList<IntVar>();
			sLV.add(model.intVar(i));
			sortedLessonsV.add(sLV);
		}
		for (int i = nbLessons + 1; i <= nbSlots; i ++) {
			List<IntVar> lV = new ArrayList<IntVar>();
			lV.add(model.intVar("Dummy VarSlot", 1, nbSlots));
			lessonsV.add(lV);
			List<IntVar> sLV = new ArrayList<IntVar>();
			sLV.add(model.intVar(i));
			sortedLessonsV.add(sLV);
		}
		
		if (global) addGlobalListsConstraintLink(model, lessons, slots, lessonsV, sortedLessonsV);
		if (day) addDayListsConstraintLink(model, lessons, slots, lessonsV, sortedLessonsV);
		if (week) addWeekListsConstraintLink(model, lessons, slots, lessonsV, sortedLessonsV);
		
		IntVar[] permutations = model.intVarArray("Perm Link SlotGlobDayWeek", nbSlots, 1, nbSlots);

		System.out.println("lessonsV " + lessonsV);
		System.out.println("sortedLessonsV " + sortedLessonsV);
		
		model.keySort(lessonsV.stream().map(lV -> lV.toArray(IntVar[]::new)).toArray(IntVar[][]::new),
						permutations,
						sortedLessonsV.stream().map(sLV -> sLV.toArray(IntVar[]::new)).toArray(IntVar[][]::new),
						1).post();
	}
	
	private void addGlobalListsConstraintLink(Model model, Lesson[] lessons, Slot[] slots, List<List<IntVar>> lessonsV, List<List<IntVar>> sortedLessonsV) {
		for (int i = 0; i < lessons.length; i ++) {
			lessonsV.get(i).add(getLessonVarSlotGlobal(lessons[i]));
			sortedLessonsV.get(i).add(model.intVar(getIdMSlotGlobal(slots[i])));
		}
		int[] valsGlobs = getValsSlotGlobal(slots);
		for (int i = lessons.length; i < slots.length; i ++) {
			lessonsV.get(i).add(model.intVar("Dummy VarSlotGlob " + i,valsGlobs));
			sortedLessonsV.get(i).add(model.intVar(getIdMSlotGlobal(slots[i])));
		}
	}
	
	private void addDayListsConstraintLink(Model model, Lesson[] lessons, Slot[] slots, List<List<IntVar>> lessonsV, List<List<IntVar>> sortedLessonsV) {
		for (int i = 0; i < lessons.length; i ++) {
			lessonsV.get(i).add(getLessonVarDay(lessons[i]));
			sortedLessonsV.get(i).add(model.intVar(getIdMDay(slots[i].getDay())));
		}
		int[] valsDays = getArrayInt(getIdMDay(services.getCalendarService().getDaysSorted(planning.getCalendar().getId()).toArray(Day[]::new)));
		for (int i = lessons.length; i < slots.length; i ++) {
			lessonsV.get(i).add(model.intVar("Dummy VarDay",valsDays));
			sortedLessonsV.get(i).add(model.intVar(getIdMDay(slots[i].getDay())));
		}
	}
	
	private void addWeekListsConstraintLink(Model model, Lesson[] lessons, Slot[] slots, List<List<IntVar>> lessonsV, List<List<IntVar>> sortedLessonsV) {
		for (int i = 0; i < lessons.length; i ++) {
			lessonsV.get(i).add(getLessonVarWeek(lessons[i]));
			sortedLessonsV.get(i).add(model.intVar(getIdMWeek(slots[i].getDay().getWeek())));
		}
		int[] valsWeeks = getArrayInt(getIdMWeek(services.getCalendarService().getWeeksSorted(planning.getCalendar().getId()).toArray(Week[]::new)));
		for (int i = lessons.length; i < slots.length; i ++) {
			lessonsV.get(i).add(model.intVar("Dummy VarWeek", valsWeeks));
			sortedLessonsV.get(i).add(model.intVar(getIdMWeek(slots[i].getDay().getWeek())));
		}
	}
	
	private void setConstraintSequences(Model model) {
		List<Sequencing> sequences = services.getSequencingService().getSequencingOf(planning.getCalendar().getTaf().getId());
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
		for (Day day : services.getCalendarService().getDaysSorted(planning.getCalendar().getId())) {
			List<Slot> slots = services.getDayService().findSlotsDayByCalendarSorted(day, planning.getCalendar());
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
	
	private void setConstraintAntecedences(Model model) {
		 List<Antecedence> antecedences = services.getAntecedenceService().getAntecedencesByIdTaf(planning.getCalendar().getTaf().getId());
		    
		    for (Antecedence antecedence : antecedences) {
		        Lesson previousLesson = antecedence.getPreviousLesson();
		        Lesson nextLesson = antecedence.getNextLesson();
		        
		        IntVar previousSlot = getLessonVarSlot(previousLesson);
		        IntVar nextSlot = getLessonVarSlot(nextLesson);

		        model.arithm(nextSlot, ">", previousSlot).post();
		    }
	}
	
	
	private static void setSynchronisationConstraints(Model model, SolverMain[] solMains) {
		for (int i = 0; i < solMains.length - 1; i ++) {
			for (Synchronization sync : services.getSynchronizationService().getSynchronizationsByIdTaf(solMains[i].getPlanning().getCalendar().getTaf().getId()))
				if (sync.getLesson1().getUe().getTaf().getId() == solMains[i].getPlanning().getCalendar().getTaf().getId())
					model.arithm(solMains[i].getLessonVarSlotGlobal(sync.getLesson1()), "=", getSolMainTaf(solMains, sync.getLesson2().getUe().getTaf()).getLessonVarSlotGlobal(sync.getLesson2())).post();
		}
	}
	
	public static SolverMain getSolMainTaf(SolverMain[] solMains, TAF taf) {
		for (SolverMain solMain : solMains)
			if (solMain.getPlanning().getCalendar().getTaf().getId() == taf.getId())
				return solMain;
		return null;
	}

	private void setConstraintGlobalUnavailability(Model model) {
		for (Slot slot : services.getCalendarService().getSlotsOrdered(planning.getCalendar().getId()))
			if (services.getGlobalUnavailabilityService().findBySlot(slot).filter(g -> g.getStrict()).isPresent())
				model.arithm(this.getSlotVarLesson(slot), "=", 0).post();
	}

	private void setConstraintLecturerUnavailability(Model model) {
		for (UE ue : planning.getCalendar().getTaf().getUes())
			for (Lesson lesson : ue.getLessons())
				for (Slot slot : services.getLessonService().findLessonLecturersUnavailabilitiesByLessonAndCalendar(lesson, planning.getCalendar()))
					model.arithm(getLessonVarSlot(lesson), "!=", getIdMSlot(slot)).post();					
	}
	
	private void setConstraintLunchBreak(Model model) {
		LocalTime startLunch = LocalTime.of(12, 00);
		LocalTime endLunch = LocalTime.of(13, 30);
		for (Day day : services.getCalendarService().getDaysSorted(planning.getCalendar().getId())) {
			List<Slot> possibleSlotsForLunchTime = new ArrayList<Slot>();
			List<Slot> slotsDay = services.getDayService().findSlotsDayByCalendar(day, planning.getCalendar());
			//If the end of the classes possible this day is before the end of the lunchBreak (or conversely with the beginning),
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
					IntVar count = model.intVar("Count Lunch Day-" + day.getId(),1, possibleSlotsForLunchTime.size());
					model.count(0, possibleSlotsForLunchTime.stream().map(s -> getSlotVarLesson(s)).toArray(IntVar[]::new), count).post();
				}
			}
		}
	}
	
	private void setConstraintNoInterweaving(Model model) {
		HashMap<Long, FiniteAutomaton> automatons = new HashMap<Long,FiniteAutomaton>();
		int nbUe = planning.getCalendar().getTaf().getUes().size();
		for (UE ue : planning.getCalendar().getTaf().getUes()) {
			String ueString = "<" + getIdMUe(ue) + ">";
			automatons.put(ue.getId(), new FiniteAutomaton("[^" + ueString + "]*[" + ueString + "|0]*[^" + ueString + "]*", 0, nbUe));
		}
		for (Day day : services.getCalendarService().getDaysSorted(planning.getCalendar().getId())) {
			//System.out.println(services.getDayService().findSlotsDayByCalendar(day, cal));
			IntVar[] varsDay = services.getDayService().findSlotsDayByCalendar(day, planning.getCalendar()).stream().map(s -> getSlotVarUe(s)).toArray(IntVar[]::new);
			//System.out.println(Arrays.deepToString(varsDay));
			if (varsDay.length > 0)
				for (UE ue : planning.getCalendar().getTaf().getUes()) {
					//System.out.println(getIdMUe(ue));
					model.regular(varsDay, automatons.get(ue.getId())).post();
				}
		}
	}
	
	private void setConstraintMinMaxLessonUeInWeek(Model model) {
		for (Week week : services.getCalendarService().getWeeksSorted(planning.getCalendar().getId())) {
			IntVar[] varSlots = getSlotVarUe(services.getSlotService().findSlotsByWeekAndCalendar(week, planning.getCalendar()).stream().toArray(Slot[]::new));
			for (UE ue : planning.getCalendar().getTaf().getUes()) {
				int min = 2;
				int max = 6;
				int[] valsCnt = new int[max - min + 2];
				valsCnt[0] = 0;
				for (int i = 1; i <= max - min + 1; i ++)
					valsCnt[i] = min + i - 1;
				//System.out.println(valsCnt[valsCnt.length - 1]);
				IntVar cnt = model.intVar("Cnt min max " + ue.getName() + ", Week :" + week.getNumber(), valsCnt);
				model.count(getIdMUe(ue), varSlots, cnt).post();
				countsDebug.add(cnt);
			}
		}
	}
	
	private HashMap<Integer, List<IntVar>> setConstraintSortedLessonsUeVarDayOrWeek(Model model, boolean sortWeeks) {
		HashMap<Integer, List<IntVar>> sortedLessonsVar = new HashMap<Integer, List<IntVar>>();
		for (UE ue : planning.getCalendar().getTaf().getUes()) {
			IntVar[][] vars;
			int nbVals;
			if (sortWeeks) {
				vars = ue.getLessons().stream().map(l -> new IntVar[] {getLessonVarWeek(l)}).toArray(IntVar[][]::new);
				nbVals = services.getCalendarService().getWeeksSorted(planning.getCalendar().getId()).size();
			}
			else {
				vars = ue.getLessons().stream().map(l -> new IntVar[] {getLessonVarDay(l)}).toArray(IntVar[][]::new);
				nbVals = services.getCalendarService().getDaysSorted(planning.getCalendar().getId()).size();
			}
			System.out.println(sortWeeks);
			System.out.println(Arrays.deepToString(vars));
			IntVar[][] sortedVars = IntStream.range(0,vars.length).mapToObj(i -> new IntVar[] {model.intVar("sortedVarLessUe " + ue.getName() + " (" + i + ")", 0, nbVals)}).toArray(IntVar[][]::new);
			IntVar[] permutations = model.intVarArray("Perm SortedLessUe " + ue.getName(), vars.length, 1, vars.length);
			model.keySort(vars, permutations, sortedVars, 1).post();
			sortedLessonsVar.put(getIdMUe(ue), Arrays.stream(sortedVars).map(t -> t[0]).toList());
		}
		return sortedLessonsVar;
	}
	
	private void setConstraintSpreadMinMaxWeeks(Model model) {
		
	}
	
	private IntVar setPreferences(Model model) {
		ArrayList<IntVar> preferences = new ArrayList<IntVar>();
		/*if (preferencesGlobal)*/ //preferences.add(setPreferencesGlobal(model).mul(30).intVar());
		/*if (preferencesLecturers)*/ //preferences.add(setPreferencesLecturers(model).mul(19).intVar());
		/*if (preferenceCentered)*/ //preferences.add(setPreferenceCenteredLessons(model).mul(1).intVar());
		/*if (preferenceRegroupLessons)*/ preferences.add(setPreferenceRegroupLessonsByNbSlots(model).mul(10).intVar());
		/*if (preferenceMaxBreakUe)*/ //preferences.add(setPreferenceMaxBreakWithoutLessonUe(model).mul(5).intVar()); //TODO Change the mul factor to have something proportionnal with the valMax (i.e. having a fixed cost when the break is the double than the prefered max because now the cost is of one for each unit of time)
		return model.sum("Preferences", preferences.stream().filter(v -> v != null).toArray(IntVar[]::new));
	}
	
	private IntVar setPreferencesGlobal(Model model) {
		
		 List<Slot> slots = services.getCalendarService().getSlotsOrdered(planning.getCalendar().getId());
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

	private IntVar setPreferencesLecturers(Model model) {
		ArrayList<IntVar> isNotPreferredVars = new ArrayList<IntVar>();

		for (UE ue : planning.getCalendar().getTaf().getUes()) {
		    for (Lesson lesson : ue.getLessons()) {
		    	
		    	for (Slot slot : services.getLessonService().findNotPreferedSlotsByLessonAndCalendar(lesson, planning.getCalendar())) {
		    		BoolVar isNotPreferredVar = model.boolVar("NotPreferred_" + lesson.getId() + "_" + slot.getId());
		    		model.reification(isNotPreferredVar, model.arithm(getLessonVarSlot(lesson), "=", getIdMSlot(slot)));
		    		isNotPreferredVars.add(isNotPreferredVar);
		    	}
		    }		    
		}   
		return model.sum("NotPreferredAllocations", isNotPreferredVars.stream().toArray(IntVar[]::new));
	}
	
	private IntVar setPreferenceCenteredLessons(Model model) {
		ArrayList<IntVar> penaltyNotCentered = new ArrayList<IntVar>();
		for (Day day : services.getCalendarService().getDaysSorted(planning.getCalendar().getId())) {
			List<Slot> slots = services.getDayService().findSlotsDayByCalendarSorted(day, planning.getCalendar());
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
	
	/*private IntVar setPreferenceRegroupLessonsByNbSlots2(Model model) {
		List<Day> days = services.getCalendarService().getDaysSorted(planning.getCalendar().getId()); 
		List<List<Slot>> slotDays = days.stream().map(d -> services.getDayService().findSlotsDayByCalendar(d, planning.getCalendar())).toList();
		int nbMaxSlotsDay = slotDays.stream().mapToInt(l -> l.size()).max().orElse(0);
		if (nbMaxSlotsDay == 0) return null;
		List<IntVar> distancesFromPreferedValues = new ArrayList<IntVar>();
		int nbUe = planning.getCalendar().getTaf().getUes().size();
 		for (UE ue : planning.getCalendar().getTaf().getUes()) {
			int[] preferedVals = new int[] {2, 3}; //Must be ordered
			int[] costs = getCostsTblRegroupLessons(preferedVals, nbMaxSlotsDay);
			System.out.println(Arrays.toString(costs));
			int iDay = 0;
			for (List<Slot> slots : slotDays) {
				IntVar cnt = model.count("Count Ue " + ue.getName() + "(" + getIdMUe(ue) + ") in Day " + getIdMDay(days.get(iDay)),
										getIdMUe(ue),
										getSlotVarUe(slots.stream().toArray(Slot[]::new)));
				System.out.println(Arrays.deepToString(getSlotVarUe(slots.stream().toArray(Slot[]::new))));
				distancesFromPreferedValues.add(
						model.element("Distance Ue " + ue.getName() + "(" + getIdMUe(ue) + ") in Day " + getIdMDay(days.get(iDay)),
									costs, cnt, 0));
				iDay ++;
			}
		}
 		IntVar preferenceNoInterweaving = setPreferenceNoInterweaving(model, nbMaxSlotsDay).mul(2).intVar();
 		IntVar preferenceNbLessons = model.sum("preferenceNbLessons", distancesFromPreferedValues.stream().toArray(IntVar[]::new));
		return model.sum("preferenceRegroupLessons", preferenceNoInterweaving, preferenceNbLessons);
	}*/
	
	private IntVar setPreferenceRegroupLessonsByNbSlots(Model model) {
		List<Day> days = services.getCalendarService().getDaysSorted(planning.getCalendar().getId()); 
		List<List<Slot>> slotDays = days.stream().map(d -> services.getDayService().findSlotsDayByCalendar(d, planning.getCalendar())).toList();
		int nbMaxSlotsDay = slotDays.stream().mapToInt(l -> l.size()).max().orElse(0);
		if (nbMaxSlotsDay == 0) return null;
		List<IntVar> distancesFromPreferedValues = new ArrayList<IntVar>();
		int nbUe = planning.getCalendar().getTaf().getUes().size();
 		List<UE> ues = planning.getCalendar().getTaf().getUes();
 		int[][] preferedVals = IntStream.range(0, nbUe).mapToObj(i -> new int[] {2,3}).toArray(int[][]::new);
 		int[][] costs = IntStream.range(0, nbUe).mapToObj(i -> getCostsTblRegroupLessons(preferedVals[i], nbMaxSlotsDay)).toArray(int[][]::new);
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
 		IntVar preferenceNoInterweaving = setPreferenceNoInterweaving(model, nbMaxSlotsDay).mul(2).intVar();
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
	
	private IntVar setPreferenceNoInterweaving(Model model) {
		return setPreferenceNoInterweaving(model, services.getCalendarService().getDaysSorted(planning.getCalendar().getId()).stream().mapToInt(l -> l.getSlots().size()).max().orElse(0));
	}
	
	private IntVar setPreferenceNoInterweaving(Model model, int nbMaxSlotsDay) {
		if (nbMaxSlotsDay == 0) return null;
		List<Day> days = services.getCalendarService().getDaysSorted(planning.getCalendar().getId()); 
		List<UE> ues = planning.getCalendar().getTaf().getUes();
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
				IntVar costForward = model.intVar("CostForward " + ue.getName() + " day " + day.getId(), 0, nbMaxSlotsDay - 1);
				IntVar costBackward = model.intVar("CostBackward " + ue.getName() + " day " + day.getId(), 0, nbMaxSlotsDay - 1);
				IntVar costDay = model.intVar("CostDay " + ue.getName() + " day " + day.getId(), 0, nbMaxSlotsDay - 2);
				IntVar[] vars = services.getDayService().findSlotsDayByCalendarSorted(day, planning.getCalendar()).stream().map(s -> getSlotVarUe(s)).toArray(IntVar[]::new);
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
		FiniteAutomaton automaton = new FiniteAutomaton();
		int idMUe = getIdMUe(ue);
		int[] alphabet = IntStream.range(0, nbUes + 1).toArray();
		int[] alphabetWithoutUe = Arrays.stream(alphabet).filter(val -> val != idMUe).toArray();
		int state0 = automaton.addState();
		int state1 = automaton.addState();
		automaton.setInitialState(state0);
		automaton.setFinal(state0, state1);
		automaton.addTransition(state0, state0, alphabetWithoutUe);
		automaton.addTransition(state0, state1, idMUe);
		automaton.addTransition(state1, state1, alphabet);
		return automaton;
	}
	
	private IntVar setPreferenceMaxBreakWithoutLessonUe(Model model) {
		boolean breakUnitInWeeks = true;
		int valMax = 2;
		List<IntVar> penalties = new ArrayList<IntVar>();
		HashMap<Integer, List<IntVar>> sortedLessonsVar = setConstraintSortedLessonsUeVarDayOrWeek(model, breakUnitInWeeks);
		IntVar zero = model.intVar("Zero", 0);
		for (UE ue : planning.getCalendar().getTaf().getUes()) {
			List<IntVar> sorteds = sortedLessonsVar.get(getIdMUe(ue));
			for (int i = 0; i < sorteds.size() - 1; i ++) {
				penalties.add(model.max("MaxBreak " + ue.getName() + " (" + i + ")",
										new IntVar[] {zero,
													sorteds.get(i + 1).add(sorteds.get(i).add(valMax).neg()).intVar()}));
			}
		}
		return model.sum("PreferenceMaxBreak", penalties.stream().toArray(IntVar[]::new));
	}
	
	private static void setStrategy(SolverMain solMain, Solver solver, Planning planning) {
		IntVar[] decisionVars = solMain.getDecisionVars();
		//solver.setSearch(Search.minDomLBSearch(decisionVars));
		solver.setSearch(Search.minDomUBSearch(decisionVars));
		//solver.setSearch(Search.activityBasedSearch(decisionVars));
		//solver.setSearch(Search.conflictHistorySearch(decisionVars));
		//solver.setSearch(Search.domOverWDegSearch(decisionVars));
	}
	
	private static void setStrategy(SolverMain[] solMains, Solver solver, Planning[] plannings) {
		IntVar[] decisionVars = ArrayUtils.flatten(IntStream.range(0, plannings.length).
									mapToObj(i -> solMains[i].getDecisionVars()).toArray(IntVar[][]::new));  
		solver.setSearch(Search.minDomLBSearch(decisionVars));
		//solver.setSearch(Search.minDomUBSearch(getVarDecisionSlots(cal)));
	}
	
	private IntVar[] getDecisionVars() {
		return getVarDecisionSlots();
		//return ArrayUtils.concat(getVarDecisionSlots(), getVarDecisionLessons());
	}
	
	private IntVar[] getVarDecisionSlots() {
		List<IntVar> vars = new ArrayList<IntVar>();
		vars.addAll(planning.getCalendar().getSlots().stream().map(s -> getSlotVarLesson(s)).toList());
		IntVar[] varsArr = convertListToArrayIntVar(vars);
		return varsArr;
	}
	
	private IntVar[] getVarDecisionLessons() {
		List<IntVar> vars = new ArrayList<IntVar>();
		vars.addAll(services.getTafService().getLessonsOfTAF(planning.getCalendar().getTaf().getId()).stream().map(l -> getLessonVarSlot(l)).toList());
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
		for (Day day : services.getCalendarService().getDaysSorted(planning.getCalendar().getId())) {
			List<Slot> slots = services.getDayService().findSlotsDayByCalendarSorted(day, planning.getCalendar());
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
		for (Slot s : planning.getCalendar().getSlots())
			if (solution.getIntVal(getSlotVarLesson(s)) != 0)
				results.add(new Result(s.getId(), getIdLesson(solution.getIntVal(getSlotVarLesson(s)))));
		return results;
	}
	
	private String makeSolutionString(Solution solution) {
		StringBuilder res = new StringBuilder();
		res.append("{\"slots\":[");
		planning.getCalendar().getSlots().stream().forEach(s -> res.append("{\"id\":" + s.getId() + (solution.getIntVal(getSlotVarLesson(s)) != 0 ? ",\"lessonId\":" + this.getIdLesson(solution.getIntVal(getSlotVarLesson(s))) :  "") + "},"));
		res.deleteCharAt(res.length()-1);
		res.append("]}");
		return res.toString();
	}
	
	private String showSolutionsDebug(Solution solution) {
		StringBuilder res = new StringBuilder();
		res.append(planning.getCalendar().getTaf().getName() + "\r\n");
		res.append("Slots : [");
		planning.getCalendar().getSlots().forEach(s -> res.append("{id : " + s.getId() + 
												(IdMSlotGlobal != null ? ", idGlob : " + getIdMSlotGlobal(s) : "") +
												(solution.getIntVal(getSlotVarLesson(s)) != 0 ? ", lessonId : " + this.getIdLesson(solution.getIntVal(getSlotVarLesson(s))) :  "") +
												(true && solution.getIntVal(getSlotVarUe(s)) != 0 ? ", UeId : " + this.getIdUe(solution.getIntVal(getSlotVarUe(s))) :  "") +
												"},"));
		res.deleteCharAt(res.length() - 1);
		res.append("]\r\n");
		res.append("Lessons : [");
		planning.getCalendar().getTaf().getUes().forEach(u -> u.getLessons().forEach(l -> res.append("{id : " + l.getId() +
																					", slotId : " + this.getIdSlot(solution.getIntVal(getLessonVarSlot(l))) +
																					(IdMSlotGlobal != null ? ", idGlobVar : " + solution.getIntVal(getLessonVarSlotGlobal(l)) : "") +
																					(!lessonVarDay.isEmpty() ? ", idDayVar : " + solution.getIntVal(getLessonVarDay(l)) : "") +
																					(!lessonVarWeek.isEmpty() ? ", idWeekVar : " + solution.getIntVal(getLessonVarWeek(l)) : "") +
																					"},")));
		res.deleteCharAt(res.length() - 1);
		res.append("]");
		res.append("]\r\n");
		countsDebug.forEach(c -> res.append(c.getName() + " " +  solution.getIntVal(c) + ";"));
		
		return res.toString();
	}
	
	public static void main(String[] args) {
		
	}
	
}
/*
Rust :

runtime environment performance
fast reliable productive
compiled
static typing
imperative with some functional
no garbage collection
guarantee

vs Python
Much faster
much lower memory use
multi-threading
pattern matching
many fewer runtime (static typing)
algebraic data types.

vs Java
No JVM or GC pauses
Much lower memory
Zero-cost abstraction
Pattern matching
ConcurrentModificationException
Unified build system (no maven/...)
dependency management

vs C/C++
No segfaults
No buffer overflows
No null pointer
No data race
Powerful type
Unified build system
Dependency management

vs Go
No GC pauses
lower memory use
No null pointer
Nicer error handling
Safe concurrency
Stronger type system
Zero-cost abstraction
Dependency management


Nice and efficient generics
Algebraic data types + pattern
Modern tooling
-> Test and documentation

Pointers are checked at compile-time
Thread-safety from types
No hidden states

No GC or runtime
Control allocation and dispatch
Can write + wrap low-level code


*/
