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
    private List<String> managers;

    public UEShort(Long id, String name, List<String> managers) {
        this.id = id;
        this.name = name;
        this.managers = managers;
    }

    public UEShort(UE ue) {
        this.id = ue.getId();
        this.name = ue.getName();
        this.managers = ue.getUeManagers().stream().map(UEManager::getUserName).collect(Collectors.toList());
    }

}
