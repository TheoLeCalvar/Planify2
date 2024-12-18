package com.planify.server.models;

import java.io.Serializable;

import com.planify.server.models.LessonLecturer.LessonLecturerId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class LessonLecturer {

    @EmbeddedId
    private LessonLecturerId id;

    @ManyToOne
    @MapsId("idUser")
    private User user;

    @ManyToOne
    @MapsId("idLesson")
    private Lesson lesson;

    // Composite Key class
    @Embeddable
    public static class LessonLecturerId implements Serializable {
        private Long idUser;
        private Long idLesson;

        public LessonLecturerId() {
        }

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

        public void setIdUser(Long idUser) {
            this.idUser = idUser;
        }

        public void setIdLesson(Long idLesson) {
            this.idLesson = idLesson;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (!(obj instanceof LessonLecturerId))
                return false;
            if (obj == null)
                return false;
            LessonLecturerId pk = (LessonLecturerId) obj;
            return pk.idUser == this.idUser && pk.idLesson == this.idLesson;
        }
    }

    public LessonLecturer() {
    }

    public LessonLecturer(User user, Lesson lesson) {
        this.id = new LessonLecturerId(user.getId(), lesson.getId());
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

    public void setId(LessonLecturerId id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public TAF getTAF() {
        return this.getLesson().getUe().getTaf();
    }

}
