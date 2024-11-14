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
    @JoinColumn(name = "id")  
    private TAF taf;

    @OneToMany(mappedBy = "lesson")
    private List<Lesson> lessons;

    @OneToMany(mappedBy = "uemanager")
    private List<UEManager> UEmanagers;

	public Long getId() {
		return id;
	}

}
