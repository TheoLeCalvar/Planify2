package com.planify.server.controller.returnsClass;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

public class SlotShort {

    private String id;

    private String inWeekId;

    private String start;

    private String end;

    private AvailabilityEnum status;

    public SlotShort(String id, String inWeekId, String start, String end, AvailabilityEnum status) {
        this.id = id;
        this.inWeekId = inWeekId;
        this.start = start;
        this.end = end;
        this.status = status;
    }

    public String getId() {
        return this.id;
    }

    public String getInWeekId() {
        return this.inWeekId;
    }

    public String getStart() {
        return this.start;
    }

    public String getEnd() {
        return this.end;
    }

    public AvailabilityEnum getStatus() {
        return this.status;
    }

    public void setStatus(AvailabilityEnum status) {
        this.status = status;
    }

}

