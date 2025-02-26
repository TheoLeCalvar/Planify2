package com.planify.server.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.planify.server.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Lazy
    @Autowired
    private PlanningService planningService;
    
    @Lazy
    @Autowired
    private GlobalUnavailabilityService globalUnavailabilityService;

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

            //Delete the planning
            List<Planning> plannings = calendar.getPlannings();
            for (Planning planning: plannings) {
                planningService.delete(planning.getId());
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
    
    public List<Slot> getSlotsOrderedWithoutUnavailableDays(Long calendarId) {
    	if (calendarRepository.existsById(calendarId)) {
    		return this.getDaysSortedWithoutUnavailable(calendarId).stream().flatMap(d -> d.getSlots().stream().filter(s -> s.getCalendar().getId() == calendarId)).toList();
    	}
    	return new ArrayList<Slot>();
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
            return getDaysSortedFromSlots(slots);
        }
        return new ArrayList<Day>();
    }
    
    public List<Day> getDaysSortedWithoutUnavailable(Long calendarId) {
    	if (calendarRepository.existsById(calendarId)) {
    		List<Slot> slots = slotService.getSlotsSorted(calendarId);
    		slots.removeIf(s -> globalUnavailabilityService.findBySlot(s).filter(g -> g.getStrict()).isPresent());
    		return getDaysSortedFromSlots(slots);
    	}
    	return new ArrayList<Day>();
    }
    
    private List<Day> getDaysSortedFromSlots(List<Slot> slots) {
    	Set<Day> days = new LinkedHashSet<Day>();
        for (Slot s : slots) {
            days.add(s.getDay());
        }
        return new ArrayList<>(days);
    }

    public List<Week> getWeeksSorted(Long idCalendar) {
    	if (calendarRepository.existsById(idCalendar)) {
            List<Slot> slots = this.getSlotsOrdered(idCalendar);
            return getWeeksSortedFromSlots(slots);
        } else {
            return new ArrayList<Week>();
        }
    }
    
    public List<Week> getWeeksSortedWithoutUnavailable(Long idCalendar) {
    	if (calendarRepository.existsById(idCalendar)) {
            List<Slot> slots = this.getSlotsOrdered(idCalendar);
            slots.removeIf(s -> globalUnavailabilityService.findBySlot(s).filter(g -> g.getStrict()).isPresent());
            return getWeeksSortedFromSlots(slots);
        } else {
            return new ArrayList<Week>();
        }
    }
    
    private List<Week> getWeeksSortedFromSlots(List<Slot> slots) {
    	Set<Week> weeks = new LinkedHashSet<Week>();
        for (Slot s : slots) {
            weeks.add(s.getDay().getWeek());
        }
        return new ArrayList<>(weeks);
    }

}
