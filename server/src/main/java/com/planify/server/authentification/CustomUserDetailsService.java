package com.planify.server.authentification;

import com.planify.server.models.User;
import com.planify.server.repo.LessonLecturerRepository;
import com.planify.server.repo.TAFManagerRepository;
import com.planify.server.repo.UEManagerRepository;
import com.planify.server.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UEManagerRepository ueManagerRepository;
    private final TAFManagerRepository tafManagerRepository;
    private final LessonLecturerRepository lessonLecturerRepository;

    public CustomUserDetailsService(UserRepository userRepository, UEManagerRepository ueManagerRepository, TAFManagerRepository tafManagerRepository, LessonLecturerRepository lessonLecturerRepository) {
        this.userRepository = userRepository;
        this.ueManagerRepository = ueManagerRepository;
        this.tafManagerRepository = tafManagerRepository;
        this.lessonLecturerRepository = lessonLecturerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByMail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
       
        List<GrantedAuthority> authorities = new ArrayList<>();

        //Check if user is admin
        if (user.getIsAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        // Check UEmanager and TAFmanager and LessonLecturer tables for roles
        if (tafManagerRepository.existsByUser(user)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_TAFMANAGER"));
        }

        if (ueManagerRepository.existsByUser(user)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_UEMANAGER"));
        }

        if (lessonLecturerRepository.existsByUser(user)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_LESSONLECTURER"));
        }
        return new org.springframework.security.core.userdetails.User(
                user.getMail(),
                user.getPassword(),
                authorities
        );
    }
}
