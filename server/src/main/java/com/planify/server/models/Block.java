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
    private Lesson idFirstLesson;
}
