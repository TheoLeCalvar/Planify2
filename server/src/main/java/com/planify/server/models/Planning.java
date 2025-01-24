package com.planify.server.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Planning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "idCalendar")
    private Calendar calendar;

    @ElementCollection
    private List<ScheduledLesson> scheduledLessons;

    public Planning() {
    }

    public Planning(Calendar calendar) {
        this.calendar = calendar;
        this.scheduledLessons = new ArrayList<ScheduledLesson>();
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public List<ScheduledLesson> getScheduledLessons() {
        return scheduledLessons;
    }

    public void setScheduledLessons(List<ScheduledLesson> scheduledLessons) {
        this.scheduledLessons = scheduledLessons;
    }

    @Override
    public String toString() {
        return "Planning " + id +
                " { timestamp=" + timestamp +
                ", calendar=" + calendar.getId() +
                ", scheduledLesson= " + scheduledLessons +
                '}';
    }
}