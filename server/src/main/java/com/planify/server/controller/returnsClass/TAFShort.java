package com.planify.server.controller.returnsClass;

import com.fasterxml.jackson.annotation.JsonManagedReference;

public class TAFShort {

    @JsonManagedReference
    private Long id;

    @JsonManagedReference
    private String name;

    @JsonManagedReference
    private String description;

    public TAFShort(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}