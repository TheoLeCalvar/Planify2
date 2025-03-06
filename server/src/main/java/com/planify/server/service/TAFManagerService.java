package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.hibernate.proxy.HibernateProxy;
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
        TAFManager tafManager = new TAFManager(user, taf);
        tafManagerRepository.save(tafManager);
        return tafManager;
    }

    public void save(TAFManager tafManager) {
        tafManagerRepository.save(tafManager);
    }

    public boolean deleteTAFManager(TAFManagerId id) {
        if (tafManagerRepository.existsById(id)) {
            TAFManager tafManager = tafManagerRepository.findById(id).get();
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
