package com.planify.server.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class GlobalUnavailability {

    private boolean strict;

    @Id
    @OneToOne
    @JoinColumn(name = "idSlot")
    private Slot slot;
    
}

