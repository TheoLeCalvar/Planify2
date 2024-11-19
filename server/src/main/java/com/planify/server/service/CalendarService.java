package com.planify.server.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.Calendar;
import com.planify.server.models.TAF;
import com.planify.server.repo.CalendarRepository;

@Service
public class CalendarService {

    @Autowired
    private CalendarRepository calendarRepository;

    public Calendar add(TAF taf) {
        Calendar calendar = new Calendar(taf);
        calendarRepository.save(calendar);
        return calendar;
    }

    public Optional<Calendar> findById(Long id) {
        Optional<Calendar> calendar = calendarRepository.findById(id);
        return calendar;
    }

    public boolean delete(Long id) {
        if (calendarRepository.existsById(id)) {
            calendarRepository.deleteById(id);
            return true;
        }
        else {
            return false;
        }
    }
    
}
