package com.planify.server.controller.returnsClass;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.planify.server.models.TAFManager.TAFManagerId;

public class TAFReturn {

    @JsonManagedReference
    private Long id;

    @JsonManagedReference
    private String name;

    @JsonManagedReference
    private List<Long> UEsId;

    @JsonManagedReference
    private List<Long> CalendarsId;

    @JsonManagedReference
    private List<TAFManagerId> TAFManagersId;

    public TAFReturn(Long id, String name, List<Long> UEsId, List<Long> CalendarsId, List<TAFManagerId> TAFManagersId) {
        this.id = id;
        this.name = name;
        this.UEsId = UEsId;
        this.CalendarsId = CalendarsId;
        this.TAFManagersId = TAFManagersId;
    }

    public String toString() {
        String string = "TAFReturn: + \n id = " + "" + id + "\n name = " + name + "\n UE = ";
        return string;
    }

}
