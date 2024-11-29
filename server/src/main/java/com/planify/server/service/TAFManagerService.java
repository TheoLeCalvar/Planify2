package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.TAF;
import com.planify.server.models.TAFManager;
import com.planify.server.models.User;
import com.planify.server.models.TAFManager.TAFManagerId;
import com.planify.server.repo.TAFManagerRepository;

@Service
public class TAFManagerService {

    @Autowired
    private TAFManagerRepository tafManagerRepository;

    public TAFManager addTAFManager(User user, TAF taf) {
        // Add TAFManager to the table
        TAFManager tafManager = tafManagerRepository.save(new TAFManager(user, taf));
        return tafManager;
    }

    public void save(TAFManager tafManager) {
        tafManagerRepository.save(tafManager);
    }

    public boolean deleteTAFManager(TAFManagerId id) {
        if (tafManagerRepository.existsById(id)) {
            TAFManager tafManager = tafManagerRepository.findById(id).get();
            tafManagerRepository.delete(tafManager);
            return true;
        }

        return false;
    }

    public Optional<TAFManager> finfById(TAFManagerId id) {
        return tafManagerRepository.findById(id);
    }

}
