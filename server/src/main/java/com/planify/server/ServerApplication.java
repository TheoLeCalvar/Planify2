package com.planify.server;

import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.tests;
import com.planify.server.models.*;
import com.planify.server.models.Antecedence.AntecedenceId;
import com.planify.server.models.LessonLecturer.LessonLecturerId;
import com.planify.server.models.Sequencing.SequencingId;
import com.planify.server.models.Synchronization.SynchronizationId;
import com.planify.server.models.TAFManager.TAFManagerId;
import com.planify.server.models.UEManager.UEManagerId;
import com.planify.server.models.UserUnavailability.UserUnavailabilityId;
import com.planify.server.service.*;

@SpringBootApplication(scanBasePackages = "com.planify.server")
public class ServerApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ServerApplication.class, args);

		final String RESET = "\u001B[0m";
		final String RED = "\u001B[31m";
		final String GREEN = "\u001B[32m";

		// Creation of the services
		AntecedenceService antecedenceService = context.getBean(AntecedenceService.class);
		BlockService blockService = context.getBean(BlockService.class);
		CalendarService calendarService = context.getBean(CalendarService.class);
		DayService dayService = context.getBean(DayService.class);
		GlobalUnavailabilityService globalUnavailabilityService = context.getBean(GlobalUnavailabilityService.class);
		LessonLecturerService lessonLecturerService = context.getBean(LessonLecturerService.class);
		LessonService lessonService = context.getBean(LessonService.class);
		SequencingService sequencingService = context.getBean(SequencingService.class);
		SlotService slotService = context.getBean(SlotService.class);
		SynchronizationService synchronizationService = context.getBean(SynchronizationService.class);
		TAFManagerService tafManagerService = context.getBean(TAFManagerService.class);
		TAFService tafService = context.getBean(TAFService.class);
		UEManagerService ueManagerService = context.getBean(UEManagerService.class);
		UEService ueService = context.getBean(UEService.class);
		UserService userService = context.getBean(UserService.class);
		UserUnavailabilityService userUnavailabilityService = context.getBean(UserUnavailabilityService.class);
		WeekService weekService = context.getBean(WeekService.class);

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
		tafService.addTAF("DCL");

		List<TAF> tafs = tafService.findByName("DCL");
		if (!tafs.isEmpty()) {
			System.out.println(GREEN + "TAF added with success:" + RESET);
			System.out.println(tafs.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! TAF not added" + RESET);
		}
		Long idTaf = tafs.get(0).getId();
		tafService.deleteTAF(idTaf);
		tafs = tafService.findByName("DCL");
		if (!tafs.isEmpty()) {
			System.out.println(RED + "PB!!! TAF not deleted:" + GREEN);
		} else {
			System.out.println(GREEN + "TAF deleted with success" + RESET);
		}
		tafService.addTAF("DCL");
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
		tafService.addTAF("LOGIN");
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

		// Test of UserUnavailability
		System.out.println("Test of UserUnavailability");
		userService.addUser("John", "Doe", "mail", null);
		User user = userService.findAll().get(0);
		slotService.add(2, dayService.findByWeek(week).get(0), calendarService.findAll().get(0));
		Slot userSlot = slotService.findAll().get(0);

		userUnavailabilityService.addUserUnavailability(userSlot, user, true);

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
		ueService.addUE("UE-Test", taf);
		List<UE> ues = ueService.findAll();
		if (!ues.isEmpty()) {
			System.out.println(GREEN + "UE added with success:" + RESET);
			System.out.println(ues.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! UE not added" + RESET);
		}
		Long idUE = ues.get(0).getId();
		ueService.deleteUE(idUE);
		ues = ueService.findAll();
		if (!ues.isEmpty()) {
			System.out.println(RED + "PB!!! UE not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "UE deleted with success" + RESET);
		}
		ueService.addUE("UE-Test", taf);

		// Test of Lesson
		System.out.println("Test of Lesson");
		ueService.addUE("UE1", taf); // Adding a UE for the lesson
		UE ue = ueService.findAll().get(0);
		lessonService.add("Lesson1", ue);

		List<Lesson> lessons = lessonService.findAll();
		if (!lessons.isEmpty()) {
			System.out.println(GREEN + "Lesson added with success:" + RESET);
			System.out.println(lessons.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! Lesson not added" + RESET);
		}
		Long idLesson = lessons.get(0).getId();
		lessonService.delete(idLesson);
		lessons = lessonService.findAll();
		if (!lessons.isEmpty()) {
			System.out.println(RED + "PB!!! Lesson not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "Lesson deleted with success" + RESET);
		}
		lessonService.add("Lesson1", ue);

		// Test of LessonLecturer
		System.out.println("Test of LessonLecturer");
		Lesson lesson1 = lessonService.findAll().get(0);

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
		Lesson firstLesson = lessonService.findAll().get(0);
		blockService.addBlock("Block1", firstLesson);
		List<Block> blocks = blockService.findAll();
		if (!blocks.isEmpty()) {
			System.out.println(GREEN + "Block added with success:" + RESET);
			System.out.println(blocks.get(0).toString());
		} else {
			System.out.println(RED + "PB!!! Block not added" + RESET);
		}
		Long idBlock = blocks.get(0).getId();
		blockService.deleteBlock(idBlock);
		blocks = blockService.findAll();
		if (!blocks.isEmpty()) {
			System.out.println(RED + "PB!!! Block not deleted:" + RESET);
		} else {
			System.out.println(GREEN + "Block deleted with success" + RESET);
		}

		// Test of Antecedence
		System.out.println("Test of Antecedence");
		lessonService.add("Lesson3", ue);
		Lesson lesson2 = lessonService.findAll().get(1);

		antecedenceService.addAntecedence(lesson1, lesson2);

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
		sequencingService.add(lesson1, lesson2);
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
		synchronizationService.addSynchronization(lesson1, lesson2);

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

	}

}
