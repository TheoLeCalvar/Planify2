package com.planify.server.controller.returnsClass;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.planify.server.models.Planning;

import java.time.LocalDateTime;

public class PlanningReturn {

    private Long id;

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private Planning.Status status;

    public PlanningReturn() {
    }

    public PlanningReturn(Long id, LocalDateTime timestamp, String name) {
        this.id = id;
        this.timestamp = timestamp;
        this.name = name;
    }

    public PlanningReturn(Long id, String name, LocalDateTime timestamp, Planning.Status generated) {
        this.id = id;
        this.name = name;
        this.timestamp = timestamp;
        this.status = generated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Planning.Status getStatus() {
        return status;
    }

    public void setStatus(Planning.Status status) {
        this.status = status;
    }
}
