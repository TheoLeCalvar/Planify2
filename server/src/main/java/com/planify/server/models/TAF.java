package com.planify.server.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

    private String description;

    private String beginDate;

    private String endDate;

    @OneToMany(mappedBy = "taf", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UE> UEs = new ArrayList<>();

    @OneToMany(mappedBy = "taf", fetch = FetchType.EAGER)
    private List<Calendar> calendars = new ArrayList<>();

    @OneToMany(mappedBy = "taf", fetch = FetchType.EAGER)
    private List<TAFManager> TAFmanagers = new ArrayList<>();

    public TAF() {
    }

    public TAF(String name, String description, String beginDate, String endDate) {
        this.name = name;
        this.description = description;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public String toString() {
        return "TAF " + Long.toString(this.id) + "\n Name: " + name;
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String string) {
        this.description = string;
    }

    public String getBeginDate() {
        return this.beginDate;
    }

    public void setBeginDate(String string) {
        this.beginDate = string;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setEndDate(String string) {
        this.endDate = string;
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