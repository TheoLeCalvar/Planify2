package com.planify.server.controller.returnsClass;

import com.fasterxml.jackson.annotation.JsonManagedReference;

public class UserShort {

    @JsonManagedReference
    private Long id;

    @JsonManagedReference
    private String fullname;

    @JsonManagedReference
    private String firstname;

    @JsonManagedReference
    private String lastname;

    @JsonManagedReference
    private String email;

    @JsonManagedReference
    private boolean alreadySelected;

    public UserShort(Long id, String name, boolean alreadySelected) {
        this.id = id;
        this.fullname = name;
        this.email = "";
        this.firstname = "";
        this.lastname = "";
        this.alreadySelected = alreadySelected;
    }

    public Long getId() {
        return this.id;
    }

    public String getFullName() {
        return this.fullname;
    }

    public String getFirstName() {
        return this.firstname;
    }

    public String getLastName() {
        return this.lastname;
    }

    public String getEmail() {
        return this.email;
    }

    public boolean getAlreadySelected() {
        return this.alreadySelected;
    }
}
