package com.planify.server.models;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class UEManager {

    @EmbeddedId
    private UEManagerId id = new UEManagerId(this.user.getId(), this.ue.getId());

    @ManyToOne
    @JoinColumn(name = "id")  
    private User user;

    @ManyToOne
    @JoinColumn(name = "id")  
    private UE ue;

    @Embeddable
    private class UEManagerId implements Serializable{

        private Long idUser;
        private Long idUE;

        public UEManagerId(Long idUser, Long idUE) {
            this.idUser = idUser;
            this.idUE = idUE;
        }
    
    }
    
}
