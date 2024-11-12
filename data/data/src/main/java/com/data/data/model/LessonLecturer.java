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
public class LessonLecturer {
    
    @EmbeddedId
    private LessonLecturerId id = new LessonLecturerId();

    @ManyToOne
    @MapsId("idUser")
    @JoinColumn(name = "idUser")
    private User user;

    @ManyToOne
    @MapsId("idLesson")
    @JoinColumn(name = "idLesson")
    private Lesson lesson;

    // Composite Key class
    @Embeddable
    public static class LessonLecturerId implements Serializable {
        private Long idUser;
        private Long idLesson;

        // equals and hashCode
    }

}
