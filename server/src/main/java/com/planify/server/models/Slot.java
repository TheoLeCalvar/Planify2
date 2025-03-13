package com.planify.server.models;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.sqm.TemporalUnit;
import org.springframework.cglib.core.Local;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Slot implements Comparable<Slot> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int number;

    @Column(name = "start_date")
    private LocalDateTime start;

    @Column(name = "end_date")
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "idDay")
    private Day day;

    @ManyToOne
    @JoinColumn(name = "idCalendar")
    private Calendar calendar;

    @OneToMany(mappedBy = "slot", fetch = FetchType.LAZY)
    private List<UserUnavailability> userUnavailabilities = new ArrayList<>();
    

    public Slot() {
    }

    public Slot(int number, Day day, Calendar calendar, LocalDateTime start, LocalDateTime end) {
        this.number = number;
        this.day = day;
        this.calendar = calendar;
        this.start = start;
        this.end = end;
    }

    public Slot(int number, Day day, Calendar calendar) {
        this.number = number;
        this.day = day;
        this.calendar = calendar;
    }

    public String toString() {
        return "Slot" + Long.toString(this.id) + "\n Number: " + "" + number + "\n Day: " + "" + day.getId();
    }

    public Long getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public LocalDateTime getStart() {
        return this.start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return this.end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public List<UserUnavailability> getUserUnavailabilities() {
        return this.userUnavailabilities;
    }

    public void setUserUnavailabilities(List<UserUnavailability> list) {
        this.userUnavailabilities = list;
    }

	public long getDurationMinutes() {
    	return ChronoUnit.MINUTES.between(this.getStart(), this.getEnd());
    }
    
    public LocalDateTime getMiddle() {
    	return this.getStart().plusMinutes(this.getDurationMinutes() / 2);
    }
    
	@Override
	public int compareTo(Slot o) {
		if (this.getDurationMinutes() != o.getDurationMinutes()) throw new ClassCastException("The two slots don't have the same duration");
		return this.getStart().compareTo(o.getStart());
	}
	
	public int getDayInt() {
		return this.getDay().getWeek().getYear() * 53 * 7 + this.getDay().getWeek().getNumber() * 7 + this.getDay().getNumber();
	}

}
