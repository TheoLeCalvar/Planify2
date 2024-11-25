package com.planify.server.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class TAF {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "taf")
    private List<UE> UEs = new ArrayList<>();

    @OneToMany(mappedBy = "taf")
    private List<Calendar> calendars = new ArrayList<>();

    @OneToMany(mappedBy = "taf")
    private List<TAFManager> TAFmanagers = new ArrayList<>();

    public TAF() {
    }

    public TAF(String name) {
        this.name = name;
    }

    // Getters et setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UE> getUes() {
        return UEs;
    }

    public void setUes(List<UE> list) {
        this.UEs = list;
    }

    public List<Calendar> getCalendars() {
        return calendars;
    }

    public void setCalendars(List<Calendar> calendars) {
        this.calendars = calendars;
    }

    public List<TAFManager> getTafManagers() {
        return TAFmanagers;
    }

    public void setTafManagers(List<TAFManager> tafManagers) {
        this.TAFmanagers = tafManagers;
    }
}