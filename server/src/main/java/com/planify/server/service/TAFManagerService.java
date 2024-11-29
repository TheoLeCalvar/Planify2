package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Synchronization;
import com.planify.server.models.TAF;
import com.planify.server.models.TAFManager;
import com.planify.server.models.User;
import com.planify.server.models.TAFManager.TAFManagerId;
import com.planify.server.repo.TAFManagerRepository;

@Service
public class TAFManagerService {

    @Autowired
    private TAFManagerRepository tafManagerRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TAFService tafService;

    @Transactional
    public TAFManager addTAFManager(User user, TAF taf) {
        // Add TAFManager to the table
        TAFManager tafManager = tafManagerRepository.save(new TAFManager(user, taf));

        // Add the TAFManager to the user's list of TAFManager
        List<TAFManager> tafManagers = user.getTafManagers();
        tafManagers.addLast(tafManager);
        user.setTafManagers(tafManagers);
        userService.save(user);

        // Add the TAFManager to the TAF's list of TafManager
        List<TAFManager> tafManagers2 = taf.getTafManagers();
        tafManagers2.addLast(tafManager);
        taf.setTafManagers(tafManagers2);
        tafService.save(taf);

        return tafManager;
    }

    public void save(TAFManager tafManager) {
        tafManagerRepository.save(tafManager);
    }

    public boolean deleteTAFManager(TAFManagerId id) {
        if (tafManagerRepository.existsById(id)) {
            TAFManager tafManager = tafManagerRepository.findById(id).get();

            // delete the TAFManager from the TAF's list of TAFManager
            TAF taf = tafService.findById(tafManager.getTaf().getId()).get();
            List<TAFManager> listManagerFromTAF = taf.getTafManagers();
            listManagerFromTAF.remove(tafManager);
            taf.setTafManagers(listManagerFromTAF);
            tafService.save(taf);

            // Delete the TAFManager in the User's UeManagers
            User user = userService.findById(tafManager.getUser().getId()).get();
            List<TAFManager> listManagerFromUser = user.getTafManagers();
            listManagerFromUser.remove(tafManager);
            user.setTafManagers(listManagerFromUser);
            userService.save(user);

            // Delete the taf manager in the TAFMAnager's table
            tafManagerRepository.delete(tafManager);

            return true;
        }

        return false;
    }

    public Optional<TAFManager> finfById(TAFManagerId id) {
        return tafManagerRepository.findById(id);
    }

    public List<TAFManager> findAll() {
        return tafManagerRepository.findAll();
    }

}
