package com.planify.server.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Week {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int number; // The number of the week for the year

    @Column(name = "\"year\"")
    private Integer year;

    @OneToMany(mappedBy = "week")
    private List<Day> days;

}
