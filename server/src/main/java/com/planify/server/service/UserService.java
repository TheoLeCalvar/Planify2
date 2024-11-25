package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.LessonLecturer;
import com.planify.server.models.TAFManager;
import com.planify.server.models.UEManager;
import com.planify.server.models.User;
import com.planify.server.models.UserUnavailability;
import com.planify.server.repo.LessonLecturerRepository;
import com.planify.server.repo.TAFManagerRepository;
import com.planify.server.repo.UEManagerRepository;
import com.planify.server.repo.UserRepository;
import com.planify.server.repo.UserUnavailabilityRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserUnavailabilityRepository userUnavailabilityRepository;

    @Autowired
    private LessonLecturerRepository lessonLecturerRepository;

    @Autowired
    private UEManagerRepository ueManagerRepository;

    @Autowired
    private TAFManagerRepository tafManagerRepository;

    public User addUser(String name, String lastName, String mail, char[] password) {
        User user = userRepository.save(new User(name, lastName, mail, password));
        return user;
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            User user = userRepository.findById(id).get();

            // delete user in the userUnavailibility table
            List<UserUnavailability> listUnavailibility = userUnavailabilityRepository.findByUser(user);
            for (UserUnavailability unavailability : listUnavailibility) {
                userUnavailabilityRepository.deleteById(unavailability.getId());
            }

            // delete user in the LessonLecturer table
            List<LessonLecturer> lessonLecturers = lessonLecturerRepository.findByUser(user);
            for (LessonLecturer lessonLecturer : lessonLecturers) {
                lessonLecturerRepository.deleteById(lessonLecturer.getId());
            }

            // delete user in the UEManager table
            List<UEManager> ueManagers = ueManagerRepository.findByUser(user);
            for (UEManager ueManager : ueManagers) {
                ueManagerRepository.deleteById(ueManager.getId());
            }

            // delete user in the TAFManager
            List<TAFManager> tafManagers = tafManagerRepository.findByUser(user);
            for (TAFManager tafManager : tafManagers) {
                tafManagerRepository.deleteById(tafManager.getId());
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

}
