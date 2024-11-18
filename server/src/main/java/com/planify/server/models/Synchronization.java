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
    private class SynchronizationId implements Serializable {

        private Long idLessonTAF1;
        private Long idLessonTAF2;

        public SynchronizationId(Long idLessonTAF1, Long idLessonTAF2) {
            this.idLessonTAF1 = idLessonTAF1;
            this.idLessonTAF2 = idLessonTAF2;
        }
    }
}
