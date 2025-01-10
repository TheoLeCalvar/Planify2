package com.planify.server.controller.returnsClass;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserShort {

    private Long id;

    private String name;

    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("lastname")
    private String lastName;

    private String email;

    private boolean alreadySelected;

    public UserShort() {

    }

    public UserShort(Long id, String name, boolean alreadySelected) {
        this.id = id;
        this.name = name;
        this.email = "";
        this.firstName = "";
        this.lastName = "";
        this.alreadySelected = alreadySelected;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String name) {
        this.lastName = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String mail) {
        this.email = mail;
    }

    public boolean getAlreadySelected() {
        return this.alreadySelected;
    }

    public void setAlreadySelected(boolean as) {
        this.alreadySelected = as;
    }
}
