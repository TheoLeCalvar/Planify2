package com.planify.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.TAF;
import com.planify.server.models.UE;
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
        // ad ue in the UE's table
        UE ue = new UE(description, taf);
        ueRepository.save(ue);

        // add ue int the TAF's ues
        List<UE> ues = taf.getUes();
        ues.addLast(ue);
        taf.setUes(ues);
        tafRepository.save(taf);

        return ue;
    }

}
