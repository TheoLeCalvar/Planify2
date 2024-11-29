package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Day;
import com.planify.server.models.Slot;
import com.planify.server.models.Week;
import com.planify.server.repo.DayRepository;

@Service
public class DayService {

    @Autowired
    private DayRepository dayRepository;

    @Lazy
    @Autowired
    private SlotService slotService;

    @Lazy
    @Autowired
    private WeekService weekService;

    @Transactional
    public Day addDay(int number, Week week) {
        Day day = new Day(number, week);

        // Update days list for week
        // List<Day> days = week.getDays();
        // days.addLast(day);
        // week.setDays(days);
        // weekService.save(week);

        dayRepository.save(day);
        return day;
    }

    public void save(Day day) {
        dayRepository.save(day);
    }

    public Optional<Day> findById(Long id) {
        Optional<Day> day = dayRepository.findById(id);
        return day;
    }

    public List<Day> findByWeek(Week week) {
        return dayRepository.findByWeek(week);
    }

    @Transactional
    public boolean deleteDay(Long id) {
        if (dayRepository.existsById(id)) {

            Day day = dayRepository.findById(id).get();

            // Update days list for week
            List<Day> days = day.getWeek().getDays();
            days.remove(day);
            day.getWeek().setDays(days);
            dayRepository.save(day);

            // Delete the slots associated to this day
            List<Slot> slots = day.getSlots();
            for (Slot s : slots) {
                slotService.deleteSlot(s.getId());
            }

            dayRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
