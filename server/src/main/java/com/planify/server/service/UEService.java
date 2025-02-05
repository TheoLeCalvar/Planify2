package com.planify.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Lesson;
import com.planify.server.models.Synchronization;
import com.planify.server.models.TAF;
import com.planify.server.models.UE;
import com.planify.server.models.UEManager;
import com.planify.server.repo.UERepository;

@Service
public class UEService {

    @Autowired
    private UERepository ueRepository;

    @Autowired
    private TAFService tafService;

    @Lazy
    @Autowired
    private LessonService lessonService;

    @Lazy
    @Autowired
    private UEManagerService ueManagerService;

    @Transactional
    public UE addUE(String name, String description, TAF taf) {
        System.out.println("------------------------" + ueRepository.findAll());
        // ads ue in the UE's table
        UE ue = new UE(name, description, taf);

        // add ue in the TAF's ues
        List<UE> ues = taf.getUes();
        ues.addLast(ue);
        taf.setUes(ues);
        // tafService.save(taf);

        ueRepository.save(ue);
        System.out.println(ueRepository.findAll());
        System.out.println(taf.getUes());
        return ue;
    }

    public void save(UE ue) {
        ueRepository.save(ue);
    }

    @Transactional
    public boolean deleteUE(Long id) {
        if (ueRepository.existsById(id)) {
            UE ue = ueRepository.findById(id).get();

            // Delete ue in the TAF's ues
            TAF taf = tafService.findById(ue.getTaf().getId()).get();
            List<UE> listUEfromTAF = taf.getUes();
            listUEfromTAF.remove(ue);
            taf.setUes(listUEfromTAF);
            tafService.save(taf);

            // Delete the lesson associated with this UE
            List<Lesson> listLessons = new ArrayList<Lesson>(ue.getLessons());
            for (Lesson l : listLessons) {
                lessonService.delete(l.getId());
            }

            // Delete the UE Managers associated with this UE
            List<UEManager> listUeManagers = ue.getUeManagers();
            if (listUeManagers != null) {
                for (UEManager m : listUeManagers) {
                    ueManagerService.deleteUEManager(m.getId());
                }
            }


            // Delete ue in the UE's table
            ueRepository.delete(ue);

            return true;

        }

        return false;
    }

    public Optional<UE> findById(Long id) {
        return ueRepository.findById(id);
    }

    public List<UE> findAll() {
        return ueRepository.findAll();
    }

    public boolean existsById(Long id) {
        return ueRepository.existsById(id);
    }
}
