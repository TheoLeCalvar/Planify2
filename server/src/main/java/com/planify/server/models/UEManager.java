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
    private UEManagerId id = new UEManagerId(this.user.getId(), this.ue.getId());

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

    }

}
