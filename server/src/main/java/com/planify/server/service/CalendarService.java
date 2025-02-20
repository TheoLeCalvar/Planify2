package com.planify.server.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Calendar;
import com.planify.server.models.Day;
import com.planify.server.models.Slot;
import com.planify.server.models.TAF;
import com.planify.server.models.Week;
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
            List<Slot> slots = new ArrayList<Slot>(calendar.getSlots());
            for (Slot s : slots) {
                slotService.deleteSlot(s.getId());
            }

            calendarRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public List<Slot> getSlotsOrdered(Long calendarId) {
        if (calendarRepository.existsById(calendarId)) {
            return slotService.getSlotsSorted(calendarId);
        } else {
            return new ArrayList<Slot>();
        }
    }

    public int getNumberOfSlots(Long idCalendar) {
        if (calendarRepository.existsById(idCalendar)) {
            Calendar c = calendarRepository.findById(idCalendar).get();
            return c.getSlots().size();
        } else {
            return -1;
        }
    }

    public List<Day> getDaysSorted(Long calendarId) {
        if (calendarRepository.existsById(calendarId)) {
            List<Slot> slots = slotService.getSlotsSorted(calendarId);
            List<Day> days = new ArrayList<Day>();
            for (Slot s : slots) {
                days.add(s.getDay());
            }
            Set<Day> uniqueSet = new LinkedHashSet<>(days);
            return new ArrayList<>(uniqueSet);
        } else {
            return new ArrayList<Day>();
        }
    }

    public List<Week> getWeeksSorted(Long idCalendar) {
        if (calendarRepository.existsById(idCalendar)) {
            List<Day> days = this.getDaysSorted(idCalendar);
            List<Week> weeks = new ArrayList<Week>();
            for (Day d : days) {
                weeks.add(d.getWeek());
            }
            Set<Week> uniqueSet = new LinkedHashSet<>(weeks);
            return new ArrayList<>(uniqueSet);
        } else {
            return new ArrayList<Week>();
        }
    }

}
