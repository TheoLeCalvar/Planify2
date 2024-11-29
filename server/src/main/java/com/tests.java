package com;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Day;
import com.planify.server.models.Week;
import com.planify.server.service.DayService;
import com.planify.server.service.WeekService;

public class tests {

    static final String RESET = "\u001B[0m";
    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";

    @Transactional
    public static void creationDay(DayService dayService, WeekService weekService) {
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
    }

}
