package com.data.data.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
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
    @Data
    private class UEManagerId implements Serializable{

        private Long idUser;
        private Long idUE;

        public UEManagerId(Long idUser, Long idUE) {
            this.idUser = idUser;
            this.idUE = idUE;
        }
    
    }
    
}
