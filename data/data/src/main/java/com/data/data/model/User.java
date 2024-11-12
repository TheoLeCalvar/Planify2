package com.data.data.model;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "userunavailability")
    private List<UserUnavailability> userunavailabilities;

    @OneToMany(mappedBy = "lessonlecturer")
    private List<LessonLecturer> lessonLecturers;

    @OneToMany(mappedBy = "uemanager")
    private List<UEManager> ueManagers;

    @OneToMany(mappedBy = "tafmanager")
    private List<TAFManager> tafManagers;
}
