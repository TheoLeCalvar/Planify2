package com.planify.server.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int number;

    @ManyToOne
    @JoinColumn(name = "idDay")
    private Day day;

    @ManyToOne
    @JoinColumn(name = "idCalendar")
    private Calendar calendar;

    @OneToMany(mappedBy = "slot")
    private List<UserUnavailability> userUnavailabilities = new ArrayList<>();

    public Slot() {
    }

    public Slot(int number, Day day, Calendar calendar) {
        this.number = number;
        this.day = day;
        this.calendar = calendar;
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

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public List<UserUnavailability> getUserUnavailabilities() {
        return this.userUnavailabilities;
    }

    public void setUserUnavailabilities(List<UserUnavailability> list) {
        this.userUnavailabilities = list;
    }

}
