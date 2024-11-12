package com.data.data.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Data;

@Entity
@Data
public class Antecedence {

    @EmbeddedId
    private AntecedenceId id = new AntecedenceId(this.previousLesson.getId(),this.nextLesson.getId());

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
    @Data
    public static class AntecedenceId implements Serializable {
        private Long idPreviousLesson;
        private Long idNextLesson;

        public AntecedenceId(long idPreviousLesson, long idNextLesson) {
            this.idPreviousLesson=idPreviousLesson;
            this.idNextLesson=idNextLesson;
        }
    }
    
}
