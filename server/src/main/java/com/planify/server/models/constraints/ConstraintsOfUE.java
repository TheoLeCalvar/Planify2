package com.planify.server.models.constraints;

import com.planify.server.models.Planning;
import com.planify.server.models.UE;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Duration;

@Entity
public class ConstraintsOfUE {

    @EmbeddedId
    private ConstraintsOfUEId id;

    @ManyToOne
    @MapsId("idUE")
    private UE ue;

    @ManyToOne
    @MapsId("idPlanning")
    private Planning planning;

    // Max and min of lessons in this UE in a day
    private boolean lessonCount;
    private int maxLesson;
    private int minLesson;

    // Max time without this UE
    private boolean maxTimeWithoutLesson;
    private Duration duration;

    @Embeddable
    public static class ConstraintsOfUEId implements Serializable {

        private Long idUE;
        private Long idPlanning;

        public ConstraintsOfUEId() {
        }

        public ConstraintsOfUEId(Long idUE, Long idPlanning) {
            this.idUE = idUE;
            this.idPlanning = idPlanning;
        }

        public Long getIdUE() {
            return idUE;
        }

        public void setIdUE(Long idUE) {
            this.idUE = idUE;
        }

        public Long getIdPlanning() {
            return idPlanning;
        }

        public void setIdPlanning(Long idPlanning) {
            this.idPlanning = idPlanning;
        }
    }

    public ConstraintsOfUE() {
    }

    public ConstraintsOfUE(UE ue, Planning planning) {
        this.id = new ConstraintsOfUEId(ue.getId(), planning.getId());
        this.ue = ue;
        this.planning = planning;
    }

    public ConstraintsOfUEId getId() {
        return id;
    }

    public void setId(ConstraintsOfUEId id) {
        this.id = id;
    }

    public UE getUe() {
        return ue;
    }

    public void setUe(UE ue) {
        this.ue = ue;
    }

    public Planning getPlanning() {
        return planning;
    }

    public void setPlanning(Planning planning) {
        this.planning = planning;
    }

    @Override
    public String toString() {
        return "ConstraintsOfUE{" +
                "id=" + id +
                ", ue=" + ue +
                ", planning=" + planning +
                '}';
    }
}
