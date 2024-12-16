package com.planify.server.controller.returnsClass;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.planify.server.models.LessonLecturer.LessonLecturerId;

public class LessonShort {

    @JsonManagedReference
    private Long id;

    @JsonManagedReference
    private String title;

    @JsonManagedReference
    private String description;

    @JsonManagedReference
    private List<LessonLecturerId> lecturers;

    public LessonShort(Long id, String title, String description, List<LessonLecturerId> lecturers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.lecturers = lecturers;
    }

}
