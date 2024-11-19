package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.Slot;
import com.planify.server.models.User;
import com.planify.server.models.UserUnavailability;
import com.planify.server.models.UserUnavailability.UserUnavailabilityId;
import com.planify.server.repo.SlotRepository;
import com.planify.server.repo.UserRepository;
import com.planify.server.repo.UserUnavailabilityRepository;

@Service
public class UserUnavailabilityservice {

    @Autowired
    private UserUnavailabilityRepository userUnavailabilityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SlotRepository slotRepository;

    public UserUnavailability addUserUnavailability(Slot slot, User user, boolean strict) {
        // Add userUnavailibility in the table
        UserUnavailability userUnavailability = userUnavailabilityRepository
                .save(new UserUnavailability(slot, user, strict));

        // Add it in the user's list of unavailabilities
        List<UserUnavailability> listUser = user.getUserUnavailabilities();
        listUser.addLast(userUnavailability);
        user.setUserUnavailabilities(listUser);
        userRepository.save(user);

        // Add it in the slot's list of unavailibities
        List<UserUnavailability> listSlot = slot.getUserUnavailabilities();
        listSlot.addLast(userUnavailability);
        slot.setUserUnavailabilities(listSlot);
        slotRepository.save(slot);

        return userUnavailability;
    }

    public boolean deleteUserUnavailability(UserUnavailabilityId id) {
        if (userUnavailabilityRepository.existsById(id)) {
            UserUnavailability userUnavailability = userUnavailabilityRepository.findById(id).get();

            // Remove userUnavailability from the table
            userUnavailabilityRepository.deleteById(id);

            // Remove it from the user's list of unavailability
            User user = userRepository.findById(id.getIdUser()).get();
            List<UserUnavailability> listUser = user.getUserUnavailabilities();
            listUser.remove(userUnavailability);
            user.setUserUnavailabilities(listUser);
            userRepository.save(user);

            // Remove it from the slot's list of unavailability
            Slot slot = slotRepository.findById(id.getIdSlot()).get();
            List<UserUnavailability> listSlot = slot.getUserUnavailabilities();
            listSlot.remove(userUnavailability);
            slot.setUserUnavailabilities(listSlot);
            slotRepository.save(slot);

            return true;
        }
        return false;
    }

    public Optional<UserUnavailability> findById(UserUnavailabilityId id) {
        return userUnavailabilityRepository.findById(id);
    }
}
