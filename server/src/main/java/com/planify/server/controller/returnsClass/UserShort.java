package com.planify.server.controller.returnsClass;

import com.fasterxml.jackson.annotation.JsonManagedReference;

public class UserShort {

    @JsonManagedReference
    private Long id;

    @JsonManagedReference
    private String name;

    @JsonManagedReference
    private boolean alreadySelected;

    public UserShort(Long id, String name, boolean alreadySelected) {
        this.id = id;
        this.name = name;
        this.alreadySelected = alreadySelected;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean getAlreadySelected() {
        return this.alreadySelected;
    }
}
