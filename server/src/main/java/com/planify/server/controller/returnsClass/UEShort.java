package com.planify.server.controller.returnsClass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.planify.server.models.UE;
import com.planify.server.models.UEManager;

public class UEShort {

    private Long id;

    private String name;

    private String description;

    private List<String> managers;

    public UEShort(Long id, String name, String description, List<String> managers) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.managers = managers;
    }

    public UEShort(UE ue) {
        this.id = ue.getId();
        this.name = ue.getName();
        this.description = ue.getDescription();
        this.managers = ue.getUeManagers().stream().map(UEManager::getUserName).collect(Collectors.toList());
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getManagers() {
        return managers;
    }
}
