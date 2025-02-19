package com.planify.server.controller.returnsClass;

import java.time.LocalDateTime;

public class PlanningReturn {

    private Long id;

    private String name;

    private String timestamp;

    private boolean generated;

    public PlanningReturn() {
    }

    public PlanningReturn(Long id, String timestamp, String name) {
        this.id = id;
        this.timestamp = timestamp;
        this.name = name;
    }

    public PlanningReturn(Long id, String name, boolean generated) {
        this.id = id;
        this.name = name;
        this.generated = generated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }
}
