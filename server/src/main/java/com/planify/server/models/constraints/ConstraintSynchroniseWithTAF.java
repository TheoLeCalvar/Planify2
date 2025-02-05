package com.planify.server.models.constraints;

import com.planify.server.models.Planning;
import com.planify.server.models.TAF;
import jakarta.persistence.*;

@Entity
public class ConstraintSynchroniseWithTAF {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idPlanning")
    private Planning planning;

    @ManyToOne
    @JoinColumn(name = "taf_id")
    private TAF taf;

    private boolean enabled;

    private boolean regenerateBothPlanning;

    public ConstraintSynchroniseWithTAF() {
    }

    public ConstraintSynchroniseWithTAF(TAF taf, boolean enabled, boolean regenerateBothPlanning) {
        this.taf = taf;
        this.enabled = enabled;
        this.regenerateBothPlanning = regenerateBothPlanning;
    }

    public TAF getTaf() {
        return taf;
    }

    public void setTaf(TAF taf) {
        this.taf = taf;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isRegenerateBothPlanning() {
        return regenerateBothPlanning;
    }

    public void setRegenerateBothPlanning(boolean regenerateBothPlanning) {
        this.regenerateBothPlanning = regenerateBothPlanning;
    }

    @Override
    public String toString() {
        return "ConstraintSynchroniseWithTAF{" +
                "id=" + id +
                ", taf=" + taf +
                ", enabled=" + enabled +
                ", regenerateBothPlanning=" + regenerateBothPlanning +
                '}';
    }
}
