package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.LazyToOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Calendar;
import com.planify.server.models.Day;
import com.planify.server.models.GlobalUnavailability;
import com.planify.server.models.Slot;
import com.planify.server.models.UserUnavailability;
import com.planify.server.repo.SlotRepository;

@Service
public class SlotService {

    @Autowired
    private SlotRepository slotRepository;

    public Slot add(int number, Day day, Calendar calendar) {
        Slot slot = new Slot(number, day, calendar);
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
            slotRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
