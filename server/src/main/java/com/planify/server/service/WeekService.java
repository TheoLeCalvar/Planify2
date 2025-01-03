package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.planify.server.models.Day;
import com.planify.server.models.UserUnavailability;
import com.planify.server.models.Week;
import com.planify.server.repo.WeekRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class WeekService {

    @Autowired
    private WeekRepository weekRepository;

    @Lazy
    @Autowired
    private DayService dayService;

    public Week addWeek(int number, Integer year) {
        Week week = new Week(number, year);
        week = weekRepository.save(week);
        return week;
    }

    @Transactional
    public boolean deleteWeek(Long id) {
        if (weekRepository.existsById(id)) {
            // delete week in the day table
            List<Day> days = weekRepository.findById(id).get().getDays();
            for (Day day : days) {
                dayService.deleteDay(day.getId());
            }

            weekRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Week> findById(Long Id) {
        return weekRepository.findById(Id);
    }

    public List<Week> findAll() {
        return weekRepository.findAll();
    }

    @Transactional
    public List<Week> findByNumber(int n) {
        List<Week> weeks = weekRepository.findByNumber(n);
        return weeks;
    }

    public void save(Week week) {
        weekRepository.save(week);
    }

}
