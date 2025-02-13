package com.planify.server.models.constraints;

import com.planify.server.models.Planning;
import com.planify.server.models.UE;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Duration;
import java.time.Period;

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

    // Max and min of lessons in this UE in a week
    private boolean lessonCountInWeek;
    private int maxLessonInWeek;
    private int minLessonInWeek;

    // Max time without this UE
    private boolean maxTimeWithoutLesson;
    private boolean maxTimeWLUnitInWeeks;
    private int maxTimeWLduration;

	// Spreading of the UE
    private boolean spreading;
    private int maxSpreading;
    private int minSpreading;

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

    public boolean isLessonCountInWeek() {
        return lessonCountInWeek;
    }

    public void setLessonCountInWeek(boolean lessonCount) {
        this.lessonCountInWeek = lessonCount;
    }

    public int getMaxLessonInWeek() {
        return maxLessonInWeek;
    }

    public void setMaxLessonInWeek(int maxLesson) {
        this.maxLessonInWeek = maxLesson;
    }

    public int getMinLessonInWeek() {
        return minLessonInWeek;
    }

    public void setMinLessonInWeek(int minLesson) {
        this.minLessonInWeek = minLesson;
    }

    public boolean isMaxTimeWithoutLesson() {
        return maxTimeWithoutLesson;
    }

    public void setMaxTimeWithoutLesson(boolean maxTimeWithoutLesson) {
        this.maxTimeWithoutLesson = maxTimeWithoutLesson;
    }

    public boolean isMaxTimeWLUnitInWeeks() {
		return maxTimeWLUnitInWeeks;
	}

	public void setMaxTimeWLUnitInWeeks(boolean maxTimeWLunitInWeeks) {
		this.maxTimeWLUnitInWeeks = maxTimeWLunitInWeeks;
	}

	public int getMaxTimeWLduration() {
		return maxTimeWLduration;
	}

	public void setMaxTimeWLduration(int maxTimeWLduration) {
		this.maxTimeWLduration = maxTimeWLduration;
	}

    public boolean isSpreading() {
        return spreading;
    }

    public void setSpreading(boolean spreading) {
        this.spreading = spreading;
    }

    public int getMaxSpreading() {
        return maxSpreading;
    }

    public void setMaxSpreading(int maxSpreading) {
        this.maxSpreading = maxSpreading;
    }

    public int getMinSpreading() {
        return minSpreading;
    }

    public void setMinSpreading(int minSpreading) {
        this.minSpreading = minSpreading;
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
