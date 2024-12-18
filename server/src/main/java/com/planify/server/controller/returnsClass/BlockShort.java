package com.planify.server.controller.returnsClass;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

public class BlockShort {

    @JsonManagedReference
    private Long id;

    @JsonManagedReference
    private String title;

    @JsonManagedReference
    private String description;

    @JsonManagedReference
    private List<LessonShort> lessons;

    @JsonManagedReference
    private List<Long> dependencies;

    public BlockShort(Long id, String title, String description, List<LessonShort> lessons, List<Long> dependencies) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.lessons = lessons;
        this.dependencies = dependencies;
    }

    public Long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public List<LessonShort> getLessons() {
        return this.lessons;
    }

    public List<Long> getDependencies() {
        return this.dependencies;
    }

}
