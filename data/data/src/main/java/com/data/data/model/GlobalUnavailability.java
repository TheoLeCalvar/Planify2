package com.data.data.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class GlobalUnavailability {

    private boolean strict;

    @Id
    @ManyToOne
    @JoinColumn(name = "idSlot")
    private Slot slot;
    
}

