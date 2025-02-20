package com.planify.server.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class Synchronization {

    @EmbeddedId
    private SynchronizationId id;

    @ManyToOne
    @MapsId("idLessonTAF1")
    private Lesson lesson1;

    @ManyToOne
    @MapsId("idLessonTAF2")
    private Lesson lesson2;

    @Embeddable
    public static class SynchronizationId implements Serializable {

        private Long idLessonTAF1;
        private Long idLessonTAF2;

        public SynchronizationId() {}

        public SynchronizationId(Long idLessonTAF1, Long idLessonTAF2) {
            this.idLessonTAF1 = idLessonTAF1;
            this.idLessonTAF2 = idLessonTAF2;
        }

        public Long getIdLesson1() {
            return this.idLessonTAF1;
        }

        public Long getIdLesson2() {
            return this.idLessonTAF2;
        }

    }

    public Synchronization() {
    }

    public Synchronization(Lesson lesson1, Lesson lesson2) {
        this.id = new SynchronizationId(lesson1.getId(),lesson2.getId());
        this.lesson1 = lesson1;
        this.lesson2 = lesson2;
    }

    public String toString() {
        return "Synchronization \n Lesson: " + this.lesson1.getId() + "\n And Lesson: " + "" + this.lesson2.getId();
    }

    public SynchronizationId getId() {
        return this.id;
    }

    public Lesson getLesson1() {
        return this.lesson1;
    }

    public void setLesson1(Lesson l) {
        this.lesson1 = l;
    }

    public Lesson getLesson2() {
        return this.lesson2;
    }

    public void setLesson2(Lesson l) {
        this.lesson2 = l;
    }

    public List<Lesson> getLessons() {
        List<Lesson> ll = new ArrayList<>();
        ll.add(this.lesson1);
        ll.add(this.lesson2);
        return ll;
    }

    public List<Long> getLessonIds() {
        List<Long> ll = new ArrayList<>();
        ll.add(this.lesson1.getId());
        ll.add(this.lesson2.getId());
        return ll;
    }

}
