package com.planify.server.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
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

@Component
public class SolverMain {
	//Services
	@Lazy
    @Autowired
	private CalendarService calendarService;
	
	@Lazy
    @Autowired
	private TAFService tafService;
	
	@Lazy
    @Autowired
	private LessonService lessonService;
	
	private BijectiveHashMap<Long, Integer> idMSlot;
	private HashMap<Long, IntVar> slotVarLesson;
	private BijectiveHashMap<Long, Integer> idMLesson;
	private HashMap<Long, IntVar> lessonVarSlot;
	private BijectiveHashMap<Long, Integer> idMUe;
	private BijectiveHashMap<Long, Integer> idMDay;
	private BijectiveHashMap<Long, Integer> idMWeek;
	

	public SolverMain() {
		idMSlot = new BijectiveHashMap<Long, Integer>();
		slotVarLesson = new HashMap<Long, IntVar>();
		idMLesson = new BijectiveHashMap<Long, Integer>();
		lessonVarSlot = new HashMap<Long, IntVar>();
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
	
	public Integer getIdMLesson(Lesson lesson) {return idMLesson.getValue(lesson.getId());}
	public Integer[] getIdMLesson(Lesson[] lessons) {return IntStream.range(0, lessons.length).mapToObj(i -> getIdMLesson(lessons[i])).toArray(Integer[]::new);}
	public Long getIdLesson(Integer idM) {return idMLesson.getKey(idM);}
	public Long[] getIdLesson(Integer[] idMs) {return IntStream.range(0, idMs.length).mapToObj(i -> getIdLesson(idMs[i])).toArray(Long[]::new);}
	
	public IntVar getLessonVarSlot(Lesson lesson) {return lessonVarSlot.get(lesson.getId());}
	public IntVar[] getLessonVarSlot(Lesson[] lessons) {return IntStream.range(0, lessons.length).mapToObj(i -> getLessonVarSlot(lessons[i])).toArray(IntVar[]::new);}
	
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
	
	
	public void generateCal(Calendar cal) {
		Model model = new Model();
		Solver solver = model.getSolver();
		int nbSlots = calendarService.getNumberOfSlots(cal.getId());
		int nbLessons = tafService.numberOfLessons(cal.getTaf().getId());
		initialiseVars(model, cal, nbSlots, nbLessons);
		setConstraints(model, cal);
		IntVar obj = setPreferences(model, cal);
		setStrategy(solver, cal);
		if (obj != null) solver.findOptimalSolution(obj, false);
		else solver.findSolution();
		System.out.println(model);
		solver.printShortStatistics();
		System.out.println(makeSolutionString(cal));
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
		for (Week week : calendarService.getWeeksSorted(cal.getId())) {
			idMWeek.put(week.getId(), idMW);
			idMW ++;
		}
	}
	
	private void initialiseDays(Calendar cal) {
		Integer idMD = 1;
		for (Day day : calendarService.getDaysSorted(cal.getId())) {
			idMDay.put(day.getId(), idMD);
			idMD ++;
		}
	}
	
	private void initialiseSlots(Model model, Calendar cal, int nbLessons) {
		Integer idMS = 1;
		for (Slot slot : calendarService.getSlotsOrdered(cal.getId())) {
			idMSlot.put(slot.getId(), idMS);
			slotVarLesson.put(slot.getId(), model.intVar("Slot " + idMS + " (" + slot.getId() + ")-VarLesson", 0,nbLessons));
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
				lessonVarSlot.put(lesson.getId(), model.intVar("Lesson " + idML + " (" + lesson.getId() + ")-VarSlot", 1, nbSlots));
				idML ++;
			}
	}

	public void setConstraints(Model model, Calendar cal) {
		setConstraintLinkLessonsSlots(model, cal);
		/*if (cal.hasConstraint1())*/ //setConstraintLecturerUnavailability(model, cal);
		 setConstraintLecturerPreferences(model, cal);
	}
	
	private void setConstraintLinkLessonsSlots(Model model, Calendar cal) {
		Slot[] slots = calendarService.getSlotsOrdered(cal.getId()).stream().toArray(Slot[]::new);
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

	public void setConstraintLecturerUnavailability(Model model, Calendar cal) {
		for (UE ue : cal.getTaf().getUes())
			for (Lesson lesson : ue.getLessons())
				for (Slot slot : lessonService.findLessonLecturersUnavailabilitiesByLessonAndCalendar(lesson, cal))
					model.arithm(getLessonVarSlot(lesson), "!=", getIdMSlot(slot)).post();					
	}
	
	public IntVar setConstraintLecturerPreferences(Model model, Calendar cal) {
		
		IntVar notPreferredAllocations = model.intVar("NotPreferredAllocations", 0, Integer.MAX_VALUE);
		ArrayList<IntVar> isNotPreferredVars = new ArrayList<IntVar>();

		for (UE ue : cal.getTaf().getUes()) {
		    for (Lesson lesson : ue.getLessons()) {
		    	
		    	for (Slot slot : lessonService.findNotPreferedSlotsByLessonAndCalendar(lesson, cal)) {
		    		BoolVar isNotPreferredVar = model.boolVar("NotPreferred_" + lesson.getId() + "_" + slot.getId());
		    		model.reification(isNotPreferredVar, model.arithm(getLessonVarSlot(lesson), "=", getIdMSlot(slot)));
		    		isNotPreferredVars.add(isNotPreferredVar);
		    	}		    	
		    }		    
		}   
		model.sum(isNotPreferredVars.stream().toArray(IntVar[]::new), "=", notPreferredAllocations).post();
		return notPreferredAllocations;
	}
	
	
	public IntVar setPreferences(Model model, Calendar cal) {
		return setConstraintLecturerPreferences( model, cal);
		//return null;
	}
	
	public void setStrategy(Solver solver, Calendar cal) {
		solver.setSearch(Search.minDomLBSearch(getVarDecisionSlots(cal)));
		//solver.setSearch(Search.minDomUBSearch(getVarDecisionSlots(cal)));
		
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
	
	public String makeSolutionString(Calendar cal) {
		StringBuilder res = new StringBuilder();
		res.append("{\"slots\":[");
		cal.getSlots().stream().forEach(s -> res.append("{\"id\":" + s.getId() + (getSlotVarLesson(s).getValue() != 0 ? ",\"lessonId\":" + this.getIdLesson(getSlotVarLesson(s).getValue()) :  "") + "},"));
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
