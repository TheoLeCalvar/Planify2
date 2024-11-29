package com.planify.server.models;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class Antecedence {

    @EmbeddedId
    private AntecedenceId id = new AntecedenceId(this.previousLesson.getId(), this.nextLesson.getId());

    @ManyToOne
    @MapsId("idPreviousLesson")
    @JoinColumn(name = "idPreviousLesson")
    private Lesson previousLesson;

    @ManyToOne
    @MapsId("idNextLesson")
    @JoinColumn(name = "idNextLesson")
    private Lesson nextLesson;

    // Composite Key class
    @Embeddable
    public static class AntecedenceId implements Serializable {
        private Long idPreviousLesson;
        private Long idNextLesson;

        public AntecedenceId(long idPreviousLesson, long idNextLesson) {
            this.idPreviousLesson = idPreviousLesson;
            this.idNextLesson = idNextLesson;
        }

        public Long getIdPreviousLesson() {
            return this.idPreviousLesson;
        }

        public Long getIdNextLesson() {
            return this.idNextLesson;
        }
    }

    public String toString() {
        return "Antecedence \n Previous Lesson: " + this.previousLesson.getName() + "\n Next Lesson: " + ""
                + this.getNextLesson().getName();
    }

    public AntecedenceId getId() {
        return this.id;
    }

    public Lesson getPreviousLesson() {
        return this.previousLesson;
    }

    public Lesson getNextLesson() {
        return this.nextLesson;
    }

    public Antecedence() {

    }

    public Antecedence(Lesson previousLesson, Lesson nextLesson) {
        this.previousLesson = previousLesson;
        this.nextLesson = nextLesson;
    }

}
