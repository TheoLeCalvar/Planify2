package com.planify.server.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToOne
    @JoinColumn(name = "idFirstLesson")
    private Lesson firstLesson;

    public Block() {
    }

    public Block(Lesson firstLesson, String title) {
        this.firstLesson = firstLesson;
        this.title = title;
    }

    public String toString() {
        return "Block \n Title: " + this.title + "\n First Lesson " + "" + this.firstLesson.getName();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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
