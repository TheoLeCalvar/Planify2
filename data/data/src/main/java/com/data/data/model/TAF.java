package com.data.data.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class TAF {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "ue")
    private List<UE> UEs;

    @OneToMany(mappedBy = "calendar")
    private List<Calendar> calendars;

    @OneToMany(mappedBy = "tafmanager")
    private List<TAFManager> TAFmanagers;


}
