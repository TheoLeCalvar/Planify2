package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.Calendar;
import com.planify.server.models.Day;
import com.planify.server.models.Week;
import com.planify.server.repo.DayRepository;

@Service
public class DayService {

    @Autowired
    private DayRepository dayRepository;

    public Day add(int number, Week week) {
        Day day = new Day(number,week);

        // Update days list for week
        List<Day> days = week.getDays();
        days.addLast(day);
        week.setDays(days);

        dayRepository.save(day);
        return day;
    }

    public Optional<Day> findById(Long id) {
        Optional<Day> day = dayRepository.findById(id);
        return day;
    }

    public boolean delete(Long id) {
        if (dayRepository.existsById(id)) {

            Day day = dayRepository.findById(id).get();

            // Update days list for week
            List<Day> days = day.getWeek().getDays();
            days.remove(day);
            day.getWeek().setDays(days);

            dayRepository.deleteById(id);
            return true;
        }
        else {
            return false;
        }
    }
    
}