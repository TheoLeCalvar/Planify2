package com.planify.server.solver;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

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
	@Lazy
    @Autowired
	private CalendarService calendarService;
	
	@Lazy
    @Autowired
	private TAFService tafService;
	
	@Lazy
    @Autowired
	private LessonService lessonService;
	
	
	public SolverMain() {}
	
	public static void generateCalendar(Calendar[] cals) {
		for (Calendar cal : cals) {
			generateCalendar(cal, Arrays.stream(cals).filter(c -> c != cal).toArray(Calendar[]::new));
		}
	}
	
	public static void generateCalendar(Calendar cal, Calendar[] otherCalendars) {
		(new SolverMain()).generateCal(cal, otherCalendars);
	}
	
	public static void generateCalendar(Calendar cal) {
		generateCalendar(cal, new Calendar[0]);
	}
	
	private BijectiveHashMap<Long, Integer> idMSlot;
	private HashMap<Long, IntVar> slotVarLesson;
	private BijectiveHashMap<Long, Integer> idMLesson;
	private HashMap<Long, IntVar> lessonVarSlot;
	private BijectiveHashMap<Long, Integer> idMUe;
	private BijectiveHashMap<Long, Integer> idMDay;
	private BijectiveHashMap<Long, Integer> idMWeek;
	
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
	
	
	public void generateCal(Calendar cal, Calendar[] otherCalendars) {
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
			slotVarLesson.put(slot.getId(), model.intVar("Slot " + idMS + "-VarLesson", 0,nbLessons));
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
	
	
	public IntVar setPreferences(Model model, Calendar cal) {
		
		return null;
	}
	
	public void setStrategy(Solver solver, Calendar cal) {
		//solver.setSearch(Search.minDomLBSearch(getVarDecisionSlots(cal)));
		solver.setSearch(Search.minDomUBSearch(getVarDecisionSlots(cal)));
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
	
	public static void main(String[] args) {
		//test0();
		//test1();
	}
	
	public static void test0() {
		Calendar cal = new Calendar();
		cal.setSlots(IntStream.range(0, 5).mapToObj(i -> new Slot()).toList());
		cal.setTaf(new TAF());
		cal.getTaf().setUes(IntStream.range(0, 2).mapToObj(i -> new UE()).toList());
		cal.getTaf().getUes().get(0).setLessons(IntStream.range(0, 2).mapToObj(i -> new Lesson()).toList());
		cal.getTaf().getUes().get(0).getLessons().forEach(l -> l.setUe(cal.getTaf().getUes().get(0)));
		cal.getTaf().getUes().get(1).setLessons(IntStream.range(0, 1).mapToObj(i -> new Lesson()).toList());
		cal.getTaf().getUes().get(1).getLessons().forEach(l -> l.setUe(cal.getTaf().getUes().get(1)));
		SolverMain.generateCalendar(cal);
	}
	
	public static void test1() {
		Calendar cal = new Calendar();
		List<Slot> slots= IntStream.range(0, 5).mapToObj(i -> new Slot()).toList();
		cal.setSlots(slots);
		cal.setTaf(new TAF());
		List<UE> ues = IntStream.range(0, 2).mapToObj(i -> new UE()).toList();
		cal.getTaf().setUes(ues);
		List<Lesson> lessonsUe1 = IntStream.range(0, 2).mapToObj(i -> new Lesson()).toList();
		lessonsUe1.forEach(l -> l.setUe(ues.get(0)));
		ues.get(0).setLessons(lessonsUe1);
		List<Lesson> lessonsUe2 = IntStream.range(0, 1).mapToObj(i -> new Lesson()).toList();
		lessonsUe2.forEach(l -> l.setUe(ues.get(1)));
		ues.get(1).setLessons(lessonsUe2);
		List<User> users = IntStream.range(0, 2).mapToObj(i -> new User("a", "a", "a", new char[0])).toList();
		List<LessonLecturer> lessonLecturers = new ArrayList<LessonLecturer>();
		lessonLecturers.add(new LessonLecturer(users.get(0), lessonsUe1.get(0)));
		lessonLecturers.add(new LessonLecturer(users.get(1), lessonsUe1.get(0)));
		lessonLecturers.add(new LessonLecturer(users.get(0), lessonsUe1.get(1)));
		lessonLecturers.add(new LessonLecturer(users.get(2), lessonsUe2.get(0)));
		users.get(0).setLessonLecturers(IntStream.range(0, 2).mapToObj(i -> lessonLecturers.get(i * 2)).toList());
		lessonsUe1.get(0).setLessonLecturers(IntStream.range(0, 2).mapToObj(i -> lessonLecturers.get(i)).toList());
		users.get(1).setLessonLecturers(IntStream.range(0, 1).mapToObj(i -> lessonLecturers.get(1)).toList());
		lessonsUe1.get(1).setLessonLecturers(IntStream.range(0, 1).mapToObj(i -> lessonLecturers.get(2)).toList());
		users.get(2).setLessonLecturers(IntStream.range(0, 1).mapToObj(i -> lessonLecturers.get(3)).toList());
		lessonsUe2.get(0).setLessonLecturers(IntStream.range(0, 1).mapToObj(i -> lessonLecturers.get(3)).toList());
		List<UserUnavailability> unavailabilitySlot1 = new ArrayList<UserUnavailability>();
		List<UserUnavailability> unavailabilitySlot2 = new ArrayList<UserUnavailability>();
		List<UserUnavailability> unavailabilitySlot3 = new ArrayList<UserUnavailability>();
		List<UserUnavailability> unavailabilityUser1 = new ArrayList<UserUnavailability>();
		unavailabilityUser1.add(new UserUnavailability(slots.get(0), users.get(0), true));
		unavailabilitySlot1.add(unavailabilityUser1.getLast());
		unavailabilityUser1.add(new UserUnavailability(slots.get(1), users.get(0), true));
		unavailabilitySlot2.add(unavailabilityUser1.getLast());
		users.get(0).setUserUnavailabilities(unavailabilityUser1);
		List<UserUnavailability> unavailabilityUser2 = new ArrayList<UserUnavailability>();
		unavailabilityUser2.add(new UserUnavailability(slots.get(0), users.get(1), true));
		unavailabilitySlot1.add(unavailabilityUser1.getLast());
		unavailabilityUser2.add(new UserUnavailability(slots.get(2), users.get(1), true));
		unavailabilitySlot3.add(unavailabilityUser1.getLast());
		users.get(1).setUserUnavailabilities(unavailabilityUser2);
		List<UserUnavailability> unavailabilityUser3 = new ArrayList<UserUnavailability>();
		unavailabilityUser3.add(new UserUnavailability(slots.get(0), users.get(2), true));
		unavailabilitySlot1.add(unavailabilityUser1.getLast());
		unavailabilityUser3.add(new UserUnavailability(slots.get(1), users.get(2), true));
		unavailabilitySlot2.add(unavailabilityUser1.getLast());
		users.get(1).setUserUnavailabilities(unavailabilityUser2);
		slots.get(0).setUserUnavailabilities(unavailabilitySlot1);
		slots.get(1).setUserUnavailabilities(unavailabilitySlot2);
		slots.get(2).setUserUnavailabilities(unavailabilitySlot3);
		
		SolverMain.generateCalendar(cal);
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
