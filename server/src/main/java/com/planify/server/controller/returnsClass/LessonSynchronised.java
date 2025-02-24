package com.planify.server.controller.returnsClass;

public class LessonSynchronised {

    private Long id;
    private String taf;
    private String ue;

    public LessonSynchronised() {
    }

    public LessonSynchronised(Long id, String taf, String ue) {
        this.id = id;
        this.taf = taf;
        this.ue = ue;
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
}
