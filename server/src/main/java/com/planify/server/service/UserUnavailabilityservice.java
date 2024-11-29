package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Slot;
import com.planify.server.models.User;
import com.planify.server.models.UserUnavailability;
import com.planify.server.models.UserUnavailability.UserUnavailabilityId;
import com.planify.server.repo.UserUnavailabilityRepository;

@Service
public class UserUnavailabilityService {

    @Autowired
    private UserUnavailabilityRepository userUnavailabilityRepository;

    public UserUnavailability addUserUnavailability(Slot slot, User user, boolean strict) {
        // Add userUnavailibility in the table
        UserUnavailability userUnavailability = userUnavailabilityRepository
                .save(new UserUnavailability(slot, user, strict));

        return userUnavailability;
    }

    @Transactional
    public boolean deleteUserUnavailability(UserUnavailabilityId id) {
        if (userUnavailabilityRepository.existsById(id)) {

            // Remove userUnavailability from the table
            userUnavailabilityRepository.deleteById(id);

            return true;
        }
        return false;
    }

    public Optional<UserUnavailability> findById(UserUnavailabilityId id) {
        return userUnavailabilityRepository.findById(id);
    }

    public List<UserUnavailability> findBySlot(Slot s) {
        return userUnavailabilityRepository.findBySlot(s);
    }

    public void save(UserUnavailability userUnavailability) {
        userUnavailabilityRepository.save(userUnavailability);
    }

}
