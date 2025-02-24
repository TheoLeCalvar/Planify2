package com.planify.server.controller.returnsClass;

import java.util.List;

public class TAFSynchronised {

    private Long id;
    private String name;
    private List<PlanningReturn> plannings;

    public TAFSynchronised() {
    }

    public TAFSynchronised(Long id, String name, List<PlanningReturn> plannings) {
        this.id = id;
        this.name = name;
        this.plannings = plannings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PlanningReturn> getPlannings() {
        return plannings;
    }

    public void setPlannings(List<PlanningReturn> plannings) {
        this.plannings = plannings;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
