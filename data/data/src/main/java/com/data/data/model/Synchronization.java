package com.data.data.model;

import java.io.Serializable;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Synchronization {
   
    @EmbeddedId
    private SynchronizationId id;

    @ManyToOne
    @JoinColumn(name = "id")  
    private Lesson lesson1 ;

    @ManyToOne
    @JoinColumn(name = "id")  
    private Lesson lesson2;

    @Embeddable
    private class SynchronizationId implements Serializable{

        private Long idLessonTAF1;
        private Long idLessonTAF2;

        public SynchronizationId(Long idLessonTAF1, Long idLessonTAF2) {
            this.idLessonTAF1 = idLessonTAF1;
            this.idLessonTAF2 = idLessonTAF2;
        }


}
