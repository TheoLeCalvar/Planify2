package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.TAF;
import com.planify.server.models.TAFManager;
import com.planify.server.models.UE;
import com.planify.server.repo.CalendarRepository;
import com.planify.server.repo.TAFManagerRepository;
import com.planify.server.repo.TAFRepository;
import com.planify.server.repo.UERepository;

@Service
public class TAFService {

    @Autowired
    private TAFRepository tafRepository;

    @Autowired
    private UERepository ueRepository;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private TAFManagerRepository tafManagerRepository;

    public TAF addTAF(String name) {
        TAF taf = tafRepository.save(new TAF(name));
        return taf;
    }

    public boolean deleteTAF(Long id) {
        if (tafRepository.existsById(id)) {
            TAF taf = tafRepository.findById(id).get();

        }

        return false;
    }

    public Optional<TAF> findById(Long id) {
        return tafRepository.findById(id);
    }

}
