package com.planify.server.models;

import java.io.Serializable;
import java.util.Objects;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class UserUnavailability {
    
    @EmbeddedId
    private UserUnavailabilityId id;

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
    public static class UserUnavailabilityId implements Serializable {

        @Column(name = "idSlot")
        private Long idSlot;

        @Column(name = "idUser")
        private Long idUser;

        public UserUnavailabilityId() {}

        public UserUnavailabilityId(Long idSlot, Long idUser) {
            this.idSlot = idSlot;
            this.idUser = idUser;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserUnavailabilityId that = (UserUnavailabilityId) o;
            return idSlot.equals(that.idSlot) && idUser.equals(that.idUser);
        }

        @Override
        public int hashCode() {
            return Objects.hash(idSlot, idUser);
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
        this.id = new UserUnavailabilityId(slot.getId(), user.getId());
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
