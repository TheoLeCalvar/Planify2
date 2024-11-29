package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.UE;
import com.planify.server.models.UEManager;
import com.planify.server.models.User;
import com.planify.server.models.UEManager.UEManagerId;
import com.planify.server.repo.UEManagerRepository;

@Service
public class UEManagerService {

    @Autowired
    private UEManagerRepository ueManagerRepository;

    public UEManager addUEManager(User user, UE ue) {
        // Add UEManager to the table
        UEManager ueManager = ueManagerRepository.save(new UEManager(user, ue));
        return ueManager;
    }

    public void save(UEManager manager) {
        ueManagerRepository.save(manager);
    }

    public boolean deleteUEManager(UEManagerId id) {
        if (ueManagerRepository.existsById(id)) {
            UEManager ueManager = ueManagerRepository.findById(id).get();
            // Delete the ue's manager in the UEManager's table
            ueManagerRepository.delete(ueManager);

            return true;
        }

        return false;
    }

    public Optional<UEManager> findById(UEManagerId id) {
        return ueManagerRepository.findById(id);
    }

}
