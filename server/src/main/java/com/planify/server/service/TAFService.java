package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Calendar;
import com.planify.server.models.TAF;
import com.planify.server.models.TAFManager;
import com.planify.server.models.UE;
import com.planify.server.repo.TAFRepository;

@Service
public class TAFService {

    @Autowired
    private TAFRepository tafRepository;

    public TAF addTAF(String name) {
        TAF taf = tafRepository.save(new TAF(name));
        return taf;
    }

    public void save(TAF taf) {
        tafRepository.save(taf);
    }

    @Transactional
    public boolean deleteTAF(Long id) {
        if (tafRepository.existsById(id)) {
            TAF taf = tafRepository.findById(id).get();
            // Delete the taf in the TAF's table
            tafRepository.delete(taf);

            return true;
        }

        return false;
    }

    public Optional<TAF> findById(Long id) {
        return tafRepository.findById(id);
    }

    public List<TAF> findByName(String name) {
        return tafRepository.findByName(name);
    }

}