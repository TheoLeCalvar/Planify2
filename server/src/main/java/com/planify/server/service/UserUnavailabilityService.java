package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Slot;
import com.planify.server.models.Synchronization;
import com.planify.server.models.User;
import com.planify.server.models.UserUnavailability;
import com.planify.server.models.UserUnavailability.UserUnavailabilityId;
import com.planify.server.repo.UserUnavailabilityRepository;

@Service
public class UserUnavailabilityService {

    @Autowired
    private UserUnavailabilityRepository userUnavailabilityRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SlotService slotService;

    @Transactional
    public UserUnavailability addUserUnavailability(Slot slot, User user, boolean strict) {

    	if (slot == null || slot.getId() == null) {
            System.out.println("SLOT NULL:" + slot);
            throw new IllegalArgumentException("Slot or Slot ID cannot be null");
        }

        Slot managedSlot = slotService.findById(slot.getId())
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        

    	UserUnavailability userUnavailability = new UserUnavailability(slot, user, strict);
        
    	userUnavailabilityRepository.save(userUnavailability);
        
        return userUnavailability;
    }

    @Transactional
    public boolean deleteUserUnavailability(UserUnavailabilityId id) {
        if (userUnavailabilityRepository.existsById(id)) {
            UserUnavailability userUnavailability = userUnavailabilityRepository.findById(id).get();

            // Remove userUnavailability from the table
            userUnavailabilityRepository.deleteById(id);

            return true;
        }
        return false;
    }

    public Optional<UserUnavailability> findById(UserUnavailabilityId id) {
        return userUnavailabilityRepository.findById(id);
    }

    public List<UserUnavailability> findAll() {
        return userUnavailabilityRepository.findAll();
    }

    public List<UserUnavailability> findBySlot(Slot s) {
        return userUnavailabilityRepository.findBySlot(s);
    }

    public void save(UserUnavailability userUnavailability) {
        userUnavailabilityRepository.save(userUnavailability);
    }

    public boolean exists(Slot slot) {
        return userUnavailabilityRepository.existsBySlot(slot);
    }

    public boolean existsBySlotAndByUser(Slot slot, User user) {
        return userUnavailabilityRepository.existsBySlotAndUser(slot, user);
    }

    public Optional<UserUnavailability> findBySlotAndByUser(Slot slot, User user) {
        return userUnavailabilityRepository.findBySlotAndUser(slot, user);
    }

}
