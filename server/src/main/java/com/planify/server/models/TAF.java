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

    @OneToMany(mappedBy = "taf")
    private List<UE> UEs;

    @OneToMany(mappedBy = "taf")
    private List<Calendar> calendars;

    @OneToMany(mappedBy = "taf")
    private List<TAFManager> TAFmanagers = new ArrayList<>();

    public TAF() {
    }

    public TAF(List<UE> ues, List<Calendar> calendars, List<TAFManager> tafManagers) {
        this.UEs = ues;
        this.calendars = calendars;
        this.TAFmanagers = tafManagers;
    }

    // Getters et setters

    public Long getId() {
        return id;
    }

    public List<UE> getUes() {
        return UEs;
    }

    public List<Calendar> getCalendars() {
        return calendars;
    }

    public List<TAFManager> getTafManagers() {
        return TAFmanagers;
    }
}