package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.TAF;
import com.planify.server.models.TAFManager;
import com.planify.server.models.User;
import com.planify.server.models.TAFManager.TAFManagerId;
import com.planify.server.repo.TAFManagerRepository;
import com.planify.server.repo.TAFRepository;
import com.planify.server.repo.UserRepository;

@Service
public class TAFManagerService {

    @Autowired
    private TAFManagerRepository tafManagerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TAFRepository tafRepository;

    public TAFManager addTAFManager(User user, TAF taf) {
        // Add TAFManager to the table
        TAFManager tafManager = tafManagerRepository.save(new TAFManager(user, taf));

        // Add the TAFManager to the user's list of TAFManager
        List<TAFManager> tafManagers = user.getTafManagers();
        tafManagers.addLast(tafManager);
        user.setTafManagers(tafManagers);
        userRepository.save(user);

        // Add the TAFManager to the TAF's list of TafManager
        List<TAFManager> tafManagers2 = taf.getTafManagers();
        tafManagers2.addLast(tafManager);
        taf.setTafManagers(tafManagers2);
        tafRepository.save(taf);

        return tafManager;
    }

    public boolean deleteTAFManager(TAFManagerId id) {
        if (tafManagerRepository.existsById(id)) {
            TAFManager tafManager = tafManagerRepository.findById(id).get();

            // delete the TAFManager from the TAF's list of TAFManager
            TAF taf = tafRepository.findById(tafManager.getTaf().getId()).get();
            List<TAFManager> listManagerFromTAF = taf.getTafManagers();
            listManagerFromTAF.remove(tafManager);
            taf.setTafManagers(listManagerFromTAF);
            tafRepository.save(taf);

            // Delete the TAFManager in the User's UeManagers
            User user = userRepository.findById(tafManager.getUser().getId()).get();
            List<TAFManager> listManagerFromUser = user.getTafManagers();
            listManagerFromUser.remove(tafManager);
            user.setTafManagers(listManagerFromUser);
            userRepository.save(user);

            // Delete the taf manager in the TAFMAnager's table
            tafManagerRepository.delete(tafManager);

            return true;
        }

        return false;
    }

    public Optional<TAFManager> finfById(TAFManagerId id) {
        return tafManagerRepository.findById(id);
    }

}
