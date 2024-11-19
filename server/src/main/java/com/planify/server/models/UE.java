package com.planify.server.models;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class UE {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @ManyToOne
    @JoinColumn(name = "taf_id")
    private TAF taf;

    @OneToMany(mappedBy = "ue")
    private List<Lesson> lessons;

    @OneToMany(mappedBy = "ue")
    private List<UEManager> UEmanagers;

    public UE() {
    }

    public UE(String description, TAF taf) {
        this.description = description;
        this.taf = taf;
    }

    public Long getId() {
        return this.id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TAF getTaf() {
        return this.taf;
    }

    public void setTaf(TAF taf) {
        this.taf = taf;
    }

    public List<Lesson> getLessons() {
        return this.lessons;
    }

    public List<UEManager> getUeManagers() {
        return this.UEmanagers;
    }

}
