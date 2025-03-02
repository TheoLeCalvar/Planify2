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
        // Add UEManager to the table
        System.out.println("CHECK10");
        UEManager ueManager = new UEManager(user, ue);
        System.out.println(ueManager.toString());
        System.out.println("user.id = " + "" + ueManager.getId().getIdUser());
        System.out.println("ue.id = " + "" + ueManager.getId().getIdUE());
        System.out.println("CHECK11");
        ueManager = ueManagerRepository.save(ueManager);

        System.out.println("CHECK12");
        // Add the UEmanager to the user's list of UEManager
        List<UEManager> ueManagers = user.getUeManagers();
        ueManagers.addLast(ueManager);
        user.setUeManagers(ueManagers);
        // userService.save(user);

        System.out.println("CHECK13");
        // Add the UEmanager to the ue's list of UEManager
        List<UEManager> listUE = ue.getUeManagers();
        listUE.addLast(ueManager);
        ue.setUeManagers(listUE);
        // ueService.save(ue);

        System.out.println("CHECK14");
        return ueManager;
    }

    public void save(UEManager manager) {
        ueManagerRepository.save(manager);
    }

    public boolean deleteUEManager(UEManagerId id) {
        if (ueManagerRepository.existsById(id)) {
            UEManager ueManager = ueManagerRepository.findById(id).get();

            // Delete the UEManager in the UE's UeManagers
            UE ue = ueService.findById(ueManager.getUe().getId()).get();
            List<UEManager> listManagerFromUE = ue.getUeManagers();
            listManagerFromUE.remove(ueManager);
            ue.setUeManagers(listManagerFromUE);
            ueService.save(ue);

            // Delete the UEManager in the User's UeManagers
            User user = userService.findById(ueManager.getUser().getId()).get();
            List<UEManager> listManagerFromUser = user.getUeManagers();
            listManagerFromUser.remove(ueManager);
            user.setUeManagers(listManagerFromUser);
            userService.save(user);

            // Delete the ue's manager in the UEManager's table
            ueManagerRepository.delete(ueManager);

            return true;
        }

        return false;
    }

    public boolean deleteUEManagerWhenDeletingUE(UEManagerId id) {
        if (ueManagerRepository.existsById(id)) {
            UEManager ueManager = ueManagerRepository.findById(id).get();

            // Delete the UEManager in the User's UeManagers
            User user = userService.findById(ueManager.getUser().getId()).get();
            List<UEManager> listManagerFromUser = user.getUeManagers();
            System.out.println("user.managers avant : ");
            for (UEManager uem : listManagerFromUser) {
                System.out.println(uem.toString());
            }
            listManagerFromUser.remove(ueManager);
            user.setUeManagers(listManagerFromUser);
            userService.save(user);
            System.out.println("user.managers après : ");
            User user1 = userService.findById(ueManager.getUser().getId()).get();
            List<UEManager> listManagerFromUser1 = user1.getUeManagers();
            for (UEManager uem : listManagerFromUser1) {
                System.out.println(uem.toString());
            }

            // Delete the ue's manager in the UEManager's table
            ueManagerRepository.delete(ueManager);

            entityManager.refresh(user);

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
