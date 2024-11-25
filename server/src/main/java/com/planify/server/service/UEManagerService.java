package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.UE;
import com.planify.server.models.UEManager;
import com.planify.server.models.User;
import com.planify.server.models.UEManager.UEManagerId;
import com.planify.server.repo.UEManagerRepository;
import com.planify.server.repo.UERepository;
import com.planify.server.repo.UserRepository;

@Service
public class UEManagerService {

    @Autowired
    private UEManagerRepository ueManagerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UERepository ueRepository;

    public UEManager addUEManager(User user, UE ue) {
        // Add UEManager to the table
        UEManager ueManager = ueManagerRepository.save(new UEManager(user, ue));

        // Add the UEmanager to the user's list of UEManager
        List<UEManager> ueManagers = user.getUeManagers();
        ueManagers.addLast(ueManager);
        user.setUeManagers(ueManagers);
        userRepository.save(user);

        // Add the UEmanager to the ue's list of UEManager
        List<UEManager> listUE = ue.getUeManagers();
        listUE.addLast(ueManager);
        ue.setUeManagers(listUE);
        ueRepository.save(ue);

        return ueManager;
    }

    public boolean deleteUEManager(UEManagerId id) {
        if (ueManagerRepository.existsById(id)) {
            UEManager ueManager = ueManagerRepository.findById(id).get();

            // Delete the UEManager in the UE's UeManagers
            UE ue = ueRepository.findById(ueManager.getUe().getId()).get();
            List<UEManager> listManagerFromUE = ue.getUeManagers();
            listManagerFromUE.remove(ueManager);
            ue.setUeManagers(listManagerFromUE);
            ueRepository.save(ue);

            // Delete the UEManager in the User's UeManagers
            User user = userRepository.findById(ueManager.getUser().getId()).get();
            List<UEManager> listManagerFromUser = user.getUeManagers();
            listManagerFromUser.remove(ueManager);
            user.setUeManagers(listManagerFromUser);
            userRepository.save(user);

            // Delete the ue's manager in the UEManager's table
            ueManagerRepository.delete(ueManager);

            return true;
        }

        return false;
    }

    public Optional<UEManager> findById(UEManagerId id) {
        return ueManagerRepository.findById(id);
    }

}
