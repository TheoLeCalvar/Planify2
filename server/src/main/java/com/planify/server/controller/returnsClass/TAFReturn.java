package com.planify.server.controller.returnsClass;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.planify.server.models.Planning;

public class TAFReturn {

    private Long id;

    private String name;

    private String description;

    @JsonManagedReference
    private List<UEShort> UE;

    @JsonManagedReference
    private List<Long> CalendarsId;

    @JsonManagedReference
    private List<UserBrief> managers;

    private String startDate;

    private String endDate;

    @JsonManagedReference
    private List<PlanningReturn> resultPlanning;

    @JsonManagedReference
    private List<Long> tafSynchronised;

    public TAFReturn(Long id, String name, String description, List<UEShort> UEs, List<Long> CalendarsId,
                     List<UserBrief> managers, String beginDate, String endDate, List<PlanningReturn> plannings, List<Long> tafSynchronised) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.UE = UEs;
        this.CalendarsId = CalendarsId;
        this.managers = managers;
        this.startDate = beginDate;
        this.endDate = endDate;
        this.resultPlanning = plannings;
        this.tafSynchronised = tafSynchronised;
    }

    public TAFReturn() {
        this.id = (long) -1;
        this.name = "";
        this.description = "";
        this.UE = new ArrayList<>();
        this.CalendarsId = new ArrayList<>();
        this.managers = new ArrayList<>();
        this.startDate = "";
        this.endDate = "";
        this.resultPlanning = new ArrayList<>();
        this.tafSynchronised = new ArrayList<>();
    }

    public String toString() {
        String string = "TAFReturn: + \n id = " + "" + id + "\n name = " + name + "\n UE = ";
        return string;
    }

    public List<UEShort> getUE() {
        return UE;
    }

    public void setUE(List<UEShort> UE) {
        this.UE = UE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getCalendarsId() {
        return CalendarsId;
    }

    public void setCalendarsId(List<Long> calendarsId) {
        CalendarsId = calendarsId;
    }

    public List<UserBrief> getManagers() {
        return managers;
    }

    public void setManagers(List<UserBrief> managers) {
        this.managers = managers;
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

    public List<PlanningReturn> getResultPlanning() {
        return resultPlanning;
    }

    public void setResultPlanning(List<PlanningReturn> resultPlanning) {
        this.resultPlanning = resultPlanning;
    }

    public List<Long> getTafSynchronised() {
        return tafSynchronised;
    }

    public void setTafSynchronised(List<Long> tafSynchronised) {
        this.tafSynchronised = tafSynchronised;
    }
}
