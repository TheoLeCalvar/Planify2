package com.planify.server.controller.returnsClass;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.planify.server.models.LessonLecturer.LessonLecturerId;

public class LessonShort {

    private Long id;

    private String title;

    private String description;

    private List<Long> lecturers;

    private List<LessonSynchronised> synchronise;

    public LessonShort() {
    }

    public LessonShort(Long id, String title, String description, List<Long> lecturers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.lecturers = lecturers;
    }

    public LessonShort(Long id, String title, String description, List<Long> lecturers, List<LessonSynchronised> synchronise) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.lecturers = lecturers;
        this.synchronise = synchronise;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getLecturers() {
        return this.lecturers;
    }

    public void setLecturers(List<Long> lects) {
        this.lecturers = lects;
    }

    public List<LessonSynchronised> getSynchronise() {
        return synchronise;
    }

    public void setSynchronise(List<LessonSynchronised> synchronise) {
        this.synchronise = synchronise;
    }
}
