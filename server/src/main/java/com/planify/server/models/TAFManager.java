package com.planify.server.models;

import java.io.Serializable;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class TAFManager {

    @EmbeddedId
    private TAFManagerId id = new TAFManagerId(this.user.getId(), this.taf.getId());

    @ManyToOne
    @MapsId("idUser")
    @JoinColumn(name = "idUser")
    private User user;

    @ManyToOne
    @MapsId("idTAF")
    @JoinColumn(name = "idTAF")
    private TAF taf;

    @Embeddable
    public class TAFManagerId implements Serializable {

        private Long idUser;
        private Long idTAF;

        public TAFManagerId(Long idUser, Long idTAF) {
            this.idUser = idUser;
            this.idTAF = idTAF;
        }

        public Long getIdUser() {
            return idUser;
        }

        public Long getIdTAF() {
            return idTAF;
        }

    }

    public TAFManager() {
    }

    public TAFManager(User user, TAF taf) {
        this.user = user;
        this.taf = taf;
        this.id = new TAFManagerId(user.getId(), taf.getId());
    }

    public TAFManagerId getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (this.taf != null) {
            this.id = new TAFManagerId(user.getId(), this.taf.getId());
        }
    }

    public TAF getTaf() {
        return taf;
    }

    public void setTaf(TAF taf) {
        this.taf = taf;
        if (this.user != null) {
            this.id = new TAFManagerId(this.user.getId(), taf.getId());
        }
    }
}
