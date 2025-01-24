package com.planify.server.controller.returnsClass;

import com.fasterxml.jackson.annotation.JsonManagedReference;

public class TAFShort {

    private Long id;

    private String name;

    private String description;

    private String startDate;

    private String endDate;

    public TAFShort() {

    }

    public TAFShort(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setEndDate(String date) {
        this.endDate = date;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String date) {
        this.startDate = date;
    }
}