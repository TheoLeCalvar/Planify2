package com.planify.server.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.GlobalUnavailability;
import com.planify.server.models.Slot;
import com.planify.server.repo.GlobalUnavailabilityRepository;

@Service
public class GlobalUnavailabilityService {

    @Autowired
    private GlobalUnavailabilityRepository globalUnavailabilityRepository;

    public GlobalUnavailability add(boolean strict, Slot slot) {
        GlobalUnavailability globalUnavailability = new GlobalUnavailability(strict,slot);
        globalUnavailabilityRepository.save(globalUnavailability);
        return globalUnavailability;
    }

    public Optional<GlobalUnavailability> findBySlot(Slot slot) {
        Optional<GlobalUnavailability> globalUnavailability = globalUnavailabilityRepository.findBySlot(slot);
        return globalUnavailability;
    }


    public boolean delete(Slot slot) {
        if (globalUnavailabilityRepository.existsById(slot)) {
            globalUnavailabilityRepository.deleteById(slot);
            return true;
        }
        else {
            return false;
        }
    }
    
}