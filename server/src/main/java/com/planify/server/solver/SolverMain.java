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
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.planify.server.models.Calendar;
import com.planify.server.models.Day;
import com.planify.server.models.Lesson;
import com.planify.server.models.LessonLecturer;
import com.planify.server.models.Slot;
import com.planify.server.models.TAF;
import com.planify.server.models.UE;
import com.planify.server.models.User;
import com.planify.server.models.UserUnavailability;
import com.planify.server.models.Week;
import com.planify.server.service.CalendarService;
import com.planify.server.service.LessonService;
import com.planify.server.service.TAFService;


public class SolverMain {
	//Services
	static SolverServices services;
	
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

	public SolverMain() {
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
	
	public static void generateCal(Calendar cal) {
		Model model = new Model();
		Solver solver = model.getSolver();
		SolverMain solMain = new SolverMain();
		int nbSlots = solMain.getNumberOfSlots(cal);
		int nbLessons = solMain.getNumberOfLessons(cal.getTaf());
		solMain.initialiseVars(model, cal, nbSlots, nbLessons);
		solMain.setConstraints(model, cal);
		IntVar obj = solMain.setPreferences(model, cal);
		setStrategy(solMain, solver, cal);
		Solution solution;
		if (obj != null) solution = solver.findOptimalSolution(obj, false);
		else solution = solver.findSolution();
		System.out.println(model);
		solver.printShortStatistics();
		System.out.println(solMain.makeSolutionString(cal, solution));
	}
	
	public static void generateCals(Calendar[] cals) {
		Model model = new Model();
		Solver solver = model.getSolver();
		IntVar[] objs = new IntVar[cals.length];
		SolverMain[] solMains = new SolverMain[cals.length];
		HashMap<Long, Integer> idToIdMGlobal = getIdToIdMGlobalCals(cals);
		for (int i = 0; i < cals.length; i ++) {
			SolverMain solMain = new SolverMain();
			solMains[i] = solMain;
			Calendar cal = cals[i];
			int nbSlots = solMain.getNumberOfSlots(cal);
			int nbLessons = solMain.getNumberOfLessons(cal.getTaf());
			solMain.initialiseVars(model, cal, nbSlots, nbLessons, idToIdMGlobal);
			solMain.setConstraints(model, cal);
			IntVar obj = solMain.setPreferences(model, cal);
			objs[i] = obj != null ? obj : model.intVar(0);
		}
		//setSynchronisationConstraints(model, cals);
		IntVar globObj = model.sum("globObj", objs);
		setStrategy(solMains, solver, cals);
		Solution solution = solver.findOptimalSolution(globObj, false);
		System.out.println(model);
		solver.printShortStatistics();
		for (int i = 0; i < cals.length; i ++)
			System.out.println(cals[i].getTaf() + " : " + solMains[i].makeSolutionString(cals[i], solution));
	}
	
	private void initialiseVars(Model model, Calendar cal, int nbSlots, int nbLessons, HashMap<Long, Integer> idToIdMGlobal) {
		initialiseVars(model, cal, nbSlots, nbLessons);
		initialiseSync(model, cal, idToIdMGlobal);
	}
	
	private void initialiseSync(Model model, Calendar cal, HashMap<Long, Integer> idToIdMGlobal) {
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

	private void initialiseVars(Model model, Calendar cal, int nbSlots, int nbLessons) {
		initialiseWeeks(cal);
		initialiseDays(cal);
		initialiseSlots(model, cal, nbLessons);
		initialiseUes(cal.getTaf());
		initialiseLessons(model, cal.getTaf(), nbSlots);
	}
	
	private void initialiseWeeks(Calendar cal) {
		Integer idMW = 1;
		for (Week week : services.getCalendarService().getWeeksSorted(cal.getId())) {
			idMWeek.put(week.getId(), idMW);
			idMW ++;
		}
	}
	
	private void initialiseDays(Calendar cal) {
		Integer idMD = 1;
		for (Day day : services.getCalendarService().getDaysSorted(cal.getId())) {
			idMDay.put(day.getId(), idMD);
			idMD ++;
		}
	}
	
	private void initialiseSlots(Model model, Calendar cal, int nbLessons) {
		Integer idMS = 1;
		for (Slot slot : services.getCalendarService().getSlotsOrdered(cal.getId())) {
			idMSlot.put(slot.getId(), idMS);
			slotVarLesson.put(slot.getId(), model.intVar(nameSlot(slot) + "-VarLesson", 0,nbLessons));
			idMS ++;
		}
	}
	
	private void initialiseUes(TAF taf) {
		Integer idMU = 1;
		for (UE ue : taf.getUes()) {
			idMUe.put(ue.getId(), idMU);
			idMU ++;
		}
	}
	
	private void initialiseLessons(Model model, TAF taf, int nbSlots) {
		Integer idML = 1;
		for (UE ue : taf.getUes())
			for (Lesson lesson : ue.getLessons()) {
				idMLesson.put(lesson.getId(), idML);
				lessonVarSlot.put(lesson.getId(), model.intVar(nameLesson(lesson) + "-VarSlot", 1, nbSlots));
				idML ++;
			}
	}
	
	public int getNumberOfSlots(Calendar cal) {
		return services.getCalendarService().getNumberOfSlots(cal.getId());
	}
	
	public int getNumberOfLessons(TAF taf) {
		return services.getTafService().numberOfLessons(taf.getId());
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
		while (testiLengths(iSlots, lengthSlots)) {
			List<Integer> iMins = new ArrayList<Integer>();
			for (int i = 1; i < cals.length; i ++) {
				if (iSlots[i] < lengthSlots[i]) {
					if (iMins.size() == 0) {
						iMins.add(i);
					}
					else {
						int comparison = slots.get(i).get(iSlots[i]).compareTo(slots.get(iMins.get(0)).get(iSlots[iMins.get(0)]));
						if (comparison < 0) {
							iMins.clear();
							iMins.add(i);
						}
						if (comparison == 0) {
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
	
	private static boolean testiLengths(int[] is, int[] lengths) {
		for (int i = 0; i < is.length; i ++)
			if (is[i] < lengths[i])
				return true;
		return false;
	}

	public void setConstraints(Model model, Calendar cal) {
		setConstraintLinkLessonsSlots(model, cal);
		if (this.IdMSlotGlobal != null) setConstraintLinkSlotGlobalDayWeek(model, cal, true, false, false);
		/*if (cal.hasConstraintGlobalUnavailability()*/ setConstraintGlobalUnavailability(model, cal);
		/*if (cal.hasConstraint1())*/ setConstraintLecturerUnavailability(model, cal);
	}
	
	private void setConstraintLinkLessonsSlots(Model model, Calendar cal) {
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
	
	private void setConstraintLinkSlotGlobalDayWeek(Model model, Calendar cal, boolean global, boolean day, boolean week) {
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
		
		if (global) addGlobalListsConstraintLink(model, cal, lessons, slots, lessonsV, sortedLessonsV);
		if (day) addDayListsConstraintLink(model, cal, lessons, slots, lessonsV, sortedLessonsV);
		if (week) addWeekListsConstraintLink(model, cal, lessons, slots, lessonsV, sortedLessonsV);
		
		IntVar[] permutations = model.intVarArray(nbSlots, 1, nbSlots);
		
		model.keySort(lessonsV.toArray(IntVar[][]::new), permutations, sortedLessonsV.toArray(IntVar[][]::new), 1).post();
	}
	
	private void addGlobalListsConstraintLink(Model model, Calendar cal, Lesson[] lessons, Slot[] slots, List<List<IntVar>> lessonsV, List<List<IntVar>> sortedLessonsV) {
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
	
	private void addDayListsConstraintLink(Model model, Calendar cal, Lesson[] lessons, Slot[] slots, List<List<IntVar>> lessonsV, List<List<IntVar>> sortedLessonsV) {
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
	
	private void addWeekListsConstraintLink(Model model, Calendar cal, Lesson[] lessons, Slot[] slots, List<List<IntVar>> lessonsV, List<List<IntVar>> sortedLessonsV) {
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

	public void setConstraintGlobalUnavailability(Model model, Calendar cal) {
		for (Slot slot : services.getCalendarService().getSlotsOrdered(cal.getId()))
			if (services.getGlobalUnavailabilityService().findBySlot(slot).filter(g -> g.getStrict()).isPresent())
				model.arithm(this.getSlotVarLesson(slot), "=", 0).post();
	}

	public void setConstraintLecturerUnavailability(Model model, Calendar cal) {
		for (UE ue : cal.getTaf().getUes())
			for (Lesson lesson : ue.getLessons())
				for (Slot slot : services.getLessonService().findLessonLecturersUnavailabilitiesByLessonAndCalendar(lesson, cal))
					model.arithm(getLessonVarSlot(lesson), "!=", getIdMSlot(slot)).post();					
	}
	
	
	public IntVar setPreferences(Model model, Calendar cal) {
		
		return null;
	}
	
	public static void setStrategy(SolverMain solMain, Solver solver, Calendar cal) {
		IntVar[] decisionVars = solMain.getDecisionVars(cal);
		solver.setSearch(Search.minDomLBSearch(decisionVars));
		//solver.setSearch(Search.minDomUBSearch(decisionVars));
	}
	
	public static void setStrategy(SolverMain[] solMains, Solver solver, Calendar[] cals) {
		IntVar[] decisionVars = ArrayUtils.flatten(IntStream.range(0, cals.length).
									mapToObj(i -> solMains[i].getDecisionVars(cals[i])).toArray(IntVar[][]::new));  
		solver.setSearch(Search.minDomLBSearch(decisionVars));
		//solver.setSearch(Search.minDomUBSearch(getVarDecisionSlots(cal)));
	}
	
	public IntVar[] getDecisionVars(Calendar cal) {
		return getVarDecisionSlots(cal);
	}
	
	public IntVar[] getVarDecisionSlots(Calendar cal) {
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
	
	public String makeSolutionString(Calendar cal, Solution solution) {
		StringBuilder res = new StringBuilder();
		res.append("{\"slots\":[");
		cal.getSlots().stream().forEach(s -> res.append("{\"id\":" + s.getId() + (solution.getIntVal(getSlotVarLesson(s)) != 0 ? ",\"lessonId\":" + this.getIdLesson(solution.getIntVal(getSlotVarLesson(s))) :  "") + "},"));
		res.deleteCharAt(res.length()-1);
		res.append("]}");
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
