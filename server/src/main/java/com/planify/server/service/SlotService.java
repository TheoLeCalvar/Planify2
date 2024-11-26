package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.Calendar;
import com.planify.server.models.Day;
import com.planify.server.models.Slot;
import com.planify.server.repo.SlotRepository;

@Service
public class SlotService {

    @Autowired
    private SlotRepository slotRepository;

    public Slot add(int number, Day day, Calendar calendar) {
        Slot slot = new Slot(number,day,calendar);

        // Update slot list for days
        List<Slot> slots = day.getSlots();
        slots.addLast(slot);
        day.setSlots(slots);

        // Update slot list for calendar
        List<Slot> slots2 = calendar.getSlots();
        slots2.addLast(slot);
        calendar.setSlots(slots2);

        slotRepository.save(slot);
        return slot;
    }

    public Optional<Slot> findById(Long id) {
        Optional<Slot> slot = slotRepository.findById(id);
        return slot;
    }

    public boolean delete(Long id) {
        if (slotRepository.existsById(id)) {

            Slot slot = slotRepository.findById(id).get();

            // Update slot list for day
            List<Slot> slots = slot.getDay().getSlots();
            slots.remove(slot);
            slot.getDay().setSlots(slots);

            // Update slot list for calendar
            List<Slot> slots2 = slot.getCalendar().getSlots();
            slots2.remove(slot);
            slot.getCalendar().setSlots(slots2);

            slotRepository.deleteById(id);
            return true;
        }
        else {
            return false;
        }
    }
    
}
