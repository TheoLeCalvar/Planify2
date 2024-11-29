package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Calendar;
import com.planify.server.models.Slot;
import com.planify.server.models.TAF;
import com.planify.server.repo.CalendarRepository;

@Service
public class CalendarService {

    @Autowired
    private CalendarRepository calendarRepository;

    public Calendar addCalendar(TAF taf) {
        Calendar calendar = new Calendar(taf);

        calendarRepository.save(calendar);
        return calendar;
    }

    public void save(Calendar calendar) {
        calendarRepository.save(calendar);
    }

    public Optional<Calendar> findById(Long id) {
        Optional<Calendar> calendar = calendarRepository.findById(id);
        return calendar;
    }

    public List<Calendar> findAll() {
        List<Calendar> calendars = calendarRepository.findAll();
        return calendars;
    }

    public boolean deleteCalendar(Long id) {
        if (calendarRepository.existsById(id)) {
            calendarRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
