package com.planify.server.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "idUE")
    private UE ue;

    @OneToMany(mappedBy = "lesson")
    private List<LessonLecturer> lessonLecturers;

    @OneToMany(mappedBy = "previousLesson")
    private List<Antecedence> antecedencesAsPrevious = new ArrayList<>();

    @OneToMany(mappedBy = "nextLesson")
    private List<Antecedence> antecedencesAsNext = new ArrayList<>();

    @OneToMany(mappedBy = "previousLesson")
    private List<Sequencing> sequencingsAsPrevious = new ArrayList<>();

    @OneToMany(mappedBy = "nextLesson")
    private List<Sequencing> sequencingsAsNext = new ArrayList<>();

    @OneToMany(mappedBy = "lesson1")
    private List<Synchronization> synchronizations1;

    @OneToMany(mappedBy = "lesson2")
    private List<Synchronization> synchronizations2;

    public long getId() {
        return id;
    }

}
