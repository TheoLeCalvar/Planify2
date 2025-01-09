package com.planify.server.controller.returnsClass;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.planify.server.models.UE;
import com.planify.server.models.UEManager;

public class UEShort {

    @JsonManagedReference
    private Long id;

    @JsonManagedReference
    private String name;

    @JsonManagedReference
    private String description;

    @JsonManagedReference
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

}
