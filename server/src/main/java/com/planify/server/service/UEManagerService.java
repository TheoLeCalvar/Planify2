package com.planify.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.UE;
import com.planify.server.models.UEManager;
import com.planify.server.models.User;
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

    public UEManager addUeManager(User user, UE ue) {
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

}
