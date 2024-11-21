package com.planify.server.models;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idTAF")
    private TAF taf;

    @OneToMany(mappedBy = "calendar")
    private List<Slot> slots;

    public Calendar() {}

    public Calendar(TAF taf) {
        this.taf = taf;
        this.slots = new ArrayList<Slot>();
    }

    public Long getId() {
        return id;
    }

    public TAF getTaf() {
        return taf;
    }

    public void setTaf(TAF taf) {
        this.taf = taf;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }
    
}