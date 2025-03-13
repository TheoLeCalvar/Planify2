package com.planify.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Calendar;
import com.planify.server.models.Lesson;
import com.planify.server.models.Sequencing;
import com.planify.server.models.Synchronization;
import com.planify.server.models.TAF;
import com.planify.server.models.TAFManager;
import com.planify.server.models.UE;
import com.planify.server.repo.TAFRepository;

@Service
public class TAFService {

    @Autowired
    private TAFRepository tafRepository;

    @Lazy
    @Autowired
    private UEService ueService;

    @Lazy
    @Autowired
    private CalendarService calendarService;

    @Lazy
    @Autowired
    private TAFManagerService tafManagerService;

    public TAF addTAF(String name, String description, String beginDate, String endDate) {
        TAF taf = tafRepository.save(new TAF(name, description, beginDate, endDate));
        return taf;
    }

    public void save(TAF taf) {
        tafRepository.save(taf);
    }

    @Transactional
    public boolean deleteTAF(Long id) {
        if (tafRepository.existsById(id)) {
            TAF taf = tafRepository.findById(id).get();

            // Delete the UEs of this TAF
            List<UE> listUes = new ArrayList<UE>(taf.getUes());
            for (UE ue : listUes) {
                ueService.deleteUE(ue.getId());
            }

            // Delete the calendars associated to this TAF
            List<Calendar> listCalendars = new ArrayList<Calendar>(taf.getCalendars());
            for (Calendar c : listCalendars) {
                calendarService.deleteCalendar(c.getId());
            }

            // Delete the TAFManager of this TAF
            List<TAFManager> listManagers = new ArrayList<TAFManager>(taf.getTafManagers());
            for (TAFManager m : listManagers) {
                tafManagerService.deleteTAFManager(m.getId());
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

    public List<TAF> findAll() {
        return tafRepository.findAll();
    }

    public List<TAF> findByName(String name) {
        return tafRepository.findByName(name);
    }

    public boolean existsById(Long id) {
        return tafRepository.existsById(id);
    }

    public int numberOfLessons(Long id) {
        if (tafRepository.existsById(id)) {
            TAF taf = tafRepository.findById(id).get();
            List<UE> ues = taf.getUes();
            int n = 0;
            for (UE ue : ues) {
                n = n + ue.getLessons().size();
            }
            return n;
        } else {
            return -1;
        }
    }

    public List<Lesson> getLessonsOfTAF(Long id) {
        if (tafRepository.existsById(id)) {
            TAF taf = tafRepository.findById(id).get();
            List<UE> ues = taf.getUes();
            ArrayList<Lesson> lessons = new ArrayList<Lesson>();
            for (UE ue : ues) {
                lessons.addAll(ue.getLessons());
            }
            return lessons;
        } else {
            return new ArrayList<Lesson>();
        }
    }

}
