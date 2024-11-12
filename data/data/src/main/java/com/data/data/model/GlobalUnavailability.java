package com.data.data.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class GlobalUnavailability {

    private boolean strict;

    @Id
    @OneToOne
    @JoinColumn(name = "idSlot")
    private Slot slot;
    
}

