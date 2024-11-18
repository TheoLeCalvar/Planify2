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
    private class TAFManagerId implements Serializable {

        private Long idUser;
        private Long idTAF;

        public TAFManagerId(Long idUser, Long idTAF) {
            this.idUser = idUser;
            this.idTAF = idTAF;
        }

    }
}
