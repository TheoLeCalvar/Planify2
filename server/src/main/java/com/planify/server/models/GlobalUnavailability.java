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

    public GlobalUnavailability() {}

    public GlobalUnavailability(boolean strict, Slot slot) {
        this.strict = strict;
        this.slot = slot;
    }

    public boolean getStrict() {
        return this.strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public Slot getSlot() {
        return this.slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }
    
}

