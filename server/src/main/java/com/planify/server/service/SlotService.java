package com.planify.server.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Calendar;
import com.planify.server.models.Day;
import com.planify.server.models.GlobalUnavailability;
import com.planify.server.models.Slot;
import com.planify.server.models.UserUnavailability;
import com.planify.server.models.Week;
import com.planify.server.repo.SlotRepository;

@Service
public class SlotService {

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private DayService dayService;

    @Autowired
    private CalendarService calendarService;

    @Lazy
    @Autowired
    private UserUnavailabilityService userUnavailabilityService;

    @Lazy
    @Autowired
    private GlobalUnavailabilityService globalUnavailabilityService;

    @Transactional
    public Slot add(int number, Day day, Calendar calendar) {
        Day dayDB = dayService.findById(day.getId())
                .orElseThrow(() -> new RuntimeException("Day not found"));
        Calendar calendarDB = calendarService.findById(calendar.getId())
                .orElseThrow(() -> new RuntimeException("Calendar not found"));

        Slot slot = new Slot(number, day, calendar);

        // Update slot list for days
        List<Slot> slots = day.getSlots();
        slots.addLast(slot);
        day.setSlots(slots);
        dayService.save(day);

        // Update slot list for calendar
        List<Slot> slots2 = calendar.getSlots();
        slots2.addLast(slot);
        calendar.setSlots(slots2);
        calendarService.save(calendar);

        slotRepository.save(slot);
        return slot;
    }

    @Transactional
    public Slot add(int number, Day day, Calendar calendar, LocalDateTime start, LocalDateTime end) {
        Day dayDB = dayService.findById(day.getId())
                .orElseThrow(() -> new RuntimeException("Day not found"));
        Calendar calendarDB = calendarService.findById(calendar.getId())
                .orElseThrow(() -> new RuntimeException("Calendar not found"));

        Slot slot = new Slot(number, day, calendar, start, end);

        // Update slot list for days
        List<Slot> slots = day.getSlots();
        slots.addLast(slot);
        day.setSlots(slots);
        dayService.save(day);

        // Update slot list for calendar
        List<Slot> slots2 = calendar.getSlots();
        slots2.addLast(slot);
        calendar.setSlots(slots2);
        calendarService.save(calendar);

        slotRepository.save(slot);
        return slot;
    }

    public void save(Slot slot) {
        slotRepository.save(slot);
    }

    public Optional<Slot> findById(Long id) {
        Optional<Slot> slot = slotRepository.findById(id);
        return slot;
    }

    public List<Slot> findAll() {
        return slotRepository.findAll();
    }

    public Day getDay(Slot slot) {
        Day day = slot.getDay();
        return day;
    }

    public Week getWeek(Slot slot) {
        Day day = slot.getDay();
        Week week = day.getWeek();
        return week;
    }

    public List<Slot> findSlotsByDayAndCalendar(Day day, Calendar calendar) {
        List<Slot> slots = this.slotRepository.findByDayAndCalendar(day, calendar);
        slots.sort(Comparator.comparing(Slot::getNumber));
        return slots;
    }

    public List<Slot> findSlotsByWeekAndCalendar(Week week, Calendar calendar) {
        List<Day> days = week.getDays();
        days.sort(Comparator.comparing(Day::getNumber));
        List<Slot> slots = new ArrayList<Slot>();
        for (Day day : days) {
            slots.addAll(this.findSlotsByDayAndCalendar(day, calendar));
        }
        return slots;
    }
    
    public List<Slot> findSlotsByWeekAndCalendarWithoutUnavailableDays(Week week, Calendar calendar) {
    	List<Day> days = week.getDays();
    	days.removeIf(d -> d.getSlots().stream().map(s -> globalUnavailabilityService.findBySlot(s).filter(g -> g.getStrict()).isPresent()).reduce(true, Boolean::logicalAnd));
        days.sort(Comparator.comparing(Day::getNumber));
        List<Slot> slots = new ArrayList<Slot>();
        for (Day day : days) {
            slots.addAll(this.findSlotsByDayAndCalendar(day, calendar));
        }
        return slots;
    }
    
    @Transactional
    public boolean deleteSlot(Long id) {
        if (slotRepository.existsById(id)) {

            Slot slot = slotRepository.findById(id).get();

            // Update slot list for day
            List<Slot> slots = slot.getDay().getSlots();
            slots.remove(slot);
            slot.getDay().setSlots(slots);
            dayService.save(slot.getDay());

            // Update slot list for calendar
            List<Slot> slots2 = slot.getCalendar().getSlots();
            slots2.remove(slot);
            slot.getCalendar().setSlots(slots2);
            calendarService.save(slot.getCalendar());

            // Delete userUnavailabilities of this slot
            List<UserUnavailability> list = userUnavailabilityService.findBySlot(slot);
            for (UserUnavailability u : list) {
                userUnavailabilityService.deleteUserUnavailability(u.getId());
            }

            // Delete globalunavailability linked to this slot if exists
            Optional<GlobalUnavailability> globalUnavailability = globalUnavailabilityService.findBySlot(slot);
            if (globalUnavailability.isPresent()) {
                globalUnavailabilityService.deleteGlobalUnavailability(globalUnavailability.get().getId());
            }

            slotRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public List<Slot> getSlotsSorted(Long idCalendar) {
        return slotRepository.findSlotByIdCalendrierOrdered(idCalendar);
    }

}
