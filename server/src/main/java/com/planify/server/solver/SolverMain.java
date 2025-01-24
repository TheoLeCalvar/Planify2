package com.planify.server.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.nary.automata.FA.FiniteAutomaton;
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
		solMain.initialiseVars(model, nbSlots, nbLessons, true, false, false);
		solMain.setConstraints(model);
		IntVar obj = solMain.setPreferences(model);
		setStrategy(solMain, solver, planning);
		Solution solution;
		if (obj != null) solution = solver.findOptimalSolution(obj, false);
		else solution = solver.findSolution();
		System.out.println(model);
		solver.printShortStatistics();
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
		solMain.initialiseVars(model, nbSlots, nbLessons, true, false, false);
		solMain.setConstraints(model);
		IntVar obj = solMain.setPreferences(model);
		setStrategy(solMain, solver, planning);
		Solution solution;
		if (obj != null) solution = solver.findOptimalSolution(obj, false);
		else solution = solver.findSolution();
		System.out.println(model);
		solver.printShortStatistics();
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
		for (int i = 0; i < plannings.length; i ++)
			System.out.println(solMains[i].showSolutionsDebug(solution));
	}
	
	private void initialiseVars(Model model, int nbSlots, int nbLessons, HashMap<Long, Integer> idToIdMGlobal) {
		initialiseVars(model, nbSlots, nbLessons, true, false, false);
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
						int comparison = slots.get(i).get(iSlots[i]).compareTo(slots.get(iMins.get(0)).get(iSlots[iMins.get(0)]));
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
		setConstraintLinkLessonsSlots(model, true);
		setConstraintLinkSlotGlobalDayWeek(model, this.IdMSlotGlobal != null, false, false);
		setConstraintSequences(model);
		setConstraintAntecedences(model);
		/*if (planning.hasConstraintGlobalUnavailability()*/ setConstraintGlobalUnavailability(model);
		/*if (planning.hasConstraint1())*/ setConstraintLecturerUnavailability(model);
		/*if (planning.hasConstraint())*/ //setConstraintLunchBreak(model);
		/*if (planning.hasConstraint())*/ setConstraintNoInterweaving(model);
		/*if (planning.hesConstraint()*/ //setConstraintMinMaxLessonUeInWeek(model); //Idée pour essayer d'améliorer les performances si besoin : essayer de faire l'optimisation sur les variables du nombre de cours de l'UE considéré.
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
	
	private void setConstraintLinkSlotGlobalDayWeek2(Model model, boolean global, boolean day, boolean week) {
		if (!(global || day || week)) return;
		List<Slot> slots = services.getCalendarService().getSlotsOrdered(planning.getCalendar().getId());
		int[] globInt = new int[] {};
		if (global) globInt = slots.stream().mapToInt(s -> getIdMSlotGlobal(s)).toArray();
		int[] dayInt = new int[] {};
		if (day) dayInt = slots.stream().mapToInt(s -> getIdMDay(s.getDay())).toArray();
		int[] weekInt = new int[] {};
		if (week) weekInt = slots.stream().mapToInt(s -> getIdMWeek(s.getDay().getWeek())).toArray();
		for (Lesson lesson : services.getTafService().getLessonsOfTAF(planning.getCalendar().getTaf().getId())) {
			if (global) model.element(getLessonVarSlot(lesson), globInt, getLessonVarSlotGlobal(lesson)).post();
			if (day) model.element(getLessonVarSlot(lesson), dayInt, getLessonVarDay(lesson)).post();
			if (week) model.element(getLessonVarSlot(lesson), weekInt, getLessonVarWeek(lesson)).post();
		}
	}
	
	private void setConstraintLinkSlotGlobalDayWeek(Model model, boolean global, boolean day, boolean week) {
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
			lV.add(model.intVar(1, nbSlots));
			lessonsV.add(lV);
			List<IntVar> sLV = new ArrayList<IntVar>();
			sLV.add(model.intVar(i));
			sortedLessonsV.add(sLV);
		}
		
		if (global) addGlobalListsConstraintLink(model, lessons, slots, lessonsV, sortedLessonsV);
		if (day) addDayListsConstraintLink(model, lessons, slots, lessonsV, sortedLessonsV);
		if (week) addWeekListsConstraintLink(model, lessons, slots, lessonsV, sortedLessonsV);
		
		IntVar[] permutations = model.intVarArray(nbSlots, 1, nbSlots);

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
			lessonsV.get(i).add(model.intVar(valsGlobs));
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
			lessonsV.get(i).add(model.intVar(valsDays));
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
			lessonsV.get(i).add(model.intVar(valsWeeks));
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
		for (Day day : services.getCalendarService().getDaysSorted(planning.getCalendar().getId())) {
			List<Slot> possibleSlotsForLunchTime = new ArrayList<Slot>();
			for (Slot slot: services.getDayService().findSlotsDayByCalendar(day, planning.getCalendar())) {
				if (slot.getNumber() == 2 || slot.getNumber() == 3)
					possibleSlotsForLunchTime.add(slot);
			}
			if (possibleSlotsForLunchTime != null) {
				boolean lunchBreakAlreadyFixed = false;
				for (Slot slot : possibleSlotsForLunchTime)
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
				int max = 4;
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
	
	private IntVar setPreferences(Model model) {
		ArrayList<IntVar> preferences = new ArrayList<IntVar>();
		/*if (preferencesGlobal)*/ //preferences.add(setPreferencesGlobal(model));
		/*if (preferencesLecturers)*/ preferences.add(setPreferencesLecturers(model));
		return model.sum("Preferences", preferences.stream().filter(v -> v != null).toArray(IntVar[]::new));
	}
	
	private IntVar setPreferencesGlobal(Model model) {
	    ArrayList<IntVar> globalPreferences = new ArrayList<IntVar>();

	    for (UE ue : planning.getCalendar().getTaf().getUes()) {
	        for (Lesson lesson : ue.getLessons()) {
	            for (LessonLecturer lessonLecturer : lesson.getLessonLecturers()) {
	            	User lecturer = lessonLecturer.getUser();
	                List<Slot> notPreferredSlots = services.getUserService().getNotPreferedSlotsByUserAndCalendar(lecturer, planning.getCalendar());

	                for (Slot notPreferredSlot : notPreferredSlots) {
	                    BoolVar isNotPreferredVar = model.boolVar("GlobalNotPreferred_" + lesson.getId() + "_" + notPreferredSlot.getId());
	                    
	                    model.reification(isNotPreferredVar, model.arithm(getLessonVarSlot(lesson), "=", getIdMSlot(notPreferredSlot)));
	                    globalPreferences.add(isNotPreferredVar);
	                }
	            }
	        }
	    }
	    return model.sum("GlobalPreferences", globalPreferences.stream().toArray(IntVar[]::new));
	}

	private IntVar setPreferencesLecturers(Model model) {
		
		//IntVar notPreferredAllocations = model.intVar("NotPreferredAllocations", 0, Integer.MAX_VALUE);
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
	
	private static void setStrategy(SolverMain solMain, Solver solver, Planning planning) {
		IntVar[] decisionVars = solMain.getDecisionVars();
		//solver.setSearch(Search.minDomLBSearch(decisionVars));
		solver.setSearch(Search.minDomUBSearch(decisionVars));
	}
	
	private static void setStrategy(SolverMain[] solMains, Solver solver, Planning[] plannings) {
		IntVar[] decisionVars = ArrayUtils.flatten(IntStream.range(0, plannings.length).
									mapToObj(i -> solMains[i].getDecisionVars()).toArray(IntVar[][]::new));  
		solver.setSearch(Search.minDomLBSearch(decisionVars));
		//solver.setSearch(Search.minDomUBSearch(getVarDecisionSlots(cal)));
	}
	
	private IntVar[] getDecisionVars() {
		return getVarDecisionSlots();
	}
	
	private IntVar[] getVarDecisionSlots() {
		List<IntVar> vars = new ArrayList<IntVar>();
		vars.addAll(planning.getCalendar().getSlots().stream().map(s -> getSlotVarLesson(s)).toList());
		IntVar[] varsArr = convertListToArrayIntVar(vars);
		return varsArr;
	}
	
	private IntVar[] convertListToArrayIntVar(List<IntVar> list){
		IntVar[] arr = new IntVar[list.size()];
		for (int i = 0; i < list.size(); i ++) arr[i] = list.get(i);
		return arr;
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
