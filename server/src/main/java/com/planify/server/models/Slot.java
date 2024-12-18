package com.planify.server.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Slot implements Comparable<Slot> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int number;

    @ManyToOne
    @JoinColumn(name = "idDay")
    private Day day;

    @ManyToOne
    @JoinColumn(name = "idCalendar")
    private Calendar calendar;

    @OneToMany(mappedBy = "slot", fetch = FetchType.EAGER)
    private List<UserUnavailability> userUnavailabilities = new ArrayList<>();

    public Slot() {
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

	@Override
	public int compareTo(Slot o) {
		int thisDay = this.getDayInt();
		int oDay = o.getDayInt();
		if (thisDay != oDay) return thisDay - oDay;
		return this.getNumber() - o.getNumber();
	}
	
	public int getDayInt() {
		return this.getDay().getWeek().getYear() * 53 * 7 + this.getDay().getWeek().getNumber() * 7 + this.getDay().getNumber();
	}

}
