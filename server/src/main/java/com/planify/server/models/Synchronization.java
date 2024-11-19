package com.planify.server.models;

import java.io.Serializable;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class Synchronization {

    @EmbeddedId
    private SynchronizationId id = new SynchronizationId(this.lesson1.getId(), this.lesson2.getId());

    @ManyToOne
    @MapsId("idLessonTAF1")
    @JoinColumn(name = "idLessonTAF1")
    private Lesson lesson1;

    @ManyToOne
    @MapsId("idLessonTAF2")
    @JoinColumn(name = "idLessonTAF2")
    private Lesson lesson2;

    @Embeddable
    public class SynchronizationId implements Serializable {

        private Long idLessonTAF1;
        private Long idLessonTAF2;

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
        this.lesson1 = lesson1;
        this.lesson2 = lesson2;
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

}
