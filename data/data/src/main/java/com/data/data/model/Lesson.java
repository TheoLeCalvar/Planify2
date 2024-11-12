package com.data.data.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
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

    @OneToMany(mappedBy = "lesson")
    private List<Antecedence> antecedences;

    @OneToMany(mappedBy = "lesson")
    private List<Sequencing> sequencings;

    @OneToMany(mappedBy = "lesson")
    private List<Synchronization> synchronizations;
}
