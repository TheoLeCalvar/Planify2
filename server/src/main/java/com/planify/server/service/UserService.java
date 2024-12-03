package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.LessonLecturer;
import com.planify.server.models.Synchronization;
import com.planify.server.models.TAFManager;
import com.planify.server.models.UEManager;
import com.planify.server.models.User;
import com.planify.server.models.UserUnavailability;
import com.planify.server.repo.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Lazy
    @Autowired
    private UserUnavailabilityService userUnavailabilityService;

    @Lazy
    @Autowired
    private LessonLecturerService lessonLecturerService;

    @Lazy
    @Autowired
    private UEManagerService ueManagerService;

    @Lazy
    @Autowired
    private TAFManagerService tafManagerService;

    public User addUser(String name, String lastName, String mail, char[] password) {
        User user = userRepository.save(new User(name, lastName, mail, password));
        return user;
    }

    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            User user = userRepository.findById(id).get();

            // delete user in the userUnavailibility table
            List<UserUnavailability> listUnavailibility = user.getUserUnavailabilities();
            for (UserUnavailability unavailability : listUnavailibility) {
                userUnavailabilityService.deleteUserUnavailability(unavailability.getId());
            }

            // delete user in the LessonLecturer table
            List<LessonLecturer> lessonLecturers = user.getLessonLecturers();
            for (LessonLecturer lessonLecturer : lessonLecturers) {
                lessonLecturerService.deleteLessonLecturer(lessonLecturer.getId());
            }

            // delete user in the UEManager table
            List<UEManager> ueManagers = user.getUeManagers();
            for (UEManager ueManager : ueManagers) {
                ueManagerService.deleteUEManager(ueManager.getId());
            }

            // delete user in the TAFManager
            List<TAFManager> tafManagers = user.getTafManagers();
            for (TAFManager tafManager : tafManagers) {
                tafManagerService.deleteTAFManager(tafManager.getId());
            }

            // delete the user in the user table
            userRepository.delete(user);

            return true;

        }

        return false;

    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void save(User user) {
        userRepository.save(user);
    }

}
