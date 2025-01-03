package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Antecedence;
import com.planify.server.models.GlobalUnavailability;
import com.planify.server.models.Slot;
import com.planify.server.repo.GlobalUnavailabilityRepository;

@Service
public class GlobalUnavailabilityService {

    @Autowired
    private GlobalUnavailabilityRepository globalUnavailabilityRepository;

    @Autowired
    @Lazy
    private SlotService slotService;

    @Transactional
    public GlobalUnavailability addGlobalUnavailability(boolean strict, Slot slot) {
        GlobalUnavailability globalUnavailability = new GlobalUnavailability(strict, slot);
        Slot existingSlot = slotService.findById(slot.getId())
                                  .orElseThrow(() -> new RuntimeException("Slot not found"));

        globalUnavailability.setSlot(existingSlot);
        globalUnavailabilityRepository.save(globalUnavailability);
        return globalUnavailability;
    }

    public void save(GlobalUnavailability globalUnavailability) {
        globalUnavailabilityRepository.save(globalUnavailability);
    }

    public Optional<GlobalUnavailability> findById(Long id) {
        Optional<GlobalUnavailability> globalUnavailability = globalUnavailabilityRepository.findById(id);
        return globalUnavailability;
    }

    public List<GlobalUnavailability> findAll() {
        return globalUnavailabilityRepository.findAll();
    }

    public Optional<GlobalUnavailability> findBySlot(Slot slot) {
        Optional<GlobalUnavailability> globalUnavailability = globalUnavailabilityRepository.findBySlot(slot);
        return globalUnavailability;
    }

    public boolean deleteGlobalUnavailability(Long id) {
        if (globalUnavailabilityRepository.existsById(id)) {
            globalUnavailabilityRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
