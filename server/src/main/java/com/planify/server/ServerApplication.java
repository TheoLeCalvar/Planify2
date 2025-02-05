package com.planify.server;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.planify.server.models.*;
import com.planify.server.models.Antecedence.AntecedenceId;
import com.planify.server.models.LessonLecturer.LessonLecturerId;
import com.planify.server.models.Sequencing.SequencingId;
import com.planify.server.models.Synchronization.SynchronizationId;
import com.planify.server.models.TAFManager.TAFManagerId;
import com.planify.server.models.UEManager.UEManagerId;
import com.planify.server.models.UserUnavailability.UserUnavailabilityId;
import com.planify.server.service.*;
import com.planify.server.solver.SolverExecutor;
import com.planify.server.solver.SolverMain;
import com.planify.server.solver.SolverServices;

@SpringBootApplication(scanBasePackages = "com.planify.server")
public class ServerApplication {
	
	private static AntecedenceService antecedenceService;
	private static BlockService blockService;
	private static CalendarService calendarService;
	private static DayService dayService;
	private static GlobalUnavailabilityService globalUnavailabilityService;
	private static LessonLecturerService lessonLecturerService;
	private static LessonService lessonService;
	private static SequencingService sequencingService;
	private static SlotService slotService;
	private static SynchronizationService synchronizationService;
	private static TAFManagerService tafManagerService;
	private static TAFService tafService;
	private static UEManagerService ueManagerService;
	private static UEService ueService;
	private static UserService userService;
	private static UserUnavailabilityService userUnavailabilityService;
	private static WeekService weekService;
	private static PlanningService planningService;
	
	private static final String RESET = "\u001B[0m";
	private static final String RED = "\u001B[31m";
	private static final String GREEN = "\u001B[32m";
	
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ServerApplication.class, args);

		SolverMain.setServices(context.getBean(SolverServices.class));

		

		// Creation of the services
		antecedenceService = context.getBean(AntecedenceService.class);
		blockService = context.getBean(BlockService.class);
		calendarService = context.getBean(CalendarService.class);
		dayService = context.getBean(DayService.class);
		globalUnavailabilityService = context.getBean(GlobalUnavailabilityService.class);
		lessonLecturerService = context.getBean(LessonLecturerService.class);
		lessonService = context.getBean(LessonService.class);
		sequencingService = context.getBean(SequencingService.class);
		slotService = context.getBean(SlotService.class);
		synchronizationService = context.getBean(SynchronizationService.class);
		tafManagerService = context.getBean(TAFManagerService.class);
		tafService = context.getBean(TAFService.class);
		ueManagerService = context.getBean(UEManagerService.class);
		ueService = context.getBean(UEService.class);
		userService = context.getBean(UserService.class);
		userUnavailabilityService = context.getBean(UserUnavailabilityService.class);
		weekService = context.getBean(WeekService.class);
		planningService = context.getBean(PlanningService.class);
		
		/*
		TAF taf = tafService.addTAF("LOGIN", "Polyglotte", "début", "fin");
		Calendar calendar = calendarService.addCalendar(taf);
		Planning planning = planningService.addPlanning(calendar);
		System.out.println(GREEN + planning.toString() + RESET);

		Slot slot = slotService.add(1, dayService.addDay(1, weekService.addWeek(1, 2025)),calendar);
		slot.setEnd(LocalDateTime.of(2025,1,17,18,0));
		slot.setStart(LocalDateTime.of(2025,1,17,15,0));
		slotService.save(slot);

		Lesson lesson = lessonService.add("cours 1", "début petrinet", ueService.addUE("MAPD", "petrinet", taf));

		List<Result> results = new ArrayList<>();
		results.add(new Result(slot.getId(), lesson.getId()));
		planningService.addScheduledLessons(planning, results);
		List<ScheduledLesson> scheduledLessons = planning.getScheduledLessons();
		for (ScheduledLesson sl: scheduledLessons) {
			System.out.println(GREEN + sl.toString() + RESET);
		}*/


		
		/*System.out.println("Test !!!!!!!!!!!!!!");
		TAF taf0 = tafService.addTAF(GREEN, RESET, RED, GREEN);
		UE ue0 = ueService.addUE(RED, GREEN, taf0);
		Lesson lesson0 = lessonService.add(RED, GREEN, ue0);
		User user0 = userService.addUser(RESET, RED, GREEN, new char[] {});
		LessonLecturer lessonLecturer0 = lessonLecturerService.addLessonLecturer(user0, lesson0);
		lessonService.delete(lesson0.getId());
		Lesson lesson00 = lessonService.add(RED, GREEN, ue0);
		LessonLecturer lessonLecturer1 = lessonLecturerService.addLessonLecturer(user0, lesson00);
		System.out.println("Nb lecturers : " + lesson00.getLessonLecturers().size());
		lessonLecturerService.deleteLessonLecturer(lessonLecturer1.getId());
		System.out.println("Nb lecturers : " + lesson00.getLessonLecturers().size());
		System.out.println("Nb lecturers : " + lessonService.findById(lesson00.getId()).get().getLessonLecturers().size());
		System.out.println("Fin Test !!!!!!!!!!");
		*/
		testSolver(context);
		if (weekService != null) return; //Just to not have warnings when we want to stops tests here.
		/*
		// Test of calendarService.getSlotsOrdered(idCalendar), getNumberOfSlots,
		// getDaysSorted
		tafService.addTAF("DCL", "Développement", "2024-09-07", "2025-03-30");
		tafService.addTAF("LOGIN", "info", "2024-09-07", "2025-03-30");
		tafService.addTAF("TEE", "Environnement", "2024-09-07", "2025-03-30");
		List<TAF> listTafs = tafService.findByName("DCL");
		TAF dcl = listTafs.get(0);
		Calendar c = calendarService.addCalendar(dcl);
		Week week1 = weekService.addWeek(1, 2025);
		Week week2 = weekService.addWeek(2, 2025);
		Day day11 = dayService.addDay(1, week1);
		Day day12 = dayService.addDay(2, week1);
		Day day21 = dayService.addDay(1, week2);
		Day day22 = dayService.addDay(2, week2);
		slotService.add(1, day11, c);
		slotService.add(2, day11, c);
		slotService.add(1, day12, c);
		slotService.add(1, day21, c);
		slotService.add(2, day21, c);
		slotService.add(1, day22, c);
		List<Slot> list = calendarService.getSlotsOrdered(c.getId());
		for (Slot s : list) {
			System.out.println("'Slot'" + "" + s.getDay().getWeek().getYear() + "" + s.getDay().getWeek().getNumber()
					+ "" + s.getDay().getNumber() + "" + s.getNumber());
		}
		System.out.println("Nombre = " + calendarService.getNumberOfSlots(c.getId()));
		List<Day> daysofCalendar = calendarService.getDaysSorted(c.getId());
		for (Day day : daysofCalendar) {
			System.out.println(day.toString());
		}
		List<Week> weeksOfCalendar = calendarService.getWeeksSorted(c.getId());
		System.out.println("Les semaines:");
		for (Week week : weeksOfCalendar) {
			System.out.println(week.toString());
		}

		// Test of NumberOfLesson in a TAF
		UE ue1 = ueService.addUE("UE1", "desc", dcl); // Assuming addUE adds UE linked to TAF
		UE ue2 = ueService.addUE("UE2", "desc", dcl);
		UE ue3 = ueService.addUE("UE3", "desc", dcl);
		lessonService.add("Lesson1", "description", ue1);
		lessonService.add("Lesson2", "description", ue1);
		lessonService.add("Lesson3", "description", ue2);
		lessonService.add("Lesson4", "description", ue2);
		lessonService.add("Lesson5", "description", ue3);
		System.out.println("Number of lesson in TAF dcl = " + "" + tafService.numberOfLessons(dcl.getId()));
		List<Lesson> lessonsOfTAF = tafService.getLessonsOfTAF(dcl.getId());
		for (Lesson l : lessonsOfTAF) {
			System.out.println(l.toString());
		}

		// Test of week
		System.out.println("Test of week");
		weekService.addWeek(5, 2025);
		List<Week> weeks = weekService.findByNumber(5);
		if (!weeks.isEmpty()) {
			System.out.println(GREEN + "Week added with success:" + RESET);
			System.out.println(weeks.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! Week not added" + RESET);
		}
		Long idWeek = weeks.get(0).getId();
		weekService.deleteWeek(idWeek);
		weeks = weekService.findByNumber(5);
		if (!weeks.isEmpty()) {
			System.out.println(RED + "PB!!! Week not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "Week deleted with success" + RESET);
		}
		weekService.addWeek(5, 2025);
		Week week = weekService.findByNumber(5).get(0);

		// Test of day
		System.out.println("Test of day");
		dayService.addDay(1, week);
		List<Day> days = dayService.findByWeek(week);
		if (!days.isEmpty()) {
			System.out.println(GREEN + "Day added with success:" + RESET);
			System.out.println(days.get(0).toString());
			System.out.println(weekService.findByNumber(5).get(0).getDays());
		} else {
			System.out.println(RED + "PB!!! Day not added" + RESET);
		}
		Long idDay = days.get(0).getId();
		dayService.deleteDay(idDay);
		days = dayService.findByWeek(week);
		if (!days.isEmpty()) {
			System.out.println(RED + "PB!!! Day not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "Day deleted with success" + RESET);
		}
		dayService.addDay(1, week);

		// Test of TAF
		System.out.println("Test of TAF");
		int count = tafService.findAll().size();
		tafService.addTAF("DCL", "desc", "2024-05-12", "2025-03-30");

		List<TAF> tafs = tafService.findByName("DCL");
		if (tafService.findAll().size() == count + 1) {
			System.out.println(GREEN + "TAF added with success:" + RESET);
			System.out.println(tafs.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! TAF not added" + RESET);
		}
		Long idTaf = tafs.get(0).getId();
		tafService.deleteTAF(idTaf);
		tafs = tafService.findByName("DCL");
		if (tafService.findAll().size() != count) {
			System.out.println(RED + "PB!!! TAF not deleted:" + GREEN);
		} else {
			System.out.println(GREEN + "TAF deleted with success" + RESET);
		}
		tafService.addTAF("DCL", "desc", "2024-07-20", "2025-02-14");
		tafs = tafService.findByName("DCL");
		TAF taf = tafs.get(0);

		// Test of Calendar
		System.out.println("Test of Calendar");
		calendarService.addCalendar(taf);

		List<Calendar> calendars = calendarService.findAll();
		if (!calendars.isEmpty()) {
			System.out.println(GREEN + "Calendar added with success:" + RESET);
			System.out.println(calendars.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! Calendar not added" + RESET);
		}
		Long idCalendar = calendars.get(0).getId();
		calendarService.deleteCalendar(idCalendar);
		calendars = calendarService.findAll();
		if (!calendars.isEmpty()) {
			System.out.println(RED + "PB!!! Calendar not deleted:" + GREEN);
		} else {
			System.out.println(GREEN + "Calendar deleted with success" + RESET);
		}
		tafService.addTAF("LOGIN", "desc", "2000-01-01", "2004-05-04");
		TAF login = tafService.findByName("LOGIN").get(0);
		calendarService.addCalendar(login);

		// Test of Slot
		System.out.println("Test of Slot");
		slotService.add(1, dayService.findByWeek(week).get(0), calendarService.findAll().get(0));

		List<Slot> slots = slotService.findAll();
		if (!slots.isEmpty()) {
			System.out.println(GREEN + "Slot added with success:" + RESET);
			System.out.println(slots.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! Slot not added" + RESET);
		}
		Long idSlot = slots.get(0).getId();
		slotService.deleteSlot(idSlot);
		slots = slotService.findAll();
		if (!slots.isEmpty()) {
			System.out.println(RED + "PB!!! Slot not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "Slot deleted with success" + RESET);
		}

		// Test of User
		System.out.println("Test of User");
		userService.addUser("John", "Doe", "mail", null);

		List<User> users = userService.findAll();
		if (!users.isEmpty()) {
			System.out.println(GREEN + "User added with success:" + RESET);
			System.out.println(users.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! User not added" + RESET);
		}
		Long idUser = users.get(0).getId();
		userService.deleteUser(idUser);
		users = userService.findAll();
		if (!users.isEmpty()) {
			System.out.println(RED + "PB!!! User not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "User deleted with success" + RESET);
		}

		// Test of GlobalUnavailability
		System.out.println("Test of GlobalUnavailability");
		slotService.add(1, dayService.findByWeek(week).get(0), calendarService.findAll().get(0));
		Slot slot = slotService.findAll().get(0);
		globalUnavailabilityService.addGlobalUnavailability(true, slot);

		List<GlobalUnavailability> globalUnavailabilities = globalUnavailabilityService.findAll();
		if (!globalUnavailabilities.isEmpty()) {
			System.out.println(GREEN + "GlobalUnavailability added with success:" + RESET);
			System.out.println(globalUnavailabilities.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! GlobalUnavailability not added" + RESET);
		}
		Long idGlobalUnavailability = globalUnavailabilities.get(0).getId();
		globalUnavailabilityService.deleteGlobalUnavailability(idGlobalUnavailability);
		globalUnavailabilities = globalUnavailabilityService.findAll();
		if (!globalUnavailabilities.isEmpty()) {
			System.out.println(RED + "PB!!! GlobalUnavailability not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "GlobalUnavailability deleted with success" + RESET);
		}

		userService.addUser("John", "Doe", "mail", null);
		User user = userService.findAll().get(0);

		// Test of UserUnavailability
		System.out.println("Test of UserUnavailability");

		slotService.add(2, dayService.findByWeek(week).get(0), calendarService.findAll().get(0));
		Slot userSlot = slotService.findAll().get(0);

		System.out.println(RED + " SLOT :" + userSlot + RESET);
		System.out.println(RED + "SLOT ID : " + userSlot.getId() + RESET);
		System.out.println(RED + "USER ID : " + user.getId() + RESET);

		userUnavailabilityService.addUserUnavailability(userSlot, user, true);

		System.out.println("-----------------------CHECK4---------------------");

		List<UserUnavailability> userUnavailabilities = userUnavailabilityService.findBySlot(userSlot);
		if (!userUnavailabilities.isEmpty()) {
			System.out.println(GREEN + "UserUnavailability added with success:" + RESET);
			System.out.println(userUnavailabilities.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! UserUnavailability not added" + RESET);
		}
		UserUnavailabilityId idUserUnavailability = userUnavailabilities.get(0).getId();
		userUnavailabilityService.deleteUserUnavailability(idUserUnavailability);
		userUnavailabilities = userUnavailabilityService.findBySlot(userSlot);
		if (!userUnavailabilities.isEmpty()) {
			System.out.println(RED + "PB!!! UserUnavailability not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "UserUnavailability deleted with success" + RESET);
		}

		// Test of UE
		System.out.println("Test of UE");
		List<UE> ues = ueService.findAll();
		int countUes = ues.size();
		System.out.println(RED + "LIST UES :" + ues + countUes + RESET);
		TAF ihm = tafService.addTAF("IHM", "desc", "2003-03-03", "2040-03-17");
		UE ue = ueService.addUE("UE-Test-uetest", "desc", ihm);
		System.out.println(RED + "TAF :" + ihm + RESET);

		if (countUes + 1 == ueService.findAll().size()) {
			System.out.println(GREEN + "UE added with success:" + RESET);
			System.out.println(ueService.findAll());
		} else {
			System.out.println(RED + "PB!!! UE not added" + RESET);
		}
		ueService.deleteUE(ue.getId());
		ues = ueService.findAll();

		if (countUes != ueService.findAll().size()) {
			System.out.println(RED + "PB!!! UE not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "UE deleted with success" + RESET);
		}
		ue = ueService.addUE("UE-abc", "description", ihm);

		// Test of Lesson
		System.out.println("Test of Lesson");
		UE ue_2 = ueService.addUE("UE1", "desc", ihm); // Adding a UE for the lesson
		System.out.println(RED + "UE :" + ue_2 + RESET);
		Lesson lesson = lessonService.add("Lesson1", "description", ue_2);

		List<Lesson> lessons = lessonService.findAll();
		if (!lessons.isEmpty()) {
			System.out.println(GREEN + "Lesson added with success:" + RESET);
			System.out.println(lessons.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! Lesson not added" + RESET);
		}
		lessonService.delete(lesson.getId());
		lessons = lessonService.findAll();
		if (!lessons.isEmpty()) {
			System.out.println(RED + "PB!!! Lesson not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "Lesson deleted with success" + RESET);
		}

		// Test of LessonLecturer
		System.out.println("Test of LessonLecturer");
		Lesson lesson1 = lessonService.add("name lesson 1", "description", ue_2);

		lessonLecturerService.addLessonLecturer(user, lesson1);

		List<LessonLecturer> lessonLecturers = lessonLecturerService.findAll();
		if (!lessonLecturers.isEmpty()) {
			System.out.println(GREEN + "LessonLecturer added with success:" + RESET);
			System.out.println(lessonLecturers.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! LessonLecturer not added" + RESET);
		}
		LessonLecturerId lLid = lessonLecturers.get(0).getId();
		lessonLecturerService.deleteLessonLecturer(lLid);
		lessonLecturers = lessonLecturerService.findAll();
		if (!lessonLecturers.isEmpty()) {
			System.out.println(RED + "PB!!! LessonLecturer not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "LessonLecturer deleted with success" + RESET);
		}

		// Test of UEManager
		System.out.println("Test of UEManager");
		ueManagerService.addUEManager(user, ue);

		List<UEManager> ueManagers = ueManagerService.findAll();
		if (!ueManagers.isEmpty()) {
			System.out.println(GREEN + "UEManager added with success:" + RESET);
			System.out.println(ueManagers.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! UEManager not added" + RESET);
		}
		UEManagerId ueManagerId = ueManagers.get(0).getId();
		ueManagerService.deleteUEManager(ueManagerId);
		ueManagers = ueManagerService.findAll();
		if (!ueManagers.isEmpty()) {
			System.out.println(RED + "PB!!! UEManager not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "UEManager deleted with success" + RESET);
		}

		// Test of TAFManager
		System.out.println("Test of TAFManager");
		tafManagerService.addTAFManager(user, taf);

		List<TAFManager> tafManagers = tafManagerService.findAll();
		if (!tafManagers.isEmpty()) {
			System.out.println(GREEN + "TAFManager added with success:" + RESET);
			System.out.println(tafManagers.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! TAFManager not added" + RESET);
		}
		TAFManagerId tafManagerId = tafManagers.get(0).getId();
		tafManagerService.deleteTAFManager(tafManagerId);
		tafManagers = tafManagerService.findAll();
		if (!tafManagers.isEmpty()) {
			System.out.println(RED + "PB!!! TAFManager not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "TAFManager deleted with success" + RESET);
		}

		// Test of Block
		System.out.println("Test of Block");
		UE ueBlock = ueService.addUE("ue block", "description", ihm);
		Lesson lessonBlock = lessonService.add("lesson block", "description", ueBlock);
		Block block = blockService.addBlock("Block1", lessonBlock, "description block");
		List<Block> blocks = blockService.findAll();
		if (!blocks.isEmpty()) {
			System.out.println(GREEN + "Block added with success:" + RESET);
			System.out.println(blocks.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! Block not added" + RESET);
		}
		blockService.deleteBlock(block.getId());
		blocks = blockService.findAll();
		if (!blocks.isEmpty()) {
			System.out.println(RED + "PB!!! Block not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "Block deleted with success" + RESET);
		}

		System.out.println(RED + " UE 2 : " + ue_2 + RESET);

		Lesson lessonSequencing1 = lessonService.add("name1", "description", ue_2);
		Lesson lessonSequencing2 = lessonService.add("name2", "description", ue_2);

		// Test of Antecedence
		System.out.println("Test of Antecedence");
		// lessonService.add("Lesson3", ue);
		// Lesson lesson2 = lessonService.findAll().get(1);

		antecedenceService.addAntecedence(lessonSequencing1, lessonSequencing2);

		List<Antecedence> antecedences = antecedenceService.findAll();
		if (!antecedences.isEmpty()) {
			System.out.println(GREEN + "Antecedence added with success:" + RESET);
			System.out.println(antecedences.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! Antecedence not added" + RESET);
		}
		AntecedenceId antecedenceId = antecedences.get(0).getId();
		antecedenceService.deleteAntecedence(antecedenceId);
		antecedences = antecedenceService.findAll();
		if (!antecedences.isEmpty()) {
			System.out.println(RED + "PB!!! Antecedence not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "Antecedence deleted with success" + RESET);
		}

		// Test of Sequencing
		System.out.println("Test of Sequencing");
		sequencingService.add(lessonSequencing1, lessonSequencing2);
		List<Sequencing> sequencings = sequencingService.findAll();
		if (!sequencings.isEmpty()) {
			System.out.println(GREEN + "Sequencing added with success:" + RESET);
			System.out.println(sequencings.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! Sequencing not added" + RESET);
		}
		SequencingId sequencingId = sequencings.get(0).getId();
		sequencingService.delete(sequencingId);
		sequencings = sequencingService.findAll();
		if (!sequencings.isEmpty()) {
			System.out.println(RED + "PB!!! Sequencing not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "Sequencing deleted with success" + RESET);
		}

		// Test of Synchronization
		System.out.println("Test of Synchronization");
		synchronizationService.addSynchronization(lessonSequencing1, lessonSequencing2);

		List<Synchronization> synchronizations = synchronizationService.findAll();
		if (!synchronizations.isEmpty()) {
			System.out.println(GREEN + "Synchronization added with success:" + RESET);
			System.out.println(synchronizations.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! Synchronization not added" + RESET);
		}
		SynchronizationId synchronizationId = synchronizations.get(0).getId();
		synchronizationService.deleteSynchronization(synchronizationId);
		synchronizations = synchronizationService.findAll();
		if (!synchronizations.isEmpty()) {
			System.out.println(RED + "PB!!! Synchronization not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "Synchronization deleted with success" + RESET);
		}

		System.out.println(RED + "Lesson Sequencing list 1 :" + lessonSequencing1.getSequencingsAsPrevious() + RESET);
		System.out.println(RED + "Lesson Sequencing list 2 :" + lessonSequencing2.getSequencingsAsNext() + RESET);
		System.out.println(RED + "Lesson Antecedence list 1 :" + lessonSequencing1.getAntecedencesAsPrevious() + RESET);
		System.out.println(RED + "Lesson Antecedence list 2 :" + lessonSequencing2.getAntecedencesAsNext() + RESET);
		System.out.println(RED + "Lesson Sync list 1 :" + lessonSequencing1.getSynchronizations1() + RESET);
		System.out.println(RED + "Lesson Sync list 2 :" + lessonSequencing2.getSynchronizations2() + RESET);

		System.out.println(ueService.findAll().get(0).getId());

		// Test api/ue/{ueid}/lesson
		Lesson lessonUe5_1 = lessonService.add("name", "description lesson UE 5 1", ue);
		Lesson lessonUe5_2 = lessonService.add("name", "description lesson UE 5 2", ue);
		Lesson lessonUe5_3 = lessonService.add("name", "description lesson UE 5 3", ue);
		Lesson lessonUe5_4 = lessonService.add("name", "description lesson UE 5 4", ue);
		Lesson lessonUe5_5 = lessonService.add("name", "description lesson UE 5 5", ue);
		Lesson lessonUe5_6 = lessonService.add("name", "description lesson UE 5 6", ue);

		Block blockUe5_1 = blockService.addBlock("title block", lessonUe5_1, "description block UE5 1");
		Block blockUe5_2 = blockService.addBlock("title block", lessonUe5_4, "description block UE5 2");
		Block blockUe5_3 = blockService.addBlock("title block", lessonUe5_6, "description block UE5 3");

		sequencingService.add(lessonUe5_1, lessonUe5_2);
		sequencingService.add(lessonUe5_2, lessonUe5_3);
		sequencingService.add(lessonUe5_4, lessonUe5_5);

		antecedenceService.addAntecedence(lessonUe5_3, lessonUe5_4);
		antecedenceService.addAntecedence(lessonUe5_5, lessonUe5_6);

*/
	}

	private static void testSolver(ApplicationContext context) {
		//testSolver1(context, planningSolverTestMinMaxLessonsUeWeek());
		//testSolver2(context);
		//testSolver2bis(context);
		//testSolver3(context);
		testSolverDCLNS1(context);
	}
	
	private static void testSolver1(ApplicationContext context, Planning planning) {		
		SolverServices solverServices = context.getBean(SolverServices.class);
		SolverMain.setServices(solverServices);
		SolverMain.generatePlanningString(planning);
	}
	
	private static void testSolver2(ApplicationContext context) {
		//Calendar[] cals = new Calendar[] {calendarSolver1(), calendarSolver2()};
		Planning[] plannings = new Planning[] {planningSolver1(), planningSolver2()};
		synchronizationService.addSynchronization(plannings[0].getCalendar().getTaf().getUes().get(0).getLessons().get(0), plannings[1].getCalendar().getTaf().getUes().get(1).getLessons().get(0));
		
		SolverServices solverServices = context.getBean(SolverServices.class);
		SolverMain.setServices(solverServices);
		SolverMain.generatePlannings(plannings);
	}
	
	private static void testSolver2bis(ApplicationContext context) {
		Planning planning1 = planningSolver1();
		Planning planning2 = planningSolver2();
		//synchronizationService.addSynchronization(planning1.getCalendar().getTaf().getUes().get(0).getLessons().get(0), planning2.getCalendar().getTaf().getUes().get(1).getLessons().get(0));
		synchronizationService.addSynchronization(planning2.getCalendar().getTaf().getUes().get(1).getLessons().get(0), planning1.getCalendar().getTaf().getUes().get(0).getLessons().get(0));
		SolverServices solverServices = context.getBean(SolverServices.class);
		SolverMain.setServices(solverServices);
		SolverMain.generatePlanning(planning1);
		SolverMain.generatePlanning(planning2);
		SolverMain.generatePlannings(new Planning[] {planning2}, new Planning[] {planning1});
	}
	
	private static void testSolver3(ApplicationContext context) {
		TAF taf1 = tafService.addTAF("taf1", null, null, null);
		TAF taf2 = tafService.addTAF("taf2", null, null, null);
		Calendar cal1 = calendarService.addCalendar(taf1);
		Calendar cal2 = calendarService.addCalendar(taf2);
		Planning plan1 = planningService.addPlanning(cal1);
		Planning plan2 = planningService.addPlanning(cal2);
		Planning[] plannings = new Planning[] {plan1, plan2};
		
		Week week = weekService.addWeek(5, 2025);
		Day day = dayService.addDay(1, week);
		Slot slot11 = slotService.add(1, day, cal1);
		Slot slot12 = slotService.add(2, day, cal1);
		Slot slot13 = slotService.add(3, day, cal1);
		Slot slot23 = slotService.add(3, day, cal2);
		Slot slot24 = slotService.add(4, day, cal2);
		
		UE ue1 = ueService.addUE("1", null, taf1);
		UE ue2 = ueService.addUE("2", null, taf2);
		
		Lesson lesson1 = lessonService.add("1", null, ue1);
		Lesson lesson2 = lessonService.add("2", null, ue2);
		
		synchronizationService.addSynchronization(lesson1, lesson2);
		
		SolverServices solverServices = context.getBean(SolverServices.class);
		SolverMain.setServices(solverServices);
		SolverMain.generatePlannings(plannings);
	}
	
	private static void testSolverDCLNS1(ApplicationContext context) {
		Planning planning = planningSolverDCLNS1();
		
		SolverServices solverServices = context.getBean(SolverServices.class);
		SolverMain.setServices(solverServices);
		SolverMain.generatePlanningString(planning);
	}
	
	private static Planning planningSolver1() {
		tafService.addTAF("DCL", "", "", "");
		List<TAF> listTafs = tafService.findByName("DCL");
		TAF dcl = listTafs.get(0);
		Calendar c = calendarService.addCalendar(dcl);
		Planning planning = planningService.addPlanning(c);
		Week week1 = weekService.addWeek(1, 2025);
		Week week2 = weekService.addWeek(2, 2025);
		Day day11 = dayService.addDay(1, week1);
		Day day12 = dayService.addDay(2, week1);
		Day day21 = dayService.addDay(1, week2);
		Slot slot1 = slotService.add(1, day11, c, LocalDateTime.of(2022,9,9,8,0), LocalDateTime.of(2022,9,9,9,15));
		Slot slot2 = slotService.add(2, day11, c, LocalDateTime.of(2022,9,9,9,30), LocalDateTime.of(2022, 9, 9, 10, 45));
		Slot slot3 = slotService.add(1, day12, c, LocalDateTime.of(2022,9,10,9,30), LocalDateTime.of(2022, 9, 10, 10, 45));
		Calendar c2 = calendarService.addCalendar(dcl);
		Slot slotDummy = slotService.add(1, day21, c2, LocalDateTime.of(2022,9,16,8,0), LocalDateTime.of(2022, 9, 16, 10, 0));
		Slot slot4 = slotService.add(1, day21, c, LocalDateTime.of(2022,9,16,8,0), LocalDateTime.of(2022, 9, 16, 9, 15));
		Slot slot5 = slotService.add(2, day21, c, LocalDateTime.of(2022,9,16,9,30), LocalDateTime.of(2022, 9, 16, 10, 45));
		UE ue1 = ueService.addUE("UE1", "", dcl);
		UE ue2 = ueService.addUE("UE2", "", dcl);
		Lesson lesson1 = lessonService.add("Lesson1", "", ue1);
		Lesson lesson2 = lessonService.add("Lesson2", "", ue1);
		Lesson lesson3 = lessonService.add("Lesson3", "", ue2);

		globalUnavailabilityService.addGlobalUnavailability(true, slot3);
		globalUnavailabilityService.addGlobalUnavailability(true, slot2);
		
		User jacques = userService.addUser("Jacques", "Noyé", "jacques.noye@imt-atlantique.fr", "sous l\'eau");
		User bertrand = userService.addUser("Bertrand", "Lentsch", "bertrand.lentsch@nantes.univ.fr", "Deeper meaning!");
		
		lessonLecturerService.addLessonLecturer(jacques, lesson1);
		lessonLecturerService.addLessonLecturer(jacques, lesson2);
		lessonLecturerService.addLessonLecturer(bertrand, lesson3);

		userUnavailabilityService.addUserUnavailability(slot1, bertrand, true);
		userUnavailabilityService.addUserUnavailability(slot5, bertrand, false);
		userUnavailabilityService.addUserUnavailability(slot4, bertrand, false);
		userUnavailabilityService.addUserUnavailability(slot1, jacques, false);
		userUnavailabilityService.addUserUnavailability(slot3, jacques, true);
		userUnavailabilityService.addUserUnavailability(slot4, jacques, false);
		
		return planning;
		
	}
	
	private static Planning planningSolver2() {
		TAF login = tafService.addTAF("Login*", "", "", "");
		Calendar c = calendarService.addCalendar(login);
		Planning planning = planningService.addPlanning(c);
		Week week1 = weekService.findByNumber(1).get(0);
		Week week2 = weekService.findByNumber(2).get(0);
		Day day11 = dayService.findByWeek(week1).get(0);
		Day day12 = dayService.findByWeek(week1).get(1);
		Day day21 = dayService.findByWeek(week2).get(0);
		Slot slot1 = slotService.add(1, day11, c, LocalDateTime.of(2022,9,9,8,0), LocalDateTime.of(2022,9,9,9,15));
		Slot slot2 = slotService.add(2, day11, c, LocalDateTime.of(2022,9,9,9,30), LocalDateTime.of(2022, 9, 9, 10, 45));
		Slot slot3 = slotService.add(1, day12, c, LocalDateTime.of(2022,9,10,8,0), LocalDateTime.of(2022, 9, 10, 9, 15));
		Slot slot4 = slotService.add(2, day12, c, LocalDateTime.of(2022,9,10,9,30), LocalDateTime.of(2022, 9, 10, 10, 45));
		Calendar c2 = calendarService.addCalendar(login);
		Slot slotDummy = slotService.add(1, day21, c2);
		Slot slot5 = slotService.add(1, day21, c, LocalDateTime.of(2022,9,16,9,30), LocalDateTime.of(2022, 9, 16, 10, 45));
		UE ue1 = ueService.addUE("UE1", "", login);
		UE ue2 = ueService.addUE("UE2", "", login);
		Lesson lesson1 = lessonService.add("Lesson4", "", ue1);
		Lesson lesson2 = lessonService.add("Lesson5", "", ue1);
		Lesson lesson3 = lessonService.add("Lesson6", "", ue2);
		
		
		User helene = userService.addUser("Hélène", "Coullon", "jacques.noye@imt-atlantique.fr", "password");
		User bertrand = userService.findAll().stream().filter(u -> u.getLastName().equals("Lentsch")).findFirst().get();//userService.findById((long) 1).get();
		
		System.out.println(bertrand);
		
		lessonLecturerService.addLessonLecturer(helene, lesson1);
		lessonLecturerService.addLessonLecturer(helene, lesson2);
		lessonLecturerService.addLessonLecturer(bertrand, lesson3);

		userUnavailabilityService.addUserUnavailability(slot1, bertrand, false);
		userUnavailabilityService.addUserUnavailability(slot5, bertrand, false);
		userUnavailabilityService.addUserUnavailability(slot4, bertrand, false);
		userUnavailabilityService.addUserUnavailability(slot1, helene, false);
		userUnavailabilityService.addUserUnavailability(slot5, helene, true);
		userUnavailabilityService.addUserUnavailability(slot2, helene, true);
		
		return planning;
	}
	
	private static Planning planningSolverOneDay() {
		TAF dcl = tafService.addTAF("DCL-Day", "", "", "");
		Calendar cal = calendarService.addCalendar(dcl);
		Planning planning = planningService.addPlanning(cal);
		Week week = weekService.addWeek(0, 2022);
		Day day = dayService.addDay(0, week);
		List<Slot> slots = new ArrayList<Slot>();
		LocalDate startSlotDay = LocalDate.of(2022, 9, 9);
		LocalTime startSlotHour = LocalTime.of(8, 0);
		for (int i = 0; i < 7; i ++) {
			slots.add(slotService.add(i, day, cal, LocalDateTime.of(startSlotDay, startSlotHour), LocalDateTime.of(startSlotDay, startSlotHour.plusMinutes(75))));
			startSlotHour = startSlotHour.plusMinutes(90);
		}
		UE ue1 = ueService.addUE("UE-1", "", dcl);
		UE ue2 = ueService.addUE("UE-2", "", dcl);

		User user1 = userService.addUser("user-1", "1", "1", "pw");
		User user2 = userService.addUser("user-2", "2", "2", "1234");
		
		List<Lesson> lessonsUE1 = createOrderedTypeLesson(3,"UE1-C", ue1);
		List<Lesson> lessonsUE2 = createOrderedTypeLesson(2, "UE2-C", ue2);

		addIntervenantToAllTypeLesson(lessonsUE1, user1);
		addIntervenantToAllTypeLesson(lessonsUE2, user2);

		sequencingService.add(lessonsUE1.get(0), lessonsUE1.get(1));
		sequencingService.add(lessonsUE1.get(1), lessonsUE1.get(2));
		
		userUnavailabilityService.addUserUnavailability(slots.get(6), user2, true);
		userUnavailabilityService.addUserUnavailability(slots.get(4), user2, false);
		userUnavailabilityService.addUserUnavailability(slots.get(5), user1, true);
		userUnavailabilityService.addUserUnavailability(slots.get(2), user1, false);
		
		globalUnavailabilityService.addGlobalUnavailability(true, slots.get(3));
		
		return planning;
	}
	
	private static Planning planningSolverTestMinMaxLessonsUeWeek() {
		TAF dcl = tafService.addTAF("DCL-Day", "", "", "");
		Calendar cal = calendarService.addCalendar(dcl);
		Planning planning = planningService.addPlanning(cal);
		List<Week> weeks = new ArrayList<Week>();
		List<List<Day>> days = new ArrayList<List<Day>>();
		List<List<List<Slot>>> slots = new ArrayList<List<List<Slot>>>();
		LocalDate startSlotDay = LocalDate.of(2022, 9, 9);
		for (int i = 0; i < 6; i ++) {
			weeks.add(weekService.addWeek(i, 2022));
			days.add(new ArrayList<Day>());
			slots.add(new ArrayList<List<Slot>>());
			for (int j = 0; j < 1; j ++) {
				days.getLast().add(dayService.addDay(j, weeks.getLast()));
				slots.getLast().add(new ArrayList<Slot>());
				LocalTime startSlotHour = LocalTime.of(8, 0);
				for (int k = 0; k < 7; k ++) {
					slots.getLast().getLast().add(slotService.add(k, days.getLast().getLast(), cal, LocalDateTime.of(startSlotDay, startSlotHour), LocalDateTime.of(startSlotDay, startSlotHour.plusMinutes(75))));
					startSlotHour = startSlotHour.plusMinutes(90);
				}
				startSlotDay = startSlotDay.plusDays(1);
				globalUnavailabilityService.addGlobalUnavailability(false, slots.getLast().getLast().get(3));
			}
			startSlotDay = startSlotDay.plusDays(4);
		}
		UE ue = ueService.addUE("UE", "", dcl);
		List<Lesson> lessons = new ArrayList<Lesson>();
		for (int l = 0; l < 7; l ++)
			lessons.add(lessonService.add("Lesson " + l, "", ue));
		return planning;
	}
	
	private static Planning planningSolverTestMaxBreakUe() {
		TAF dcl = tafService.addTAF("DCL - MaxBreakUe", "", "", "");
		Calendar cal = calendarService.addCalendar(dcl);
		Planning planning = planningService.addPlanning(cal);
		//Week week = weekService.addWeek(0, 2025);
		List<Week> weeks = new ArrayList<Week>();
		List<Day> days = new ArrayList<Day>();
		List<Slot> slots = new ArrayList<Slot>();
		for (int i = 0; i < 5; i ++) {
			weeks.add(weekService.addWeek(i, 2025));
			days.add(dayService.addDay(i, weeks.getLast()));
			slots.add(slotService.add(0, days.getLast(), cal));
		}
		for (int i = 1; i < 4; i ++) {
			globalUnavailabilityService.addGlobalUnavailability(false, slots.get(i));
		}
		
		UE ue = ueService.addUE("UE", "", dcl);
		List<Lesson> lessons = createOrderedTypeLesson(2, "Lesson", ue);
		
		return planning;
	}
	
	private static Calendar test() {
		TAF login = tafService.addTAF("Login*", "", "", "");
		Calendar c = calendarService.addCalendar(login);
		Week week1 = weekService.addWeek(1, 2025);
		Week week2 = weekService.addWeek(2, 2025);
		Day day11 = dayService.addDay(1, week1);
		Day day12 = dayService.addDay(2, week1);
		Day day21 = dayService.addDay(1, week2);
		Slot slot1 = slotService.add(1, day11, c, LocalDateTime.of(2025, 1, 4, 9, 30), LocalDateTime.of(2025, 1, 4, 10, 45));
		Slot slot2 = slotService.add(2, day11, c, LocalDateTime.of(2025, 1, 4, 11, 0), LocalDateTime.of(2025, 1, 4, 12, 15));
		Slot slot3 = slotService.add(1, day12, c, LocalDateTime.of(2025, 1, 5, 9, 30), LocalDateTime.of(2025, 1, 4, 10, 45));
		Slot slot4 = slotService.add(2, day12, c, LocalDateTime.of(2025, 1, 5, 11, 0), LocalDateTime.of(2025, 1, 4, 12, 15));
		
		
		UE ue1 = ueService.addUE("UE1", "", login);
		UE ue2 = ueService.addUE("UE2", "", login);
		Lesson lesson1 = lessonService.add("Lesson1", null, ue1);
		Lesson lesson2 = lessonService.add("Lesson2", null, ue1);
		Lesson lesson3 = lessonService.add("Lesson3", null, ue2);
		
		User helene = userService.addUser("Hélène", "Coullon", "jacques.noye@imt-atlantique.fr", "password");
		
		lessonLecturerService.addLessonLecturer(helene, lesson1);
		lessonLecturerService.addLessonLecturer(helene, lesson2);
		lessonLecturerService.addLessonLecturer(helene, lesson3);
		
		
		antecedenceService.addAntecedence(lesson2, lesson3);
		antecedenceService.addAntecedence(lesson3, lesson1);
		
		globalUnavailabilityService.addGlobalUnavailability(false, slot4);
		globalUnavailabilityService.addGlobalUnavailability(false, slot2);
		globalUnavailabilityService.addGlobalUnavailability(false, slot1);
		
		return c;
	}
	
	private static Planning planningSolverDCLNS1() {
		TAF dcl = tafService.addTAF("DCL-S1", "", "", "");
		Calendar cal = calendarService.addCalendar(dcl);
		Planning planning = planningService.addPlanning(cal);
		List<Day> mardis = new ArrayList<Day>();
		LocalDate startSlotDay = LocalDate.of(2025, 9, 9);
		for (int i = 37; i < 52; i ++) {
			Week week = weekService.addWeek(i, 2025);
			for (int j = 1; j < 3; j ++) {
				Day day = dayService.addDay(j, week);
				if (j == 1) mardis.add(day);
				LocalTime startSlotHour = LocalTime.of(8, 0);
				for (int k = 0; k < 7; k ++) {
					slotService.add(k, day, cal, LocalDateTime.of(startSlotDay, startSlotHour), LocalDateTime.of(startSlotDay, startSlotHour.plusMinutes(75)));
					startSlotHour = startSlotHour.plusMinutes(90);
				}
				startSlotDay = startSlotDay.plusDays(1);
			}
			startSlotDay = startSlotDay.plusDays(5);
		}
		//7th week is unavailable.
		calendarService.getWeeksSorted(cal.getId()).get(6).getDays().stream().flatMap(d -> d.getSlots().stream()).forEach(s -> globalUnavailabilityService.addGlobalUnavailability(true, s));
		
		UE mapd = ueService.addUE("MAPD", "", dcl);
		UE idl = ueService.addUE("IDL", "", dcl);
		UE eco = ueService.addUE("ECO", "", dcl);
		
		User intervenant1 = userService.addUser("Intervenant1", "1", "i1@imt-atlantique.fr", "password");
		User intervenant2 = userService.addUser("Intervenant2", "2", "i2@imt-atlantique.fr", "password");
		User intervenant3 = userService.addUser("Intervenant3", "3", "i3@imt-atlantique.fr", "123321");
		User intervenant4 = userService.addUser("Intervenant4", "4", "i4@imt-atlantique.fr", "motdepassesecret");
		User intervenant5 = userService.addUser("Intervenant5", "5", "i5@imt-atlantique.fr", "passwordnotsosecret");
		
		for (Day mardi : mardis)
			for (Slot slot : mardi.getSlots())
				userUnavailabilityService.addUserUnavailability(slot, intervenant4, true);
		
		
		List<Lesson> mapdCours = createOrderedTypeLesson(10, "C", mapd);
		List<Lesson> mapdFilRouge = createOrderedTypeLesson(11, "F", mapd);
		List<Lesson> mapdTP = createOrderedTypeLesson(9, "T", mapd);
		List<Lesson> mapdDS = createOrderedTypeLesson(2, "DS", mapd);
		
		antecedenceService.addAntecedence(mapdCours.get(0), mapdTP.get(0));
		antecedenceService.addAntecedence(mapdCours.get(1), mapdFilRouge.get(0));
		antecedenceService.addAntecedence(mapdCours.get(2), mapdTP.get(1));
		antecedenceService.addAntecedence(mapdCours.get(3), mapdTP.get(2));
		antecedenceService.addAntecedence(mapdCours.get(4), mapdFilRouge.get(6));
		antecedenceService.addAntecedence(mapdCours.get(5), mapdTP.get(4));
		antecedenceService.addAntecedence(mapdCours.get(7), mapdTP.get(6));
		antecedenceService.addAntecedence(mapdCours.get(8), mapdTP.get(7));
		antecedenceService.addAntecedence(mapdCours.get(9), mapdDS.get(0));
		antecedenceService.addAntecedence(mapdFilRouge.get(10), mapdDS.get(0));
		antecedenceService.addAntecedence(mapdTP.get(8), mapdDS.get(0));
		
		sequencingService.add(mapdDS.get(0), mapdDS.get(1));

		addIntervenantToTypeLesson(new int[] {0,1,2,4,6,9}, mapdCours, intervenant1);
		addIntervenantToTypeLesson(new int[] {0,1}, mapdTP, intervenant1);
		addIntervenantToTypeLesson(new int[] {0,1,5}, mapdFilRouge, intervenant1);

		addIntervenantToTypeLesson(new int[] {2,3,4,6,7,8}, mapdFilRouge, intervenant2);

		addIntervenantToTypeLesson(new int[] {3,5,7,8}, mapdCours, intervenant3);
		addIntervenantToTypeLesson(new int[] {2,3,4,5,6,7,8}, mapdTP, intervenant3);
		addIntervenantToTypeLesson(new int[] {9,10}, mapdFilRouge, intervenant3);
		
		List<Lesson> ecoCours = createOrderedTypeLesson(30, "C", eco);
		List<Lesson> ecoDS = createOrderedTypeLesson(2, "DS", eco);
		antecedenceService.addAntecedence(ecoCours.getLast(), ecoDS.getFirst());
		sequencingService.add(ecoDS.get(0), ecoDS.get(1));

		addIntervenantToAllTypeLesson(ecoCours, intervenant4);
		addIntervenantToAllTypeLesson(ecoDS, intervenant4);

		List<Lesson> idlCours = createOrderedTypeLesson(14, "C", idl);
		List<Lesson> idlSynchro = createOrderedTypeLesson(16, "S", idl);
		List<Lesson> idlDS = createOrderedTypeLesson(2, "DS", idl);

		antecedenceService.addAntecedence(idlSynchro.get(3), idlCours.get(0));
		antecedenceService.addAntecedence(idlCours.get(3), idlSynchro.get(4));
		antecedenceService.addAntecedence(idlCours.get(12), idlSynchro.get(10));
		antecedenceService.addAntecedence(idlSynchro.get(15), idlDS.get(0));
		antecedenceService.addAntecedence(idlCours.get(13), idlDS.get(0));

		addIntervenantToAllTypeLesson(idlCours, intervenant2);
		addIntervenantToAllTypeLesson(idlCours, intervenant5);
		addIntervenantToAllTypeLesson(idlSynchro, intervenant2);
		addIntervenantToAllTypeLesson(idlSynchro, intervenant5);
		addIntervenantToAllTypeLesson(idlDS, intervenant2);
		addIntervenantToAllTypeLesson(idlDS, intervenant5);
		
		
		
		return planning;
	}
	
	private static List<Lesson> createOrderedTypeLesson(int nb, String name, UE ue){
		List<Lesson> lCours = new ArrayList<Lesson>();
		for (int i = 0; i < nb; i ++) {
			Lesson cours = lessonService.add(name + i, "", ue);
			if (i != 0) antecedenceService.addAntecedence(lCours.getLast(), cours);
			lCours.add(cours);
		}
		return lCours;
	}
	private static void addIntervenantToTypeLesson(int[] iLessons, List<Lesson> lessons, User user) {
		for (int i : iLessons)
			lessonLecturerService.addLessonLecturer(user, lessons.get(i));
	}
	private static void addIntervenantToAllTypeLesson(List<Lesson> lessons, User user) {
		for (Lesson lesson : lessons)
			lessonLecturerService.addLessonLecturer(user, lesson);
	}
	
}
