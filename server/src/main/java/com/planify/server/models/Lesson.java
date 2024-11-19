package com.planify.server.models;

import java.util.ArrayList;
import java.util.List;

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
    private List<LessonLecturer> lessonLecturers = new ArrayList<>();

    @OneToMany(mappedBy = "previousLesson")
    private List<Antecedence> antecedencesAsPrevious = new ArrayList<>();

    @OneToMany(mappedBy = "nextLesson")
    private List<Antecedence> antecedencesAsNext = new ArrayList<>();

    @OneToMany(mappedBy = "previousLesson")
    private List<Sequencing> sequencingsAsPrevious = new ArrayList<>();

    @OneToMany(mappedBy = "nextLesson")
    private List<Sequencing> sequencingsAsNext = new ArrayList<>();

    @OneToMany(mappedBy = "lesson1")
    private List<Synchronization> synchronizations1 = new ArrayList<>();

    @OneToMany(mappedBy = "lesson2")
    private List<Synchronization> synchronizations2 = new ArrayList<>();

    public Lesson() {}

    public Lesson(String name, UE ue) {
        this.name = name;
        this.ue = ue;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UE getUe() {
        return ue;
    }

    public void setUe(UE ue) {
        this.ue = ue;
    }

    public List<LessonLecturer> getLessonLecturers() {
        return lessonLecturers;
    }

    public void setLessonLecturers(List<LessonLecturer> lessonLecturers) {
        this.lessonLecturers = lessonLecturers;
    }

    public List<Antecedence> getAntecedencesAsPrevious() {
        return antecedencesAsPrevious;
    }

    public void setAntecedencesAsPrevious(List<Antecedence> antecedencesAsPrevious) {
        this.antecedencesAsPrevious = antecedencesAsPrevious;
    }

    public List<Antecedence> getAntecedencesAsNext() {
        return antecedencesAsNext;
    }

    public void setAntecedencesAsNext(List<Antecedence> antecedencesAsNext) {
        this.antecedencesAsNext = antecedencesAsNext;
    }

    public List<Sequencing> getSequencingsAsPrevious() {
        return sequencingsAsPrevious;
    }

    public void setSequencingsAsPrevious(List<Sequencing> sequencingsAsPrevious) {
        this.sequencingsAsPrevious = sequencingsAsPrevious;
    }

    public List<Sequencing> getSequencingsAsNext() {
        return sequencingsAsNext;
    }

    public void setSequencingsAsNext(List<Sequencing> sequencingsAsNext) {
        this.sequencingsAsNext = sequencingsAsNext;
    }

    public List<Synchronization> getSynchronizations1() {
        return synchronizations1;
    }

    public void setSynchronizations1(List<Synchronization> synchronizations1) {
        this.synchronizations1 = synchronizations1;
    }

    public List<Synchronization> getSynchronizations2() {
        return synchronizations2;
    }

    public void setSynchronizations2(List<Synchronization> synchronizations2) {
        this.synchronizations2 = synchronizations2;
    }

}
