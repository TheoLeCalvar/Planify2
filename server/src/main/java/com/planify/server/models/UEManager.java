package com.planify.server.models;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class UEManager {

    @EmbeddedId
    private UEManagerId id;

    @ManyToOne
    @MapsId("idUser")
    @JoinColumn(name = "idUser")
    private User user;

    @ManyToOne
    @MapsId("idUE")
    @JoinColumn(name = "idUE")
    private UE ue;

    @Embeddable
    private class UEManagerId implements Serializable {

        private Long idUser;
        private Long idUE;

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

    public UEManagerId getId() {
        return id;
    }

    public User getUser() {
        return user;
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
}
