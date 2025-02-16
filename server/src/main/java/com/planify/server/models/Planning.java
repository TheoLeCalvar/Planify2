package com.planify.server.models;

import com.planify.server.models.constraints.ConstraintSynchroniseWithTAF;
import com.planify.server.models.constraints.ConstraintsOfUE;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Planning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "idCalendar")
    private Calendar calendar;

    // Constraints

    // respect of the preference of the global unavailability
    private boolean globalUnavailability;
    private int weightGlobalUnavailability;

    // respect of the preference of the lecturers' unavailibilities
    private boolean lecturersUnavailability;
    private int weightLecturersUnavailability;
    //private List<ConstraintLecturerAvailabilities> constraintsLecturersUnavailibilities;

    // Synchronisation
    private boolean synchronise;

    @OneToMany(mappedBy = "planning", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ConstraintSynchroniseWithTAF> constraintsSynchronisation = new ArrayList<ConstraintSynchroniseWithTAF>();
    
    @OneToMany(mappedBy = "otherPlanning", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ConstraintSynchroniseWithTAF> constrainedSynchronisations = new ArrayList<ConstraintSynchroniseWithTAF>();

    
    // Constraints' UE
    @OneToMany(mappedBy = "planning", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ConstraintsOfUE> constraintsOfUEs = new ArrayList<ConstraintsOfUE>();
    
    
    
    // Avoid or not interlacing the UE
    private boolean UEInterlacing; // false if no interlacing

    // respect the midday break
    private boolean middayBreak;
    private LocalTime startMiddayBreak;
    private LocalTime endMiddayBreak;

    // group the lessons in the middle of the day
    private boolean middayGrouping;
    private int weightMiddayGrouping;

    // Balancing the lessons
    private boolean lessonBalancing;
    private int weightLessonBalancing;

    // Group the lessons by 2 or 3
    private boolean lessonGrouping;
    private int weightLessonGrouping;

    // Weight of time without an UE
    private int weightTimeWithoutUE;

    // Result
    @ElementCollection
    private List<ScheduledLesson> scheduledLessons = new ArrayList<ScheduledLesson>();





    public Planning() {
    }

    public Planning(Calendar calendar) {
        this.calendar = calendar;
        this.scheduledLessons = new ArrayList<ScheduledLesson>();
        this.timestamp = LocalDateTime.now();
    }

    public Planning(Calendar calendar, boolean globalUnavailability, int weightGlobalUnavailability, boolean lecturersUnavailability, int weightLecturersUnavailability, boolean synchronise, List<ConstraintSynchroniseWithTAF> constraintsSynchronisation, List<ConstraintsOfUE> constraintsOfUEs, boolean UEInterlacing, boolean middayBreak, LocalTime startMiddayBreak, LocalTime endMiddayBreak, boolean middayGrouping, int weightMiddayGrouping, boolean lessonBalancing, int weightLessonBalancing, int weightLessonGrouping, boolean lessonGrouping, int weightTimeWithoutUE) {
        this.calendar = calendar;
        this.scheduledLessons = new ArrayList<ScheduledLesson>();
        this.timestamp = LocalDateTime.now();
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
        this.weightLessonGrouping = weightLessonGrouping;
        this.lessonGrouping = lessonGrouping;
        this.weightTimeWithoutUE = weightTimeWithoutUE;
    }







    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public List<ScheduledLesson> getScheduledLessons() {
        return scheduledLessons;
    }

    public void setScheduledLessons(List<ScheduledLesson> scheduledLessons) {
        this.scheduledLessons = scheduledLessons;
    }
    
    public boolean isGenerated() {
    	return !this.getScheduledLessons().isEmpty();
    }

    public boolean isGlobalUnavailability() {
        return globalUnavailability;
    }

    public void setGlobalUnavailability(boolean globalUnavailability) {
        this.globalUnavailability = globalUnavailability;
    }

    public int getWeightGlobalUnavailability() {
        return weightGlobalUnavailability;
    }

    public void setWeightGlobalUnavailability(int weightGlobalUnavailability) {
        this.weightGlobalUnavailability = weightGlobalUnavailability;
    }

    public boolean isSynchronise() {
        return synchronise;
    }

    public void setSynchronise(boolean synchronise) {
        this.synchronise = synchronise;
    }

    public List<ConstraintSynchroniseWithTAF> getConstraintsSynchronisation() {
        return constraintsSynchronisation;
    }

    public void setConstraintsSynchronisation(List<ConstraintSynchroniseWithTAF> constraintsSynchronisation) {
        this.constraintsSynchronisation = constraintsSynchronisation;
    }

    public boolean isUEInterlacing() {
        return UEInterlacing;
    }

    public void setUEInterlacing(boolean UEInterlacing) {
        this.UEInterlacing = UEInterlacing;
    }

    public boolean isMiddayBreak() {
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

    public boolean isMiddayGrouping() {
        return middayGrouping;
    }

    public void setMiddayGrouping(boolean middayGrouping) {
        this.middayGrouping = middayGrouping;
    }

    public int getWeightMiddayGrouping() {
        return weightMiddayGrouping;
    }

    public void setWeightMiddayGrouping(int weightMiddayGrouping) {
        this.weightMiddayGrouping = weightMiddayGrouping;
    }

    public boolean isLessonBalancing() {
        return lessonBalancing;
    }

    public void setLessonBalancing(boolean lessonBalancing) {
        this.lessonBalancing = lessonBalancing;
    }

    public int getWeightLessonBalancing() {
        return weightLessonBalancing;
    }

    public void setWeightLessonBalancing(int weightLessonBalancing) {
        this.weightLessonBalancing = weightLessonBalancing;
    }

    public boolean isLessonGrouping() {
        return lessonGrouping;
    }

    public void setLessonGrouping(boolean lessonGrouping) {
        this.lessonGrouping = lessonGrouping;
    }

    public int getWeightLessonGrouping() {
        return weightLessonGrouping;
    }

    public void setWeightLessonGrouping(int weightLessonGrouping) {
        this.weightLessonGrouping = weightLessonGrouping;
    }

    public boolean isLecturersUnavailability() {
        return lecturersUnavailability;
    }

    public void setLecturersUnavailability(boolean lecturersUnavailability) {
        this.lecturersUnavailability = lecturersUnavailability;
    }

    public int getWeightLecturersUnavailability() {
        return weightLecturersUnavailability;
    }

    public void setWeightLecturersUnavailability(int weightLecturersUnavailability) {
        this.weightLecturersUnavailability = weightLecturersUnavailability;
    }

    public List<ConstraintsOfUE> getConstraintsOfUEs() {
        return constraintsOfUEs;
    }

    public void setConstraintsOfUEs(List<ConstraintsOfUE> constraintsOfUEs) {
        this.constraintsOfUEs = constraintsOfUEs;
    }
    
    public boolean isMaxTimeWithoutLesson() {
    	return this.getConstraintsOfUEs().stream().filter(c -> c.isMaxTimeWithoutLesson()).findAny().isPresent();
    }
    
    public boolean isSpreadingUe() {
    	return this.getConstraintsOfUEs().stream().filter(c -> c.isSpreading()).findAny().isPresent();
    }
    
    public boolean isLessonCountInWeek() {
    	return this.getConstraintsOfUEs().stream().filter(c -> c.isLessonCountInWeek()).findAny().isPresent();
    }

    public int getWeightTimeWithoutUE() {
        return weightTimeWithoutUE;
    }

    public void setWeightTimeWithoutUE(int weightTimeWithoutUE) {
        this.weightTimeWithoutUE = weightTimeWithoutUE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ConstraintSynchroniseWithTAF> getConstrainedSynchronisations() {
        return constrainedSynchronisations;
    }

    public void setConstrainedSynchronisations(List<ConstraintSynchroniseWithTAF> constrainedSynchronisations) {
        this.constrainedSynchronisations = constrainedSynchronisations;
    }

    @Override
    public String toString() {
        return "Planning " + id +
                " { timestamp=" + timestamp +
                ", calendar=" + calendar.getId() +
                ", scheduledLesson= " + scheduledLessons +
                '}';
    }
}