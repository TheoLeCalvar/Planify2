package com.planify.server;

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
	
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ServerApplication.class, args);

		final String RESET = "\u001B[0m";
		final String RED = "\u001B[31m";
		final String GREEN = "\u001B[32m";

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
		
		
		testSolver(context);
		if (weekService != null) return; //Just to not have warnings when we want to stops tests here.
		
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

	}

	private static void testSolver(ApplicationContext context) {
		testSolver1(context);
		//testSolver2(context);
		//testSolver3(context);
	}
	
	private static void testSolver1(ApplicationContext context) {
		Calendar c = calendarSolver1();
		
		SolverServices solverServices = context.getBean(SolverServices.class);
		SolverMain.setServices(solverServices);
		SolverMain.generateCal(c);
	}
	
	private static void testSolver2(ApplicationContext context) {
		Calendar[] cals = new Calendar[] {calendarSolver1(), calendarSolver2()};
		
		synchronizationService.addSynchronization(cals[0].getTaf().getUes().get(0).getLessons().get(0), cals[1].getTaf().getUes().get(1).getLessons().get(0));
		
		SolverServices solverServices = context.getBean(SolverServices.class);
		SolverMain.setServices(solverServices);
		SolverMain.generateCals(cals);
	}
	
	private static void testSolver3(ApplicationContext context) {
		TAF taf1 = tafService.addTAF("taf1", null, null, null);
		TAF taf2 = tafService.addTAF("taf2", null, null, null);
		Calendar cal1 = calendarService.addCalendar(taf1);
		Calendar cal2 = calendarService.addCalendar(taf2);
		Calendar[] cals = new Calendar[] {cal1, cal2};
		
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
		SolverMain.generateCals(cals);
	}
	
	private static Calendar calendarSolver1() {
		tafService.addTAF("DCL", "", "", "");
		List<TAF> listTafs = tafService.findByName("DCL");
		TAF dcl = listTafs.get(0);
		Calendar c = calendarService.addCalendar(dcl);
		Week week1 = weekService.addWeek(1, 2025);
		Week week2 = weekService.addWeek(2, 2025);
		Day day11 = dayService.addDay(1, week1);
		Day day12 = dayService.addDay(2, week1);
		Day day21 = dayService.addDay(1, week2);
		Slot slot1 = slotService.add(1, day11, c);
		Slot slot2 = slotService.add(2, day11, c);
		Slot slot3 = slotService.add(1, day12, c);
		/*Calendar c2 = calendarService.addCalendar(dcl);
		Slot slotDummy = slotService.add(1, day21, c2);*/		
		Slot slot4 = slotService.add(1, day21, c);
		Slot slot5 = slotService.add(2, day21, c);
		UE ue1 = ueService.addUE("UE1", "", dcl);
		UE ue2 = ueService.addUE("UE2", "", dcl);
		Lesson lesson1 = lessonService.add("Lesson1", null, ue1);
		Lesson lesson2 = lessonService.add("Lesson2", null, ue1);
		Lesson lesson3 = lessonService.add("Lesson3", null, ue2);

		globalUnavailabilityService.addGlobalUnavailability(true, slot3);
		globalUnavailabilityService.addGlobalUnavailability(true, slot2);
		
		User jacques = userService.addUser("Jacques", "Noyé", "jacques.noye@imt-atlantique.fr", new char[]{'s', 'o', 'u', 's', ' ', 'l', '\'', 'e', 'a', 'u'});
		User bertrand = userService.addUser("Bertrand", "Lentsch", "bertrand.lentsch@nantes.univ.fr", new char[] {'D', 'e', 'e', 'p', 'e', 'r', ' ', 'm', 'e', 'a', 'n', 'i', 'n', 'g', '!'});
		
		lessonLecturerService.addLessonLecturer(jacques, lesson1);
		lessonLecturerService.addLessonLecturer(jacques, lesson2);
		lessonLecturerService.addLessonLecturer(bertrand, lesson3);

		userUnavailabilityService.addUserUnavailability(slot1, bertrand, true);
		userUnavailabilityService.addUserUnavailability(slot5, bertrand, false);
		userUnavailabilityService.addUserUnavailability(slot4, bertrand, false);
		userUnavailabilityService.addUserUnavailability(slot1, jacques, false);
		userUnavailabilityService.addUserUnavailability(slot3, jacques, true);
		userUnavailabilityService.addUserUnavailability(slot4, jacques, false);
		
		return c;
		
	}
	
	private static Calendar calendarSolver2() {
		TAF login = tafService.addTAF("Login*", "", "", "");
		Calendar c = calendarService.addCalendar(login);
		Week week1 = weekService.findByNumber(1).get(0);
		Week week2 = weekService.findByNumber(2).get(0);
		Day day11 = dayService.findByWeek(week1).get(0);
		Day day12 = dayService.findByWeek(week1).get(1);
		Day day21 = dayService.findByWeek(week2).get(0);
		Slot slot1 = slotService.add(1, day11, c);
		Slot slot2 = slotService.add(2, day11, c);
		Slot slot3 = slotService.add(1, day12, c);
		Slot slot4 = slotService.add(2, day12, c);
		/*Calendar c2 = calendarService.addCalendar(dcl);
		Slot slotDummy = slotService.add(1, day21, c2);*/		
		Slot slot5 = slotService.add(1, day21, c);
		UE ue1 = ueService.addUE("UE1", "", login);
		UE ue2 = ueService.addUE("UE2", "", login);
		Lesson lesson1 = lessonService.add("Lesson1", null, ue1);
		Lesson lesson2 = lessonService.add("Lesson2", null, ue1);
		Lesson lesson3 = lessonService.add("Lesson3", null, ue2);
		
		
		User helene = userService.addUser("Hélène", "Coullon", "jacques.noye@imt-atlantique.fr", new char[]{});
		User bertrand = userService.findById((long) 1).get();
		
		System.out.println(bertrand);
		
		lessonLecturerService.addLessonLecturer(helene, lesson1);
		lessonLecturerService.addLessonLecturer(helene, lesson2);
		lessonLecturerService.addLessonLecturer(bertrand, lesson3);

		userUnavailabilityService.addUserUnavailability(slot1, bertrand, true);
		userUnavailabilityService.addUserUnavailability(slot5, bertrand, true);
		userUnavailabilityService.addUserUnavailability(slot4, bertrand, false);
		userUnavailabilityService.addUserUnavailability(slot1, helene, false);
		userUnavailabilityService.addUserUnavailability(slot5, helene, true);
		userUnavailabilityService.addUserUnavailability(slot2, helene, true);
		
		return c;
	}
	
}
