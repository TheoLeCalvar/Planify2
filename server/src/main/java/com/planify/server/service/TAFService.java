package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.Calendar;
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

    public void save(TAF taf) {
        tafRepository.save(taf);
    }

    public boolean deleteTAF(Long id) {
        if (tafRepository.existsById(id)) {
            TAF taf = tafRepository.findById(id).get();

            // Delete the UEs of this TAF
            List<UE> listUes = taf.getUes();
            for (UE ue : listUes) {
                ueRepository.delete(ue);
            }

            // Delete the calendars associated to this TAF
            List<Calendar> listCalendars = taf.getCalendars();
            for (Calendar c : listCalendars) {
                calendarRepository.delete(c);
            }

            // Delete the TAFManager of this TAF
            List<TAFManager> listManagers = taf.getTafManagers();
            for (TAFManager m : listManagers) {
                tafManagerRepository.delete(m);
            }

            // Delete the taf in the TAF's table
            tafRepository.delete(taf);

            return true;
        }

        return false;
    }

    public Optional<TAF> findById(Long id) {
        return tafRepository.findById(id);
    }

}
