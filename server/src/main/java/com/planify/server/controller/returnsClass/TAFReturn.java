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
    private String description;

    @JsonManagedReference
    private List<UEShort> UEs;

    @JsonManagedReference
    private List<Long> CalendarsId;

    @JsonManagedReference
    private List<TAFManagerId> TAFManagersId;

    @JsonManagedReference
    private String beginDate;

    @JsonManagedReference
    private String endDate;

    public TAFReturn(Long id, String name, String description, List<UEShort> UEs, List<Long> CalendarsId,
            List<TAFManagerId> TAFManagersId, String beginDate, String endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.UEs = UEs;
        this.CalendarsId = CalendarsId;
        this.TAFManagersId = TAFManagersId;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public String toString() {
        String string = "TAFReturn: + \n id = " + "" + id + "\n name = " + name + "\n UE = ";
        return string;
    }

}
