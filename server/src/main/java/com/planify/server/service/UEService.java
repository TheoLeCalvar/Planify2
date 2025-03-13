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
import com.planify.server.models.constraints.ConstraintsOfUE;
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
    
    @Autowired
    private ConstraintsOfUEService constraintsOfUEService;

    @Transactional
    public UE addUE(String name, String description, TAF taf) {
        // ads ue in the UE's table
        UE ue = new UE(name, description, taf);

        ueRepository.save(ue);
        return ue;
    }

    public void save(UE ue) {
        ueRepository.save(ue);
    }

    @Transactional
    public boolean deleteUE(Long id) {
        if (ueRepository.existsById(id)) {
            UE ue = ueRepository.findById(id).get();

            // Delete the lesson associated with this UE
            List<Lesson> listLessons = new ArrayList<Lesson>(ue.getLessons());
            for (Lesson l : listLessons) {
                lessonService.delete(l.getId());
            }

            // Delete the UE Managers associated with this UE
            List<UEManager> listUeManagers = ue.getUEManagers();
            if (listUeManagers != null && !listUeManagers.isEmpty()) {
                listUeManagers.forEach(uem -> ueManagerService.deleteUEManager(uem.getId()));
            }
            
            List<ConstraintsOfUE> listCUE = ue.getConstraintsOfUE();
            if (listCUE != null && !listCUE.isEmpty()) {
            	listCUE.forEach(cUe -> constraintsOfUEService.deleteById(cUe.getId()));
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
