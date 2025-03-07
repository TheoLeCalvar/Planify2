package com.planify.server.models.constraints;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

import com.planify.server.controller.returnsClass.Config;
import com.planify.server.models.Planning;
import com.planify.server.service.ConstraintSynchroniseWithTAFService;

import jakarta.persistence.*;

@Entity
public class ConstraintSynchroniseWithTAF {
	
    @EmbeddedId
    private ConstraintsSynchroniseWithTAFId id;

    @ManyToOne
    @MapsId("idPlanning")
    private Planning planning;

    @ManyToOne
    @MapsId("idOtherPlanning")
    private Planning otherPlanning;

    private boolean enabled;

    @Embeddable
    public static class ConstraintsSynchroniseWithTAFId implements Serializable {

        private Long idPlanning;
        private Long idOtherPlanning;

        public ConstraintsSynchroniseWithTAFId() {
        }

        public ConstraintsSynchroniseWithTAFId( Long idPlanning, Long idOtherPlanning) {
            this.idOtherPlanning = idOtherPlanning;
            this.idPlanning = idPlanning;
        }

        public Long getIdOtherPlanning() {
            return idOtherPlanning;
        }

        public void setIdUE(Long idOtherPlanning) {
            this.idOtherPlanning = idOtherPlanning;
        }

        public Long getIdPlanning() {
            return idPlanning;
        }

        public void setIdPlanning(Long idPlanning) {
            this.idPlanning = idPlanning;
        }
    }
    
    public ConstraintSynchroniseWithTAF() {
    }
    
    public ConstraintSynchroniseWithTAF(Planning planning, Planning otherPlanning) {
    	this.id = new ConstraintsSynchroniseWithTAFId(planning.getId(), otherPlanning.getId());
    	this.planning = planning;
        this.otherPlanning = otherPlanning;
    }
    
    public ConstraintSynchroniseWithTAF(Planning planning, Planning otherPlanning, boolean enabled) {
        this.id = new ConstraintsSynchroniseWithTAFId(planning.getId(), otherPlanning.getId());
    	this.planning = planning;
        this.otherPlanning = otherPlanning;
        this.enabled = enabled;
    }

    public Planning getOtherPlanning() {
        return otherPlanning;
    }

    public void setOtherPlanning(Planning otherPlanning) {
        this.otherPlanning = otherPlanning;
    }

    public ConstraintsSynchroniseWithTAFId getId() {
        return id;
    }

    public void setId(ConstraintsSynchroniseWithTAFId id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Planning getPlanning() {
        return planning;
    }

    public void setPlanning(Planning planning) {
        this.planning = planning;
    }

    @Override
    public String toString() {
        return "ConstraintSynchroniseWithTAF{" +
                "id=" + id +
                ", enabled=" + enabled +
                '}';
    }
}
