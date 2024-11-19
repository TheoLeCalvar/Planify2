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
import jakarta.persistence.Table;

@Entity
@Table(name = "app_day")
public class Day {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int number;

    @ManyToOne
    @JoinColumn(name = "idWeek")
    private Week week;

    @OneToMany
    @JoinColumn(name = "idDay")
    private List<Slot> slots;

    public Day() {}

    public Day(int number, Week week) {
        this.number = number;
        this.week = week; // Add this day in Week days list
        this.slots = new ArrayList<Slot>();
    }

    public Long getId() {
        return this.id;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Week getWeek() {
        return this.week;
    }

    public void setWeek(Week week) {
        this.week = week;
    }

    public List<Slot> getSlots() {
        return this.slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }
}
