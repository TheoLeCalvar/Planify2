package com.planify.server.controller.returnsClass;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.planify.server.models.Planning;
import com.planify.server.models.constraints.ConstraintSynchroniseWithTAF;
import com.planify.server.models.constraints.ConstraintsOfUE;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private Long id;

    private String name;

    private Long calendar;

    private Boolean globalUnavailability;

    private Integer weightGlobalUnavailability;

    private Boolean lecturersUnavailability;

    private Integer weightLecturersUnavailability;

    private Boolean synchronise;

    private List<CSyncrho> constraintsSynchronisation;

    private List<CUE> constraintsOfUEs;

    private int weightMaxTimeWithoutLesson;

    @JsonProperty("UEInterlacing")
    private Boolean UEInterlacing;

    private Boolean middayBreak;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startMiddayBreak;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private  LocalTime endMiddayBreak;

    private Boolean middayGrouping;

    private Integer weightMiddayGrouping;

    private Boolean lessonBalancing;

    private Integer weightLessonBalancing;

    private  Boolean lessonGrouping;

    private Integer weightLessonGrouping;

    public static class CSyncrho {
        private Long otherPlanning;
        private Boolean enabled;
        private Boolean generateOtherPlanning;

        public CSyncrho() {
        }

        public CSyncrho(ConstraintSynchroniseWithTAF c) {
            this.otherPlanning = c.getOtherPlanning().getId();
            this.enabled = c.isEnabled();
            this.generateOtherPlanning = c.isGenerateOtherPlanning();
        }

        public Long getOtherPlanning() {
            return otherPlanning;
        }

        public void setOtherPlanning(Long otherPlanning) {
            this.otherPlanning = otherPlanning;
        }

        public Boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Boolean isGenerateOtherPlanning() {
            return generateOtherPlanning;
        }

        public void setGenerateOtherPlanning(boolean generateOtherPlanning) {
            this.generateOtherPlanning = generateOtherPlanning;
        }
    }

    public static class CUE {
        private Long ue;
        private Boolean lessonCountInWeek;
        private Integer maxLessonInWeek;
        private Integer minLessonInWeek;
        private Boolean maxTimeWithoutLesson;
        private Boolean maxTimeWLUnitInWeeks;
        private Integer maxTimeWLDuration;
        private Boolean spreading;
        private Integer maxSpreading;
        private Integer minSpreading;
        private int[] lessonGroupingNbLessons;

        public CUE() {
        }

        public CUE(Long ue, boolean lessonCountInWeek, int maxLessonInWeek, int minLessonInWeek, boolean maxTimeWithoutLesson, boolean maxTimeWLUnitInWeeks, int maxTimeWLDuration, boolean spreading, int maxSpreading, int minSpreading, int[] lessonGroupingNbLessons) {
            this.ue = ue;
            this.lessonCountInWeek = lessonCountInWeek;
            this.maxLessonInWeek = maxLessonInWeek;
            this.minLessonInWeek = minLessonInWeek;
            this.maxTimeWithoutLesson = maxTimeWithoutLesson;
            this.maxTimeWLUnitInWeeks = maxTimeWLUnitInWeeks;
            this.maxTimeWLDuration = maxTimeWLDuration;
            this.spreading = spreading;
            this.maxSpreading = maxSpreading;
            this.minSpreading = minSpreading;
            this.lessonGroupingNbLessons = lessonGroupingNbLessons;
        }

        public CUE(ConstraintsOfUE c) {
            this.ue = c.getUe().getId();
            this.lessonCountInWeek = c.isLessonCountInWeek();
            this.maxLessonInWeek = c.getMaxLessonInWeek();
            this.minLessonInWeek = c.getMinLessonInWeek();
            this.maxTimeWithoutLesson = c.isMaxTimeWithoutLesson();
            this.maxTimeWLUnitInWeeks = c.isMaxTimeWLUnitInWeeks();
            this.maxTimeWLDuration = c.getMaxTimeWLDuration();
            this.spreading = c.isSpreading();
            this.maxSpreading = c.getMaxSpreading();
            this.minSpreading = c.getMinSpreading();
            this.lessonGroupingNbLessons = c.getLessonGroupingNbLessons();
        }

        public Long getUe() {
            return ue;
        }

        public void setUe(Long ue) {
            this.ue = ue;
        }

        public Boolean isLessonCountInWeek() {
            return lessonCountInWeek;
        }

        public void setLessonCountInWeek(boolean lessonCountInWeek) {
            this.lessonCountInWeek = lessonCountInWeek;
        }

        public Integer getMaxLessonInWeek() {
            return maxLessonInWeek;
        }

        public void setMaxLessonInWeek(int maxLessonInWeek) {
            this.maxLessonInWeek = maxLessonInWeek;
        }

        public Integer getMinLessonInWeek() {
            return minLessonInWeek;
        }

        public void setMinLessonInWeek(int minLessonInWeek) {
            this.minLessonInWeek = minLessonInWeek;
        }

        public Boolean isMaxTimeWithoutLesson() {
            return maxTimeWithoutLesson;
        }

        public void setMaxTimeWithoutLesson(boolean maxTimeWithoutLesson) {
            this.maxTimeWithoutLesson = maxTimeWithoutLesson;
        }

        public Boolean isMaxTimeWLUnitInWeeks() {
            return maxTimeWLUnitInWeeks;
        }

        public void setMaxTimeWLUnitInWeeks(boolean maxTimeWLUnitInWeeks) {
            this.maxTimeWLUnitInWeeks = maxTimeWLUnitInWeeks;
        }

        public Integer getMaxTimeWLDuration() {
            return maxTimeWLDuration;
        }

        public void setMaxTimeWLDuration(int maxTimeWLDuration) {
            this.maxTimeWLDuration = maxTimeWLDuration;
        }

        public Boolean isSpreading() {
            return spreading;
        }

        public void setSpreading(boolean spreading) {
            this.spreading = spreading;
        }

        public Integer getMaxSpreading() {
            return maxSpreading;
        }

        public void setMaxSpreading(int maxSpreading) {
            this.maxSpreading = maxSpreading;
        }

        public Integer getMinSpreading() {
            return minSpreading;
        }

        public void setMinSpreading(int minSpreading) {
            this.minSpreading = minSpreading;
        }

        public int[] getLessonGroupingNbLessons() {
            return lessonGroupingNbLessons;
        }

        public void setLessonGroupingNbLessons(int[] lessonGroupingNbLessons) {
            this.lessonGroupingNbLessons = lessonGroupingNbLessons;
        }
    }

    public Config() {
    }

    public Config(Long id, String name, Long calendar, boolean globalUnavailability, int weightGlobalUnavailability, boolean lecturersUnavailability, int weightLecturersUnavailability, boolean synchronise, List<CSyncrho> constraintsSynchronisation, List<CUE> constraintsOfUEs, boolean UEInterlacing, boolean middayBreak, LocalTime startMiddayBreak, LocalTime endMiddayBreak, boolean middayGrouping, int weightMiddayGrouping, boolean lessonBalancing, int weightLessonBalancing, boolean lessonGrouping, int weightLessonGrouping, int weightMaxTimeWithoutLesson) {
        this.id = id;
        this.name = name;
        this.calendar = calendar;
        this.globalUnavailability = globalUnavailability;
        this.weightGlobalUnavailability = weightGlobalUnavailability;
        this.lecturersUnavailability = lecturersUnavailability;
        this.weightLecturersUnavailability = weightLecturersUnavailability;
        this.synchronise = synchronise;
        this.constraintsSynchronisation = constraintsSynchronisation;
        this.constraintsOfUEs = constraintsOfUEs;
        this.UEInterlacing = UEInterlacing;
        this.middayBreak = middayBreak;
        this.startMiddayBreak = startMiddayBreak;
        this.endMiddayBreak = endMiddayBreak;
        this.middayGrouping = middayGrouping;
        this.weightMiddayGrouping = weightMiddayGrouping;
        this.lessonBalancing = lessonBalancing;
        this.weightLessonBalancing = weightLessonBalancing;
        this.lessonGrouping = lessonGrouping;
        this.weightLessonGrouping = weightLessonGrouping;
        this.weightMaxTimeWithoutLesson = weightMaxTimeWithoutLesson;
    }

    public static List<CUE> extractsCues(Planning planning) {
        List<CUE> cues = new ArrayList<>();
        if (planning.getConstraintsOfUEs()!=null && !planning.getConstraintsOfUEs().isEmpty()) {
            for (ConstraintsOfUE c : planning.getConstraintsOfUEs()) {
                cues.add(new CUE(c));
            }
        }
        return cues;
    }

    public static List<CSyncrho> extractCSynchro(Planning planning) {
        List<CSyncrho> cs = new ArrayList<>();
        if (planning.getConstraintsSynchronisation()!=null && !planning.getConstraintsSynchronisation().isEmpty()) {
            for (ConstraintSynchroniseWithTAF c : planning.getConstraintsSynchronisation()) {
                cs.add(new CSyncrho(c));
            }
        }
        return cs;
    }

    public Config(Planning planning) {
        this(
                planning.getId(),
                planning.getName(),
                planning.getCalendar().getId(),
                planning.isGlobalUnavailability(),
                planning.getWeightGlobalUnavailability(),
                planning.isLecturersUnavailability(),
                planning.getWeightLecturersUnavailability(),
                planning.isSynchronise(),
                extractCSynchro(planning),
                extractsCues(planning),
                planning.isUEInterlacing(),
                planning.isMiddayBreak(),
                planning.getStartMiddayBreak(),
                planning.getEndMiddayBreak(),
                planning.isMiddayGrouping(),
                planning.getWeightMiddayGrouping(),
                planning.isLessonBalancing(),
                planning.getWeightLessonBalancing(),
                planning.isLessonGrouping(),
                planning.getWeightLessonGrouping(),
                planning.getWeightMaxTimeWithoutLesson()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCalendar() {
        return calendar;
    }

    public void setCalendar(Long calendar) {
        this.calendar = calendar;
    }

    public Boolean isGlobalUnavailability() {
        return globalUnavailability;
    }

    public void setGlobalUnavailability(boolean globalUnavailability) {
        this.globalUnavailability = globalUnavailability;
    }

    public Integer getWeightGlobalUnavailability() {
        return weightGlobalUnavailability;
    }

    public void setWeightGlobalUnavailability(int weightGlobalUnavailability) {
        this.weightGlobalUnavailability = weightGlobalUnavailability;
    }

    public Boolean isLecturersUnavailability() {
        return lecturersUnavailability;
    }

    public void setLecturersUnavailability(boolean lecturersUnavailability) {
        this.lecturersUnavailability = lecturersUnavailability;
    }

    public Integer getWeightLecturersUnavailability() {
        return weightLecturersUnavailability;
    }

    public void setWeightLecturersUnavailability(int weightLecturersUnavailability) {
        this.weightLecturersUnavailability = weightLecturersUnavailability;
    }

    public Boolean isSynchronise() {
        return synchronise;
    }

    public void setSynchronise(boolean synchronise) {
        this.synchronise = synchronise;
    }

    public List<CSyncrho> getConstraintsSynchronisation() {
        return constraintsSynchronisation;
    }

    public void setConstraintsSynchronisation(List<CSyncrho> constraintsSynchronisation) {
        this.constraintsSynchronisation = constraintsSynchronisation;
    }

    public List<CUE> getConstraintsOfUEs() {
        return constraintsOfUEs;
    }

    public void setConstraintsOfUEs(List<CUE> constraintsOfUEs) {
        this.constraintsOfUEs = constraintsOfUEs;
    }

    public Boolean getUEInterlacing() {
        return UEInterlacing;
    }

    @JsonProperty("UEInterlacing")
    public void setUEInterlacing(Boolean UEInterlacing) {
        this.UEInterlacing = UEInterlacing;
    }

    public Boolean isMiddayBreak() {
        return middayBreak;
    }

    public void setMiddayBreak(boolean middayBreak) {
        this.middayBreak = middayBreak;
    }

    public LocalTime getStartMiddayBreak() {
        return startMiddayBreak;
    }

    public void setStartMiddayBreak(LocalTime startMiddayBreak) {
        this.startMiddayBreak = startMiddayBreak;
    }

    public LocalTime getEndMiddayBreak() {
        return endMiddayBreak;
    }

    public void setEndMiddayBreak(LocalTime endMiddayBreak) {
        this.endMiddayBreak = endMiddayBreak;
    }

    public Boolean isMiddayGrouping() {
        return middayGrouping;
    }

    public void setMiddayGrouping(boolean middayGrouping) {
        this.middayGrouping = middayGrouping;
    }

    public Integer getWeightMiddayGrouping() {
        return weightMiddayGrouping;
    }

    public void setWeightMiddayGrouping(int weightMiddayGrouping) {
        this.weightMiddayGrouping = weightMiddayGrouping;
    }

    public Integer getWeightLessonBalancing() {
        return weightLessonBalancing;
    }

    public void setWeightLessonBalancing(int weightLessonBalancing) {
        this.weightLessonBalancing = weightLessonBalancing;
    }

    public Boolean isLessonBalancing() {
        return lessonBalancing;
    }

    public void setLessonBalancing(boolean lessonBalancing) {
        this.lessonBalancing = lessonBalancing;
    }

    public Boolean isLessonGrouping() {
        return lessonGrouping;
    }

    public void setLessonGrouping(boolean lessonGrouping) {
        this.lessonGrouping = lessonGrouping;
    }

    public Integer getWeightLessonGrouping() {
        return weightLessonGrouping;
    }

    public void setWeightLessonGrouping(int weightLessonGrouping) {
        this.weightLessonGrouping = weightLessonGrouping;
    }

    public int getWeightMaxTimeWithoutLesson() {
        return weightMaxTimeWithoutLesson;
    }

    public void setWeightMaxTimeWithoutLesson(int weightMaxTimeWithoutLesson) {
        this.weightMaxTimeWithoutLesson = weightMaxTimeWithoutLesson;
    }
}
