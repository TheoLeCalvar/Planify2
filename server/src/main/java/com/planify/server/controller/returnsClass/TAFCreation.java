package com.planify.server.controller.returnsClass;

import java.time.LocalDate;
import java.util.List;

public class TAFCreation {

    private String name;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private List<Long> managers;

    public TAFCreation() {
    }

    public TAFCreation(String name, String description, LocalDate startDate, LocalDate endDate, List<Long> managers) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.managers = managers;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<Long> getManagers() {
        return managers;
    }

    public void setManagers(List<Long> managers) {
        this.managers = managers;
    }

    @Override
    public String toString() {
        return "TAFCreation{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", managers=" + managers +
                '}';
    }
}
