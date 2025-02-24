package com.planify.server.models;

public class Result {

    private Long id;

    private Long idLesson;

    public Result(Long id, Long idLesson) {
        this.id = id;
        this.idLesson = idLesson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdLesson() {
        return idLesson;
    }

    public void setIdLesson(Long idLesson) {
        this.idLesson = idLesson;
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", idLesson=" + idLesson +
                '}';
    }
}
