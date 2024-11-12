package com.data.data.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class UserUnavailability {
        
    @EmbeddedId
    private UserUnavailabilityId id;

    private boolean strict;

    @ManyToOne
    @JoinColumn(name = "id") 
    private User user;

    @Embeddable
    private class UserUnavailabilityId implements Serializable{

        private Long idSlot;
        private Long idUser;

        public UserUnavailabilityId(Long idSlot, Long idUser) {
            this.idSlot = idSlot;
            this.idUser = idUser;
        }

        // Getters and Setters
        public Long getIdSlot() {
            return idSlot;
        }

        public void setIdSlot(Long idSlot) {
            this.idSlot = idSlot;
        }

        public Long getIdUser() {
            return idUser;
        }

        public void setIdUser(Long idUser) {
            this.idUser = idUser;
        }

    }

    // Getters and Setters
    public Long getIdSlot() {
        return this.id.getIdSlot();
    }

    public void setIdSlot(Long idSlot) {
        this.id.setIdSlot(idSlot);
    }

    public Long getIdUser() {
        return this.id.getIdUser();
    }

    public void setIdUser(Long idUser) {
        this.id.setIdUser(idUser);;
    }

}
