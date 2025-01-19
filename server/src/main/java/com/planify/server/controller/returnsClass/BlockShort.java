package com.planify.server.controller.returnsClass;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

public class BlockShort {

    private Long id;

    private String title;

    private String description;

    private List<LessonShort> lessons;

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

    public List<LessonShort> getLessons() {
        return this.lessons;
    }

    public void setLessons(List<LessonShort> lessons) {
        this.lessons = lessons;
    }

    public List<Long> getDependencies() {
        return this.dependencies;
    }

    public void setDependencies(List<Long> dep) {
        this.dependencies = dep;
    }

}
