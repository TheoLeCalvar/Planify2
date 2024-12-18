package com.planify.server.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import com.planify.server.models.Calendar;
import com.planify.server.models.Day;
import com.planify.server.models.Lesson;
import com.planify.server.models.Slot;
import com.planify.server.models.Synchronization;
import com.planify.server.models.TAF;
import com.planify.server.models.UE;
import com.planify.server.models.Week;


public class SolverMain {
	//Services
	static SolverServices services;
	
	private Calendar cal;
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
	
	private HashMap<Long, Integer> IdMSlotGlobal;

	public SolverMain(Calendar cal) {
		this.cal = cal;
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
	
	public Calendar getCal() {return this.cal;};
	
	public Integer getIdMSlot(Slot slot) {return idMSlot.getValue(slot.getId());}
	public Integer[] getIdMSlot(Slot[] slots) {return IntStream.range(0, slots.length).mapToObj(i -> getIdMSlot(slots[i])).toArray(Integer[]::new);}
	public Long getIdSlot(Integer idM) {return idMSlot.getKey(idM);}
	public Long[] getIdSlot(Integer[] idMs) {return IntStream.range(0, idMs.length).mapToObj(i -> getIdSlot(idMs[i])).toArray(Long[]::new);}
	
	public IntVar getSlotVarLesson(Slot slot) {return slotVarLesson.get(slot.getId());}
	public IntVar[] getSlotVarLesson(Slot[] slots) {return IntStream.range(0, slots.length).mapToObj(i -> getSlotVarLesson(slots[i])).toArray(IntVar[]::new);}
	
	public IntVar getSlotVarUe(Slot slot) {return slotVarUe.get(slot.getId());}
	public IntVar[] getSlotVarUe(Slot[] slots) {return IntStream.range(0, slots.length).mapToObj(i -> getSlotVarUe(slots[i])).toArray(IntVar[]::new);}
	
	public Integer getIdMLesson(Lesson lesson) {return idMLesson.getValue(lesson.getId());}
	public Integer[] getIdMLesson(Lesson[] lessons) {return IntStream.range(0, lessons.length).mapToObj(i -> getIdMLesson(lessons[i])).toArray(Integer[]::new);}
	public Long getIdLesson(Integer idM) {return idMLesson.getKey(idM);}
	public Long[] getIdLesson(Integer[] idMs) {return IntStream.range(0, idMs.length).mapToObj(i -> getIdLesson(idMs[i])).toArray(Long[]::new);}
	
	public IntVar getLessonVarSlot(Lesson lesson) {return lessonVarSlot.get(lesson.getId());}
	public IntVar[] getLessonVarSlot(Lesson[] lessons) {return IntStream.range(0, lessons.length).mapToObj(i -> getLessonVarSlot(lessons[i])).toArray(IntVar[]::new);}

	public IntVar getLessonVarSlotGlobal(Lesson lesson) {return lessonVarSlotGlobal.get(lesson.getId());}
	public IntVar[] getLessonVarSlotGlobal(Lesson[] lessons) {return IntStream.range(0, lessons.length).mapToObj(i -> getLessonVarSlotGlobal(lessons[i])).toArray(IntVar[]::new);}
	
	public IntVar getLessonVarDay(Lesson lesson) {return lessonVarDay.get(lesson.getId());}
	public IntVar[] getLessonVarDay(Lesson[] lessons) {return IntStream.range(0, lessons.length).mapToObj(i -> getLessonVarDay(lessons[i])).toArray(IntVar[]::new);}
	
	public IntVar getLessonVarWeek(Lesson lesson) {return lessonVarWeek.get(lesson.getId());}
	public IntVar[] getLessonVarWeek(Lesson[] lessons) {return IntStream.range(0, lessons.length).mapToObj(i -> getLessonVarWeek(lessons[i])).toArray(IntVar[]::new);}
	
	public Integer getIdMUe(UE ue) {return idMUe.getValue(ue.getId());}
	public Integer[] getIdMUe(UE[] ues) {return IntStream.range(0, ues.length).mapToObj(i -> getIdMUe(ues[i])).toArray(Integer[]::new);}
	public Long getIdUe(Integer idM) {return idMUe.getKey(idM);}
	public Long[] getIdUe(Integer[] idMs) {return IntStream.range(0, idMs.length).mapToObj(i -> getIdUe(idMs[i])).toArray(Long[]::new);}
	
	public Integer getIdMDay(Day day) {return idMDay.getValue(day.getId());}
	public Integer[] getIdMDay(Day[] days) {return IntStream.range(0, days.length).mapToObj(i -> getIdMDay(days[i])).toArray(Integer[]::new);}
	public Long getIdDay(Integer idM) {return idMDay.getKey(idM);}
	public Long[] getIdDay(Integer[] idMs) {return IntStream.range(0, idMs.length).mapToObj(i -> getIdDay(idMs[i])).toArray(Long[]::new);}
	
	public Integer getIdMWeek(Week week) {return idMWeek.getValue(week.getId());}
	public Integer[] getIdMWeek(Week[] weeks) {return IntStream.range(0, weeks.length).mapToObj(i -> getIdMWeek(weeks[i])).toArray(Integer[]::new);}
	public Long getIdWeek(Integer idM) {return idMWeek.getKey(idM);}
	public Long[] getIdWeek(Integer[] idMs) {return IntStream.range(0, idMs.length).mapToObj(i -> getIdWeek(idMs[i])).toArray(Long[]::new);}
	
	public Integer getIdMSlotGlobal(Slot slot) {return IdMSlotGlobal.get(slot.getId());}
	public Integer[] getIdMSlotGlobal(Slot[] slots) {return IntStream.range(0, slots.length).mapToObj(i -> getIdMSlotGlobal(slots[i])).toArray(Integer[]::new);}
	
	public static void setServices(SolverServices services) {
		SolverMain.services = services;
	}
	
	public static String generateCal(Calendar cal) {
		Model model = new Model();
		Solver solver = model.getSolver();
		SolverMain solMain = new SolverMain(cal);
		int nbSlots = solMain.getNumberOfSlots();
		int nbLessons = solMain.getNumberOfLessons();
		solMain.initialiseVars(model, nbSlots, nbLessons);
		solMain.setConstraints(model);
		IntVar obj = solMain.setPreferences(model);
		setStrategy(solMain, solver, cal);
		Solution solution;
		if (obj != null) solution = solver.findOptimalSolution(obj, false);
		else solution = solver.findSolution();
		System.out.println(model);
		solver.printShortStatistics();
		System.out.println(solMain.makeSolutionString(solution));
		return solMain.makeSolutionString(solution);
	}
	
	public static void generateCals(Calendar[] cals) {
		System.out.println("Yo");
		Model model = new Model();
		Solver solver = model.getSolver();
		IntVar[] objs = new IntVar[cals.length];
		SolverMain[] solMains = new SolverMain[cals.length];
		System.out.println("Yo1");
		HashMap<Long, Integer> idToIdMGlobal = getIdToIdMGlobalCals(cals);
		System.out.println("Yo2");
		for (int i = 0; i < cals.length; i ++) {
			System.out.println("Ya" + i);
			SolverMain solMain = new SolverMain(cals[i]);
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
		setStrategy(solMains, solver, cals);
		Solution solution = solver.findOptimalSolution(globObj, false);
		System.out.println(model);
		solver.printShortStatistics();
		for (int i = 0; i < cals.length; i ++)
			System.out.println(solMains[i].showSolutionsDebug(solution));
	}
	
	private void initialiseVars(Model model, int nbSlots, int nbLessons, HashMap<Long, Integer> idToIdMGlobal) {
		initialiseVars(model, nbSlots, nbLessons);
		initialiseSync(model, idToIdMGlobal);
	}
	
	private void initialiseSync(Model model, HashMap<Long, Integer> idToIdMGlobal) {
		this.IdMSlotGlobal = idToIdMGlobal;
		int[] valGlobs = getValsSlotGlobal(services.getCalendarService().getSlotsOrdered(cal.getId()).toArray(Slot[]::new));
		for (UE ue : cal.getTaf().getUes())
			for (Lesson lesson : ue.getLessons())
					lessonVarSlotGlobal.put(lesson.getId(), model.intVar(nameLesson(lesson) + "-VarSlotGlobal", valGlobs));
	}
	
	private int[] getValsSlotGlobal(Slot[] slots) {
		ArrayList<Integer> vals = new ArrayList<Integer>();
		for (Slot slot : slots)
			vals.add(getIdMSlotGlobal(slot));
		return vals.stream().mapToInt(i -> (int) i).toArray();
	}

	private void initialiseVars(Model model, int nbSlots, int nbLessons) {
		initialiseWeeks();
		initialiseDays();
		initialiseSlots(model, nbLessons);
		initialiseUes();
		initialiseLessons(model, nbSlots);
	}
	
	private void initialiseWeeks() {
		Integer idMW = 1;
		for (Week week : services.getCalendarService().getWeeksSorted(cal.getId())) {
			idMWeek.put(week.getId(), idMW);
			idMW ++;
		}
	}
	
	private void initialiseDays() {
		Integer idMD = 1;
		for (Day day : services.getCalendarService().getDaysSorted(cal.getId())) {
			idMDay.put(day.getId(), idMD);
			idMD ++;
		}
	}
	
	private void initialiseSlots(Model model, int nbLessons) {
		Integer idMS = 1;
		for (Slot slot : services.getCalendarService().getSlotsOrdered(cal.getId())) {
			idMSlot.put(slot.getId(), idMS);
			slotVarLesson.put(slot.getId(), model.intVar(nameSlot(slot) + "-VarLesson", 0,nbLessons));
			idMS ++;
		}
	}
	
	private void initialiseUes() {
		Integer idMU = 1;
		for (UE ue : cal.getTaf().getUes()) {
			idMUe.put(ue.getId(), idMU);
			idMU ++;
		}
	}
	
	private void initialiseLessons(Model model, int nbSlots) {
		Integer idML = 1;
		for (UE ue : cal.getTaf().getUes())
			for (Lesson lesson : ue.getLessons()) {
				idMLesson.put(lesson.getId(), idML);
				lessonVarSlot.put(lesson.getId(), model.intVar(nameLesson(lesson) + "-VarSlot", 1, nbSlots));
				idML ++;
			}
	}
	
	public int getNumberOfSlots() {
		return services.getCalendarService().getNumberOfSlots(cal.getId());
	}
	
	public int getNumberOfLessons() {
		return services.getTafService().numberOfLessons(cal.getTaf().getId());
	}
	
	public static int getNumberOfSlots(HashMap<Long, Integer> idToIdMGlob) {
		int max = 0;
		for (Integer iVal : idToIdMGlob.values())
			if (max < iVal)
				max = iVal;
		return max;
	}
	
	private static HashMap<Long, Integer> getIdToIdMGlobalCals(Calendar[] cals){
		HashMap<Long, Integer> idToIdMGlob = new HashMap<Long, Integer>();
		List<List<Slot>> slots = IntStream.range(0, cals.length).mapToObj(i -> services.getCalendarService().getSlotsOrdered(cals[i].getId())).toList();
		int[] iSlots = new int[cals.length];
		int[] lengthSlots = slots.stream().mapToInt(s -> s.size()).toArray();
		Integer idMGlob = 1;
		System.out.println(Arrays.toString(lengthSlots));
		while (testiLengths(iSlots, lengthSlots)) {
			System.out.println(Arrays.toString(iSlots));
			List<Integer> iMins = new ArrayList<Integer>();
			for (int i = 0; i < cals.length; i ++) {
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

	public void setConstraints(Model model) {
		setConstraintLinkLessonsSlots(model);
		if (this.IdMSlotGlobal != null) setConstraintLinkSlotGlobalDayWeek(model, true, false, false);
		/*if (cal.hasConstraintGlobalUnavailability()*/ setConstraintGlobalUnavailability(model);
		/*if (cal.hasConstraint1())*/ setConstraintLecturerUnavailability(model);
	}
	
	private void setConstraintLinkLessonsSlots(Model model) {
		Slot[] slots = services.getCalendarService().getSlotsOrdered(cal.getId()).stream().toArray(Slot[]::new);
		Lesson[] lessons = cal.getTaf().getUes().stream().flatMap(u -> u.getLessons().stream()).toArray(Lesson[]::new);
		int nbSlots = slots.length;
		int nbLessons = lessons.length;
		IntVar[][] slotsV = IntStream.range(0, nbSlots).mapToObj(i -> new IntVar[] {getSlotVarLesson(slots[i])}).toArray(IntVar[][]::new);
		IntVar[] lessonsV = new IntVar[nbSlots];
		IntVar[][] sortedSlotsV = new IntVar[nbSlots][1];
		IntVar zero = model.intVar("zero", 0);
		for (int i = 0; i < nbSlots - nbLessons; i ++) {
			lessonsV[i] = model.intVar("DummyKeySort1 " + i, 1, nbSlots);
			sortedSlotsV[i][0] = zero;
		}
		for (int i = nbSlots - nbLessons; i < nbSlots; i ++) {
			lessonsV[i] = getLessonVarSlot(lessons[i - nbSlots + nbLessons]);
			sortedSlotsV[i][0] = model.intVar("SortedSlotsV " + i, i - nbSlots + nbLessons + 1);
		}
		model.keySort(slotsV, lessonsV, sortedSlotsV, 1).post();
	}
	
	private void setConstraintLinkSlotGlobalDayWeek(Model model, boolean global, boolean day, boolean week) {
		Slot[] slots = services.getCalendarService().getSlotsOrdered(cal.getId()).stream().toArray(Slot[]::new);
		Lesson[] lessons = cal.getTaf().getUes().stream().flatMap(u -> u.getLessons().stream()).toArray(Lesson[]::new);
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
		int[] valsDays = getArrayInt(getIdMDay(services.getCalendarService().getDaysSorted(cal.getId()).toArray(Day[]::new)));
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
		int[] valsWeeks = getArrayInt(getIdMWeek(services.getCalendarService().getWeeksSorted(cal.getId()).toArray(Week[]::new)));
		for (int i = lessons.length; i < slots.length; i ++) {
			lessonsV.get(i).add(model.intVar(valsWeeks));
			sortedLessonsV.get(i).add(model.intVar(getIdMWeek(slots[i].getDay().getWeek())));
		}
	}
	
	public static void setSynchronisationConstraints(Model model, SolverMain[] solMains) {
		for (int i = 0; i < solMains.length - 1; i ++) {
			for (Synchronization sync : services.getSynchronizationService().getSynchronizationsByIdTaf(solMains[i].getCal().getTaf().getId()))
				if (sync.getLesson1().getUe().getTaf().getId() == solMains[i].getCal().getTaf().getId())
					model.arithm(solMains[i].getLessonVarSlotGlobal(sync.getLesson1()), "=", getSolMainTaf(solMains, sync.getLesson2().getUe().getTaf()).getLessonVarSlotGlobal(sync.getLesson2())).post();
		}
	}
	
	public static SolverMain getSolMainTaf(SolverMain[] solMains, TAF taf) {
		for (SolverMain solMain : solMains)
			if (solMain.getCal().getTaf().getId() == taf.getId())
				return solMain;
		return null;
	}

	public void setConstraintGlobalUnavailability(Model model) {
		for (Slot slot : services.getCalendarService().getSlotsOrdered(cal.getId()))
			if (services.getGlobalUnavailabilityService().findBySlot(slot).filter(g -> g.getStrict()).isPresent())
				model.arithm(this.getSlotVarLesson(slot), "=", 0).post();
	}

	public void setConstraintLecturerUnavailability(Model model) {
		for (UE ue : cal.getTaf().getUes())
			for (Lesson lesson : ue.getLessons())
				for (Slot slot : services.getLessonService().findLessonLecturersUnavailabilitiesByLessonAndCalendar(lesson, cal))
					model.arithm(getLessonVarSlot(lesson), "!=", getIdMSlot(slot)).post();					
	}
	
	
	public IntVar setPreferences(Model model) {
		ArrayList<IntVar> preferences = new ArrayList<IntVar>();
		/*if (preferencesGlobal)*/ preferences.add(setPreferencesGlobal(model));
		/*if (preferencesLecturers)*/ preferences.add(setPreferencesLecturers(model));
		return model.sum("Preferences", preferences.stream().filter(v -> v != null).toArray(IntVar[]::new));
	}
	
	private IntVar setPreferencesGlobal(Model model) {
		return null;
	}

	public IntVar setPreferencesLecturers(Model model) {
		
		//IntVar notPreferredAllocations = model.intVar("NotPreferredAllocations", 0, Integer.MAX_VALUE);
		ArrayList<IntVar> isNotPreferredVars = new ArrayList<IntVar>();

		for (UE ue : cal.getTaf().getUes()) {
		    for (Lesson lesson : ue.getLessons()) {
		    	
		    	for (Slot slot : services.getLessonService().findNotPreferedSlotsByLessonAndCalendar(lesson, cal)) {
		    		BoolVar isNotPreferredVar = model.boolVar("NotPreferred_" + lesson.getId() + "_" + slot.getId());
		    		model.reification(isNotPreferredVar, model.arithm(getLessonVarSlot(lesson), "=", getIdMSlot(slot)));
		    		isNotPreferredVars.add(isNotPreferredVar);
		    	}
		    }		    
		}   
		return model.sum("NotPreferredAllocations", isNotPreferredVars.stream().toArray(IntVar[]::new));
	}
	
	public static void setStrategy(SolverMain solMain, Solver solver, Calendar cal) {
		IntVar[] decisionVars = solMain.getDecisionVars();
		solver.setSearch(Search.minDomLBSearch(decisionVars));
		//solver.setSearch(Search.minDomUBSearch(decisionVars));
	}
	
	public static void setStrategy(SolverMain[] solMains, Solver solver, Calendar[] cals) {
		IntVar[] decisionVars = ArrayUtils.flatten(IntStream.range(0, cals.length).
									mapToObj(i -> solMains[i].getDecisionVars()).toArray(IntVar[][]::new));  
		solver.setSearch(Search.minDomLBSearch(decisionVars));
		//solver.setSearch(Search.minDomUBSearch(getVarDecisionSlots(cal)));
		
	}
	
	public IntVar[] getDecisionVars() {
		return getVarDecisionSlots();
	}
	
	public IntVar[] getVarDecisionSlots() {
		List<IntVar> vars = new ArrayList<IntVar>();
		vars.addAll(cal.getSlots().stream().map(s -> getSlotVarLesson(s)).toList());
		IntVar[] varsArr = convertListToArrayIntVar(vars);
		return varsArr;
	}
	
	public IntVar[] convertListToArrayIntVar(List<IntVar> list){
		IntVar[] arr = new IntVar[list.size()];
		for (int i = 0; i < list.size(); i ++) arr[i] = list.get(i);
		return arr;
	}
	
	public int[] getArrayInt(Integer[] tbl) {
		int[] tblRes = new int[tbl.length];
		for (int i = 0; i < tbl.length; i ++)
			tblRes[i] = tbl[i].intValue();
		return tblRes;
	}
	
	public String nameSlot(Slot slot) {
		return "Slot " + idMSlot.getValue(slot.getId()) + " (" + slot.getId() + ")";
	}
	
	public String nameLesson(Lesson lesson) {
		return "Lesson " + idMLesson.getValue(lesson.getId()) + " (" + lesson.getId() + ")";
	}
	
	public String makeSolutionString(Solution solution) {
		StringBuilder res = new StringBuilder();
		res.append("{\"slots\":[");
		cal.getSlots().stream().forEach(s -> res.append("{\"id\":" + s.getId() + (solution.getIntVal(getSlotVarLesson(s)) != 0 ? ",\"lessonId\":" + this.getIdLesson(solution.getIntVal(getSlotVarLesson(s))) :  "") + "},"));
		res.deleteCharAt(res.length()-1);
		res.append("]}");
		return res.toString();
	}
	
	public String showSolutionsDebug(Solution solution) {
		StringBuilder res = new StringBuilder();
		res.append(cal.getTaf().getName() + "\r\n");
		res.append("Slots : [");
		cal.getSlots().forEach(s -> res.append("{id : " + s.getId() + 
												(IdMSlotGlobal != null ? ", idGlob : " + getIdMSlotGlobal(s) : "") +
												(solution.getIntVal(getSlotVarLesson(s)) != 0 ? ", lessonId : " + this.getIdLesson(solution.getIntVal(getSlotVarLesson(s))) :  "") +
												"},"));
		res.deleteCharAt(res.length() - 1);
		res.append("]\r\n");
		res.append("Lessons : [");
		cal.getTaf().getUes().forEach(u -> u.getLessons().forEach(l -> res.append("{id : " + l.getId() +
																					", slotId : " + this.getIdSlot(solution.getIntVal(getLessonVarSlot(l))) +
																					(IdMSlotGlobal != null ? ", idGlobVar : " + solution.getIntVal(getLessonVarSlotGlobal(l)) : "") +
																					"},")));
		res.deleteCharAt(res.length() - 1);
		res.append("]");
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
