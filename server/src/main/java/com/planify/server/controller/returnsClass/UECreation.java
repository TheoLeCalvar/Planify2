package com.planify.server.controller.returnsClass;

import java.util.List;

public class UECreation {

    private Long tafId;

    private  String name;

    private String description;

    private List<Long> managers;

    public UECreation() {
    }

    public UECreation(Long tafId, String name, String description, List<Long> managers) {
        this.tafId = tafId;
        this.name = name;
        this.description = description;
        this.managers = managers;
    }

    public Long getTafId() {
        return tafId;
    }

    public void setTafId(Long tafId) {
        this.tafId = tafId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getManagers() {
        return managers;
    }

    public void setManagers(List<Long> managers) {
        this.managers = managers;
    }

    @Override
    public String toString() {
        return "UECreation{" +
                "tafId=" + tafId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", managers=" + managers +
                '}';
    }
}
