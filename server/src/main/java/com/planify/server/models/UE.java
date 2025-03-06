package com.planify.server.models;

import java.util.ArrayList;
import java.util.List;

import com.planify.server.models.constraints.ConstraintsOfUE;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "taf_id")
    private TAF taf;

    @OneToMany(mappedBy = "ue", fetch = FetchType.LAZY)
    private List<Lesson> lessons = new ArrayList<>();

    @OneToMany(mappedBy = "ue", fetch = FetchType.LAZY)
    private List<UEManager> UEManagers = new ArrayList<>();

    @OneToMany(mappedBy = "ue", fetch = FetchType.LAZY)
    private List<ConstraintsOfUE> constraintsOfUE = new ArrayList<ConstraintsOfUE>();
    
    public UE() {
    }

    public UE(String name, String description, TAF taf) {
        this.name = name;
        this.description = description;
        this.taf = taf;
    }

    public String toString() {
        return "UE " + Long.toString(this.id) + "\n Description: " + this.description + "\n TAF: " + this.taf.getName();
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

	public List<UEManager> getUEManagers() {
		return UEManagers;
	}

	public void setUEManagers(List<UEManager> uEmanagers) {
		UEManagers = uEmanagers;
	}

	public List<ConstraintsOfUE> getConstraintsOfUE() {
		return constraintsOfUE;
	}

	public void setConstraintsOfUE(List<ConstraintsOfUE> constraintsOfUE) {
		this.constraintsOfUE = constraintsOfUE;
	}

}
