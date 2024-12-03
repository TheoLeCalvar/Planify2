package com.planify.server.models;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class UserUnavailability {

    @EmbeddedId
    private UserUnavailabilityId id = new UserUnavailabilityId(this.slot.getId(), this.user.getId());

    private boolean strict;

    @ManyToOne
    @MapsId("idUser")
    @JoinColumn(name = "idUser")
    private User user;

    @ManyToOne
    @MapsId("idSlot")
    @JoinColumn(name = "idSlot")
    private Slot slot;

    @Embeddable
    public class UserUnavailabilityId implements Serializable {

        private Long idSlot;
        private Long idUser;

        public UserUnavailabilityId(Long idSlot, Long idUser) {
            this.idSlot = idSlot;
            this.idUser = idUser;
        }

        public Long getIdSlot() {
            return this.idSlot;
        }

        public Long getIdUser() {
            return this.idUser;
        }

    }

    public UserUnavailability() {
    }

    public UserUnavailability(Slot s, User u, boolean strict) {
        this.slot = s;
        this.user = u;
        this.strict = strict;
    }

    public String toString() {
        return "UserUnavailability \n User: " + this.user.getName() + "\n Slot: " + "" + this.slot.getId() + "\n Strict"
                + "" + strict;
    }

    public UserUnavailabilityId getId() {
        return this.id;
    }

    public boolean getStrict() {
        return this.strict;
    }

    public void setStrict(boolean s) {
        this.strict = s;
    }

    public Slot getSlot() {
        return this.slot;
    }

    public User getUser() {
        return this.user;
    }

}
