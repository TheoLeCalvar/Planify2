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

		// Test of calendarService.getSlotsOrdered(idCalendar), getNumberOfSlots,
		// getDaysSorted
		tafService.addTAF("DCL");
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

		ueService.addUE("MAPD", dcl);
		ueService.addUE("Environnement du dvlpeur", dcl);
		// User u = userService.addUser("Théo", "Le Calvar", "theo.lecalvar", null);
		// System.out.println(u.toString());
		// tafManagerService.addTAFManager(u, dcl);

	}

}
