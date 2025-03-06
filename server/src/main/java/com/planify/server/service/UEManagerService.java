package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.UE;
import com.planify.server.models.UEManager;
import com.planify.server.models.User;
import com.planify.server.models.UEManager.UEManagerId;
import com.planify.server.repo.UEManagerRepository;

@Service
public class UEManagerService {

    @Autowired
    private UEManagerRepository ueManagerRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UEService ueService;

    @PersistenceContext
    private EntityManager entityManager;

    public UEManager addUEManager(User user, UE ue) {
        UEManager ueManager = new UEManager(user, ue);
        ueManager = ueManagerRepository.save(ueManager);
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

    public List<UEManager> findAll() {
        return ueManagerRepository.findAll();
    }

}
