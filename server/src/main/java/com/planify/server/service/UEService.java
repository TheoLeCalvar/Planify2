package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Lesson;
import com.planify.server.models.TAF;
import com.planify.server.models.UE;
import com.planify.server.models.UEManager;
import com.planify.server.repo.UERepository;

@Service
public class UEService {

    @Autowired
    private UERepository ueRepository;

    public UE addUE(String description, TAF taf) {
        // ads ue in the UE's table
        UE ue = new UE(description, taf);
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
            ueRepository.delete(ue);

            return true;

        }

        return false;
    }

    public Optional<UE> findById(Long id) {
        return ueRepository.findById(id);
    }

}
