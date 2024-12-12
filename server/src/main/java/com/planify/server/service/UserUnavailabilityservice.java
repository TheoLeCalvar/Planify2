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

        System.out.println("SLOT ID IN ADD USER UNAVAILABILITY :" + managedSlot.getId());
        System.out.println("SLOT ID IN ADD USER UNAVAILABILITY :" + slot.getId());
        // Add userUnavailibility in the table
        UserUnavailability userUnavailability = new UserUnavailability(managedSlot, user, strict);

        System.out.println("user unavailability = " + userUnavailability.toString());
        System.out.println("id slot de user unavailability = " + "" + userUnavailability.getId().getIdSlot());
        System.out.println("id de user unavailability = " + "" + userUnavailability.getId());

        userUnavailabilityRepository.save(userUnavailability);

        System.out.println("---------------------------------------------" + slot);

        // Add it in the user's list of unavailabilities
        List<UserUnavailability> listUser = user.getUserUnavailabilities();
        listUser.addLast(userUnavailability);
        user.setUserUnavailabilities(listUser);
        userService.save(user);

        System.out.println("-----------------------CHECK1---------------------");

        // Add it in the slot's list of unavailibities
        List<UserUnavailability> listSlot = slot.getUserUnavailabilities();
        listSlot.addLast(userUnavailability);
        slot.setUserUnavailabilities(listSlot);
        slotService.save(slot);

        System.out.println("-----------------------CHECK2---------------------");
        System.out.println("user unavailability = " + userUnavailability.toString());
        System.out.println("id slot de user unavailability = " + "" + userUnavailability.getId().getIdSlot());
        System.out.println("id de user unavailability = " + "" + userUnavailability.getId());

        userUnavailabilityRepository.save(userUnavailability);

        for (UserUnavailability ua : userUnavailabilityRepository.findAll()) {
            System.out.println(ua.toString());
        }

        System.out.println("-----------------------CHECK3---------------------");
        return userUnavailability;
    }

    @Transactional
    public boolean deleteUserUnavailability(UserUnavailabilityId id) {
        if (userUnavailabilityRepository.existsById(id)) {
            UserUnavailability userUnavailability = userUnavailabilityRepository.findById(id).get();

            // Remove userUnavailability from the table
            userUnavailabilityRepository.deleteById(id);

            // Remove it from the user's list of unavailability
            User user = userService.findById(id.getIdUser()).get();
            List<UserUnavailability> listUser = user.getUserUnavailabilities();
            listUser.remove(userUnavailability);
            user.setUserUnavailabilities(listUser);
            userService.save(user);

            // Remove it from the slot's list of unavailability
            Slot slot = slotService.findById(id.getIdSlot()).get();
            List<UserUnavailability> listSlot = slot.getUserUnavailabilities();
            listSlot.remove(userUnavailability);
            slot.setUserUnavailabilities(listSlot);
            slotService.save(slot);

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

}
