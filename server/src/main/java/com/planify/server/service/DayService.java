package com.planify.server.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Antecedence;
import com.planify.server.models.Calendar;
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
    
    @Lazy
    @Autowired
    private GlobalUnavailabilityService globalUnavailabilityService;

    @Transactional
    public Day addDay(int number, Week week) {
        Day day = new Day(number, week);

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

    public List<Day> findAll() {
        return dayRepository.findAll();
    }

    public List<Day> findByWeek(Week week) {
        return dayRepository.findByWeek(week);
    }

    public List<Slot> findLastDailySlotsByCalendar(Calendar calendar) {
        List<Day> allDays = this.dayRepository.findAll();
        List<Slot> slots = new ArrayList<Slot>();
        for (Day day : allDays) {
        	Slot lastSlot = null;
        	for (Slot slot : day.getSlots())
	            if (slot.getCalendar().getId() == calendar.getId() && (lastSlot == null || lastSlot.compareTo(slot) < 0)) {
	                lastSlot = slot;
	            }
        	if (lastSlot != null) slots.add(lastSlot);
        }
        return slots;
    }
    
    public List<Slot> findSlotsDayByCalendar(Day day, Calendar calendar) {
    	List<Slot> slots = new ArrayList<Slot>();
    	for (Slot slot : day.getSlots())
    		if (slot.getCalendar().getId() == calendar.getId())
    			slots.add(slot);
    	return slots;
    }
    
    public List<Slot> findSlotsDayByCalendarSorted(Day day, Calendar calendar) {
    	List<Slot> slots = new ArrayList<Slot>();
    	for (Slot slot : day.getSlots())
    		if (slot.getCalendar().getId() == calendar.getId())
    			slots.add(slot);
    	slots.sort(Comparator.comparing(Slot::getStart));
    	return slots;
    }
    
    public boolean isDayAvailableForCalendar(Day day, Calendar calendar) {
    	return day.getSlots().stream().filter(s -> s.getCalendar().getId() == calendar.getId())
									.anyMatch(s -> globalUnavailabilityService.findBySlot(s)
													.filter(g -> g.getStrict()).isEmpty());
    }
    
    @Transactional
    public boolean deleteDay(Long id) {
        if (dayRepository.existsById(id)) {

            Day day = dayRepository.findById(id).get();

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
