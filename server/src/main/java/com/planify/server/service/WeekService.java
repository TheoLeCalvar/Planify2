package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.Day;
import com.planify.server.models.Week;
import com.planify.server.repo.DayRepository;
import com.planify.server.repo.WeekRepository;

@Service
public class WeekService {

    @Autowired
    private WeekRepository weekRepository;

    @Autowired
    private DayRepository dayRepository;

    public Week addWeek(int number, Integer year) {
        Week week = new Week(number, year);
        week = weekRepository.save(week);
        return week;
    }

    public boolean deleteWeek(Long id) {
        if (weekRepository.existsById(id)) {
            // delete week in the day table
            List<Day> days = dayRepository.findByWeek(weekRepository.findById(id).get());
            dayRepository.deleteAll(days);

            weekRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Week> findById(Long Id) {
        return weekRepository.findById(Id);
    }

}
