package com.planify.server.controller.returnsClass;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.planify.server.models.LessonLecturer.LessonLecturerId;

public class LessonShortLecturer {

    private Long id;

    private String title;

    private String description;

    private List<UserBrief> lecturers;

    private List<LessonSynchronised> synchronise;

    public LessonShortLecturer() {
    }

    public LessonShortLecturer(Long id, String title, String description, List<UserBrief> lecturers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.lecturers = lecturers;
    }

    public LessonShortLecturer(Long id, String title, String description, List<UserBrief> lecturers, List<LessonSynchronised> synchronise) {
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

    public List<UserBrief> getLecturers() {
        return this.lecturers;
    }

    public void setLecturers(List<UserBrief> lecturers) {
        this.lecturers = lecturers;
    }

    public List<LessonSynchronised> getSynchronise() {
        return synchronise;
    }

    public void setSynchronise(List<LessonSynchronised> synchronise) {
        this.synchronise = synchronise;
    }
}

