package com.planify.server.models;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class UserUnavailability {
        
    @EmbeddedId
    private UserUnavailabilityId id = new UserUnavailabilityId(this.slot.getId(),this.user.getId());

    private boolean strict;

    @ManyToOne
    @JoinColumn(name = "idUser") 
    private User user;

    @ManyToOne
    @JoinColumn(name = "idSlot") 
    private Slot slot;

    @Embeddable
    public class UserUnavailabilityId implements Serializable{

        private Long idSlot;
        private Long idUser;

        public UserUnavailabilityId(Long idSlot, Long idUser) {
            this.idSlot = idSlot;
            this.idUser = idUser;
        }

    }

}
