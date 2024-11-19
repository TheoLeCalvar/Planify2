package com.planify.server.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Block {

    private String title;

    @Id
    @OneToOne
    @JoinColumn(name = "idFirstLesson")
    private Lesson firstLesson;

    public Block() {}

    public Block(Lesson firstLesson) {
        this.firstLesson = firstLesson;
    }

    public Lesson getFirstLesson() {
        return this.firstLesson;
    }

    public void setFirstLesson(Lesson firstLesson) {
        this.firstLesson = firstLesson;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
