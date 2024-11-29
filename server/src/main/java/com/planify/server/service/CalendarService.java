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

    @Lazy
    @Autowired
    private SlotService slotService;

    @Lazy
    @Autowired
    private TAFService tafService;

    @Transactional
    public Calendar addCalendar(TAF taf) {
        Calendar calendar = new Calendar(taf);

        // Update calendar list for taf
        List<Calendar> calendars = taf.getCalendars();
        calendars.addLast(calendar);
        taf.setCalendars(calendars);
        tafService.save(taf);

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

    @Transactional
    public boolean deleteCalendar(Long id) {
        if (calendarRepository.existsById(id)) {
            Calendar calendar = calendarRepository.findById(id).get();

            // Update calendar list for taf
            List<Calendar> calendars = calendar.getTaf().getCalendars();
            calendars.remove(calendar);
            calendar.getTaf().setCalendars(calendars);
            calendarRepository.save(calendar);

            // Delete the slots associated to this calendar
            List<Slot> slots = calendar.getSlots();
            for (Slot s : slots) {
                slotService.deleteSlot(s.getId());
            }

            calendarRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
