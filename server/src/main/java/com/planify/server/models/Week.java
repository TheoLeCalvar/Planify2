package com.planify.server.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

    @OneToMany(mappedBy = "week", fetch = FetchType.LAZY)
    private List<Day> days;

    public Week() {
    }

    public Week(int number, Integer year) {
        this.number = number;
        this.year = year;
        this.days = new ArrayList<Day>();
    }

    public String toString() {
        return "Week " + Long.toString(this.id) + "\n Number: " + "" + number + "\n Year: " + "" + year;
    }

    public Long getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }
}
