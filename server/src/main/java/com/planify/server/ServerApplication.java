package com.planify.server;

import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.tests;
import com.planify.server.models.*;
import com.planify.server.service.*;

@SpringBootApplication
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
		LessonLecturerService lecturerService = context.getBean(LessonLecturerService.class);
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
			// System.out.println(weekService.findByNumber(5).get(0).getDays());
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
		calendarService.addCalendar(taf);

	}

}
