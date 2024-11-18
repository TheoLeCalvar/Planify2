package com.planify.server.models;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "user")
    private List<UserUnavailability> userunavailabilities;

    @OneToMany(mappedBy = "user")
    private List<LessonLecturer> lessonLecturers;

    @OneToMany(mappedBy = "user")
    private List<UEManager> ueManagers;

    @OneToMany(mappedBy = "user")
    private List<TAFManager> tafManagers;

    public long getId() {
        return id;
    }
}
