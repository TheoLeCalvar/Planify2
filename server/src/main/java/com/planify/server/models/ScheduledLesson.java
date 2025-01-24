package com.planify.server.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Embeddable
public class ScheduledLesson {

    private Long id;

    @JsonFormat(pattern = "dd/MM/yyyy hh:mm")
    private LocalDateTime start; // date of the start of the lesson

    @Column(name = "\"end\"")
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm")
    private LocalDateTime end; // date of the end of the lesson

    private String UE; // name of the UE

    private String title; // title of the lesson

    private String description; // description of the lesson

    private List<String> lecturers; // list of the full name of the lecturers

    public ScheduledLesson() {
    }

    public ScheduledLesson(Long id, LocalDateTime start, LocalDateTime end, String UE, String title, String description, List<String> lecturers) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.UE = UE;
        this.title = title;
        this.description = description;
        this.lecturers = lecturers;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getUE() {
        return UE;
    }

    public void setUE(String UE) {
        this.UE = UE;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getLecturers() {
        return lecturers;
    }

    public void setLecturers(List<String> lecturers) {
        this.lecturers = lecturers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduledLesson that = (ScheduledLesson) o;
        return Objects.equals(start, that.start) && Objects.equals(end, that.end) && Objects.equals(UE, that.UE) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(lecturers, that.lecturers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, UE, title, description);
    }

    @Override
    public String toString() {
        return "ScheduledLesson {" +
                "start=" + start +
                ", end=" + end +
                ", UE='" + UE + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", lecturers=" + lecturers +
                '}';
    }
}
