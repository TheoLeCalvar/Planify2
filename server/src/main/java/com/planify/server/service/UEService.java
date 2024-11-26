package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.Lesson;
import com.planify.server.models.TAF;
import com.planify.server.models.UE;
import com.planify.server.models.UEManager;
import com.planify.server.repo.LessonRepository;
import com.planify.server.repo.TAFRepository;
import com.planify.server.repo.UEManagerRepository;
import com.planify.server.repo.UERepository;

@Service
public class UEService {

    @Autowired
    private UERepository ueRepository;

    @Autowired
    private TAFRepository tafRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private UEManagerRepository ueManagerRepository;

    public UE addUE(String description, TAF taf) {
        // ads ue in the UE's table
        UE ue = new UE(description, taf);
        ueRepository.save(ue);

        // add ue in the TAF's ues
        List<UE> ues = taf.getUes();
        ues.addLast(ue);
        taf.setUes(ues);
        tafRepository.save(taf);

        return ue;
    }

    public void save(UE ue) {
        ueRepository.save(ue);
    }

    public boolean deleteUE(Long id) {
        if (ueRepository.existsById(id)) {
            UE ue = ueRepository.findById(id).get();

            // Delete ue in the TAF's ues
            TAF taf = tafRepository.findById(ue.getTaf().getId()).get();
            List<UE> listUEfromTAF = taf.getUes();
            listUEfromTAF.remove(ue);
            taf.setUes(listUEfromTAF);
            tafRepository.save(taf);

            // Delete the lesson associated with this UE
            List<Lesson> listLessons = ue.getLessons();
            for (Lesson l : listLessons) {
                lessonRepository.delete(l);
            }

            // Delete the UE Managers associated with this UE
            List<UEManager> listUeManagers = ue.getUeManagers();
            for (UEManager m : listUeManagers) {
                ueManagerRepository.delete(m);
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

}
