package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.Antecedence;
import com.planify.server.models.Calendar;
import com.planify.server.models.TAF;
import com.planify.server.repo.CalendarRepository;

@Service
public class CalendarService {

    @Autowired
    private CalendarRepository calendarRepository;

    public Calendar add(TAF taf) {
        Calendar calendar = new Calendar(taf);

        // Update calendar list for taf
        List<Calendar> calendars = taf.getCalendars();
        calendars.addLast(calendar);
        taf.setCalendars(calendars);

        calendarRepository.save(calendar);
        return calendar;
    }

    public Optional<Calendar> findById(Long id) {
        Optional<Calendar> calendar = calendarRepository.findById(id);
        return calendar;
    }

    public boolean delete(Long id) {
        if (calendarRepository.existsById(id)) {
            Calendar calendar = calendarRepository.findById(id).get();

            // Update calendar list for taf
            List<Calendar> calendars = calendar.getTaf().getCalendars();
            calendars.remove(calendar);
            calendar.getTaf().setCalendars(calendars);

            calendarRepository.deleteById(id);
            return true;
        }
        else {
            return false;
        }
    }
    
}
