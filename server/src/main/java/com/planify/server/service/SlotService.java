package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.Calendar;
import com.planify.server.models.Day;
import com.planify.server.models.Sequencing;
import com.planify.server.models.Slot;
import com.planify.server.models.UserUnavailability;
import com.planify.server.repo.SlotRepository;

@Service
public class SlotService {

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private DayService dayService;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private UserUnavailabilityService userUnavailabilityService;

    public Slot add(int number, Day day, Calendar calendar) {
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

    public void save(Slot slot) {
        slotRepository.save(slot);
    }

    public Optional<Slot> findById(Long id) {
        Optional<Slot> slot = slotRepository.findById(id);
        return slot;
    }

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

            slotRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
