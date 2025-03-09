package com.planify.server.controller.returnsClass;

public class LessonSynchronised {

    private Long id;
    private String taf;
    private String ue;
    private String name;

    public LessonSynchronised() {
    }

    public LessonSynchronised(Long id, String taf, String ue, String name) {
        this.id = id;
        this.taf = taf;
        this.ue = ue;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaf() {
        return taf;
    }

    public void setTaf(String taf) {
        this.taf = taf;
    }

    public String getUe() {
        return ue;
    }

    public void setUe(String ue) {
        this.ue = ue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
