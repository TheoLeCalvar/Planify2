package com.data.data.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class UEManager {

    @EmbeddedId
    private UEManagerId id;

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
