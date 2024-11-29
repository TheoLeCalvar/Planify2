package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.LessonLecturer;
import com.planify.server.models.TAFManager;
import com.planify.server.models.UEManager;
import com.planify.server.models.User;
import com.planify.server.models.UserUnavailability;
import com.planify.server.repo.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User addUser(String name, String lastName, String mail, char[] password) {
        User user = userRepository.save(new User(name, lastName, mail, password));
        return user;
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            User user = userRepository.findById(id).get();
            userRepository.delete(user);

            return true;

        }

        return false;

    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void save(User user) {
        userRepository.save(user);
    }

}
