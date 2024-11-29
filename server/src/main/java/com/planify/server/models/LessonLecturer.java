package com.planify.server.models;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class LessonLecturer {

    @EmbeddedId
    private LessonLecturerId id = new LessonLecturerId(this.user.getId(), this.lesson.getId());

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

        public LessonLecturerId(long idUser, long idLesson) {
            this.idUser = idUser;
            this.idLesson = idLesson;
        }

        public Long getIdUser() {
            return this.idUser;
        }

        public Long getIdLesson() {
            return this.idLesson;
        }
    }

    public LessonLecturer() {
    }

    public LessonLecturer(User user, Lesson lesson) {
        this.user = user;
        this.lesson = lesson;
    }

    public String toString() {
        return "Week \n Lecturer: " + this.user.getName() + "\n Lesson: " + this.lesson.getName();
    }

    public LessonLecturerId getId() {
        return this.id;
    }

    public User getUser() {
        return this.user;
    }

    public Lesson getLesson() {
        return this.lesson;
    }

}
