package com.planify.server.controller.returnsClass;

import com.planify.server.models.Planning;

public class ConfigShort {

    private Long id;

    private String name;

    private Long calendar;

    public ConfigShort() {
    }

    public ConfigShort(Long id, String name, Long calendar) {
        this.id = id;
        this.name = name;
        this.calendar = calendar;
    }

    public ConfigShort(Planning planning) {
        this(planning.getId(), planning.getName(), planning.getCalendar().getId());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCalendar() {
        return calendar;
    }

    public void setCalendar(Long calendar) {
        this.calendar = calendar;
    }
}
