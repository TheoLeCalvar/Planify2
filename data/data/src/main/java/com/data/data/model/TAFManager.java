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
public class TAFManager {

    @EmbeddedId
    private TAFManagerId id = new TAFManagerId(this.user.getId(), this.taf.getId());

    @ManyToOne
    @JoinColumn(name = "id")  
    private User user;

    @ManyToOne
    @JoinColumn(name = "id")  
    private TAF taf;

    @Embeddable
    @Data
    private class TAFManagerId implements Serializable{

        private Long idUser;
        private Long idTAF;

        public TAFManagerId(Long idUser, Long idTAF) {
            this.idUser = idUser;
            this.idTAF = idTAF;
        }
    

    }
}
