package com.planify.server.models;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class Sequencing {

    @EmbeddedId
    private SequencingId id = new SequencingId(this.previousLesson.getId(), this.nextLesson.getId());

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
    public static class SequencingId implements Serializable {

        private Long idPreviousLesson;
        private Long idNextLesson;

        public SequencingId(Long idPreviousLesson, Long idNextLesson) {
            this.idPreviousLesson = idPreviousLesson;
            this.idNextLesson = idNextLesson;
        }

        public Long getIdPreviousLesson() {
            return idPreviousLesson;
        }

        public Long getIdNextLesson() {
            return idNextLesson;
        }

    }

    public Sequencing() {
    }

    public Sequencing(Lesson previousLesson, Lesson nextLesson) {
        this.previousLesson = previousLesson;
        this.nextLesson = nextLesson;
        this.id = new SequencingId(previousLesson.getId(), nextLesson.getId());
    }

    public SequencingId getId() {
        return id;
    }

    public Lesson getPreviousLesson() {
        return previousLesson;
    }

    public Lesson getNextLesson() {
        return nextLesson;
    }
}