package com.planify.server.solver;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;

import com.planify.server.models.Calendar;
import com.planify.server.models.Lesson;
import com.planify.server.models.LessonLecturer;
import com.planify.server.models.Slot;
import com.planify.server.models.TAF;
import com.planify.server.models.UE;
import com.planify.server.models.User;
import com.planify.server.models.UserUnavailability;

public class SolverMain {
	public static void generateTaf(Calendar cal) {
		Model model = new Model();
		Solver solver = model.getSolver();
		int nbSlots = 5;
		int nbLessons = 3;
		cal.initialiseVars(model, nbSlots, nbLessons);
		setConstraints(model, cal);
		IntVar obj = setPreferences(model, cal);
		setStrategy(solver, cal);
		if (obj != null) solver.findOptimalSolution(obj, false);
		else solver.findSolution();
		System.out.println(model);
		solver.printShortStatistics();
	}
	
	public static void setConstraints(Model model, Calendar cal) {
		setConstraintLinkLessonsSlots(model, cal);
		/*if (cal.hasConstraint1())*/ //setConstraintLecturerUnavailability(model, cal);
	}
	
	private static void setConstraintLinkLessonsSlots(Model model, Calendar cal) {
		//Slot[] slots = (Slot[]) cal.getSlots().toArray();
		List<Slot> slotsL = cal.getSlots();
		Slot[] slots = new Slot[slotsL.size()];
		for (int i = 0; i < slotsL.size(); i ++) slots[i] = slotsL.get(i);
		//Lesson[] lessons = (Lesson[]) cal.getTaf().getLessons().toArray();
		List<Lesson> lessonsL = cal.getTaf().getLessons();
		Lesson[] lessons = new Lesson[lessonsL.size()];
		for (int i = 0; i < lessonsL.size(); i ++) lessons[i] = lessonsL.get(i);
		int nbSlots = slots.length;
		int nbLessons = lessons.length;
		IntVar[][] slotsV = new IntVar[nbSlots][1];
		for (int i = 0; i < nbSlots; i ++) slotsV[i][0] = slots[i].getVarLesson();
		IntVar[] lessonsV = new IntVar[nbSlots];
		IntVar[][] sortedSlotsV = new IntVar[nbSlots][1];
		IntVar zero = model.intVar("zero", 0);
		for (int i = 0; i < nbSlots - nbLessons; i ++) {
			lessonsV[i] = model.intVar("DummyKeySort1 " + i, 1, nbSlots);
			sortedSlotsV[i][0] = zero;
		}
		for (int i = nbSlots - nbLessons; i < nbSlots; i ++) {
			lessonsV[i] = lessons[i - nbSlots + nbLessons].getVarSlot();
			sortedSlotsV[i][0] = model.intVar("SortedSlotsV " + i, i - nbSlots + nbLessons + 1);
		}
		model.keySort(slotsV, lessonsV, sortedSlotsV, 1).post();
	}

	public static void setConstraintLecturerUnavailability(Model model, Calendar cal) {
		for (UE ue : cal.getTaf().getUes())
			for (Lesson lesson : ue.getLessons())
				/*for (Slot slot : lesson.getLecturersUnavailability(cal))
					model.arithm(lesson.getVarSlot(), "!=", slot.getIdM());*/
				for (LessonLecturer lessonLecturer : lesson.getLessonLecturers())
					for (UserUnavailability userUnavailability : lessonLecturer.getUser().getUserUnavailabilities())
						model.arithm(lesson.getVarSlot(), "!=", userUnavailability.getSlot().getIdM()).post();
					
	}
	
	
	public static IntVar setPreferences(Model model, Calendar cal) {
		
		return null;
	}
	
	public static void setStrategy(Solver solver, Calendar cal) {
		//solver.setSearch(Search.minDomLBSearch(getVarDecisionSlots(cal)));
		solver.setSearch(Search.minDomUBSearch(getVarDecisionSlots(cal)));
	}
	
	public static IntVar[] getVarDecisionSlots(Calendar cal) {
		List<IntVar> vars = new ArrayList<IntVar>();
		vars.addAll(cal.getSlots().stream().map(s -> s.getVarLesson()).toList());
		IntVar[] varsArr = convertListToArrayIntVar(vars);
		return varsArr;
	}
	
	public static IntVar[] convertListToArrayIntVar(List<IntVar> list){
		IntVar[] arr = new IntVar[list.size()];
		for (int i = 0; i < list.size(); i ++) arr[i] = list.get(i);
		return arr;
	}
	
	public static void main(String[] args) {
		//test0();
		test1();
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
		SolverMain.generateTaf(cal);
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
		
		SolverMain.generateTaf(cal);
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
