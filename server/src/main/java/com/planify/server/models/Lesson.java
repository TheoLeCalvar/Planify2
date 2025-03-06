package com.planify.server.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

    private String description;

    @ManyToOne
    @JoinColumn(name = "idUE")
    private UE ue;

    @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY)
    private List<LessonLecturer> lessonLecturers = new ArrayList<>();

    @OneToMany(mappedBy = "previousLesson", fetch = FetchType.LAZY)
    private List<Antecedence> antecedencesAsPrevious = new ArrayList<>();

    @OneToMany(mappedBy = "nextLesson", fetch = FetchType.LAZY)
    private List<Antecedence> antecedencesAsNext = new ArrayList<>();

    @OneToMany(mappedBy = "previousLesson", fetch = FetchType.LAZY)
    private List<Sequencing> sequencingsAsPrevious = new ArrayList<>();

    @OneToMany(mappedBy = "nextLesson", fetch = FetchType.LAZY)
    private List<Sequencing> sequencingsAsNext = new ArrayList<>();

    @OneToMany(mappedBy = "lesson1", fetch = FetchType.LAZY)
    private List<Synchronization> synchronizations1 = new ArrayList<>();

    @OneToMany(mappedBy = "lesson2", fetch = FetchType.LAZY)
    private List<Synchronization> synchronizations2 = new ArrayList<>();

    public Lesson() {
    }

    public Lesson(String name, String description, UE ue) {
        this.name = name;
        this.ue = ue;
        this.description = description;
    }

    public String toString() {
        return "Lesson " + Long.toString(this.id) + "\n Name: " + this.name + "\n UE: " + this.ue.getName();
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<Synchronization> getSynchronizations() {
        List<Synchronization> ls = this.getSynchronizations1();
        ls.addAll(0, this.getSynchronizations2());
        return ls;
    }

    public boolean isSynchronised() {
        if (this.getSynchronizations().isEmpty()) {
            return false;
        }
        return true;
    }

    public List<TAF> synchronisedWith() {
        if (this.isSynchronised()) {
            List<TAF> tafs1 = new ArrayList<>(this.getSynchronizations1().stream().flatMap(s -> s.getLessons().stream().map(l -> l.getUe().getTaf())).toList());
            List<TAF> tafs2 = new ArrayList<>(this.getSynchronizations2().stream().flatMap(s -> s.getLessons().stream().map(l -> l.getUe().getTaf())).toList());
            tafs1.addAll(0,tafs2);
            TAF thisTAF = this.ue.getTaf();
            return tafs1.stream().distinct().filter(taf -> !taf.equals(thisTAF)).toList();
        }
        else {
            return new ArrayList<>();
        }
    }

}
