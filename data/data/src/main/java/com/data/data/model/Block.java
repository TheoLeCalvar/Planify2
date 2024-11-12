package com.data.data.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Block {

    private String title;

    @Id
    @OneToOne
    @JoinColumn(name = "idFirstLesson")
    private Lesson idFirstLesson;
}
