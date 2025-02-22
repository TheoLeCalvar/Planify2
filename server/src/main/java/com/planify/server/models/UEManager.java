package com.planify.server.models;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class UEManager {

    @EmbeddedId
    private UEManagerId id;

    @MapsId("idUser")
    @ManyToOne
    private User user;

    @MapsId("idUE")
    @ManyToOne
    private UE ue;

    @Embeddable
    static public class UEManagerId implements Serializable {

        private Long idUser;
        private Long idUE;

        public UEManagerId() {
        }

        public UEManagerId(Long idUser, Long idUE) {
            this.idUser = idUser;
            this.idUE = idUE;
        }

        public Long getIdUser() {
            return idUser;
        }

        public Long getIdUE() {
            return idUE;
        }

    }

    public UEManager() {
    }

    public UEManager(User user, UE ue) {
        this.user = user;
        this.ue = ue;
        this.id = new UEManagerId(user.getId(), ue.getId());
    }

    public UEManager(UEManagerId id, User user, UE ue) {
        this.user = user;
        this.ue = ue;
        this.id = id;
    }

    public String toString() {
        return "UE Manager \n Manager: " + this.user.getName() + "\n UE: " + this.ue.getDescription();
    }

    public UEManagerId getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getUserName() {
        return user.getName() + " " + user.getLastName();
    }

    public void setUser(User user) {
        this.user = user;
        if (this.ue != null) {
            this.id = new UEManagerId(user.getId(), this.ue.getId());
        }
    }

    public UE getUe() {
        return ue;
    }

    public void setUe(UE ue) {
        this.ue = ue;
        if (this.user != null) {
            this.id = new UEManagerId(this.user.getId(), ue.getId());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UEManager ueManager = (UEManager) o;
        return Objects.equals(id, ueManager.id) && Objects.equals(user, ueManager.user) && Objects.equals(ue, ueManager.ue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, ue);
    }
}
