package com.planify.server.controller.returnsClass;

public class UserUnavailabilityShort {
    
    private Long id;

    private AvailabilityEnum status;

    public UserUnavailabilityShort(Long id, AvailabilityEnum status) {
        this.id = id;
        this.status = status;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AvailabilityEnum getStatus() {
        return this.status;
    }

    public void setStatus(AvailabilityEnum status) {
        this.status = status;
    }
}
