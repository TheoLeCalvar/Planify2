package com.planify.server.controller.returnsClass;

import java.time.LocalDateTime;

public class PlanningReturn {

    private Long id;

    private String timestamp;

    public PlanningReturn() {
    }

    public PlanningReturn(Long id, String timestamp) {
        this.id = id;
        this.timestamp = timestamp;
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


}
