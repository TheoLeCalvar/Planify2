package com.planify.server.models;

import com.planify.server.controller.returnsClass.Config;
import com.planify.server.models.constraints.ConstraintSynchroniseWithTAF;
import com.planify.server.models.constraints.ConstraintsOfUE;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private Status status;

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
    
    private int weightMaxTimeWithoutLesson;
    
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


    // Result
    @ElementCollection
    private List<ScheduledLesson> scheduledLessons = new ArrayList<ScheduledLesson>();


    public enum Status {
        GENERATED, PROCESSING, CONFIG, WAITING_TO_BE_PROCESSED
    }


    public Planning() {
    }

    public Planning(Calendar calendar) {
        this.calendar = calendar;
        this.scheduledLessons = new ArrayList<ScheduledLesson>();
        this.timestamp = LocalDateTime.now();
        this.status = Status.CONFIG;
    }

    public Planning(Calendar calendar, String name, boolean globalUnavailability, int weightGlobalUnavailability, boolean lecturersUnavailability, int weightLecturersUnavailability, boolean synchronise, List<ConstraintSynchroniseWithTAF> constraintsSynchronisation, List<ConstraintsOfUE> constraintsOfUEs, int weightMaxTimeWithoutLesson, boolean UEInterlacing, boolean middayBreak, LocalTime startMiddayBreak, LocalTime endMiddayBreak, boolean middayGrouping, int weightMiddayGrouping, boolean lessonBalancing, int weightLessonBalancing, int weightLessonGrouping, boolean lessonGrouping) {
        this.calendar = calendar;
        this.name = name;
        this.scheduledLessons = new ArrayList<ScheduledLesson>();
        this.timestamp = LocalDateTime.now();
        this.globalUnavailability = globalUnavailability;
        this.weightGlobalUnavailability = weightGlobalUnavailability;
        this.lecturersUnavailability = lecturersUnavailability;
        this.weightLecturersUnavailability = weightLecturersUnavailability;
        this.synchronise = synchronise;
        this.constraintsSynchronisation = constraintsSynchronisation;
        this.constraintsOfUEs = constraintsOfUEs;
        this.weightMaxTimeWithoutLesson = weightMaxTimeWithoutLesson;
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
        this.status = Status.CONFIG;
    }

    public Planning(Calendar calendar, String name, boolean globalUnavailability, int weightGlobalUnavailability, boolean lecturersUnavailability, int weightLecturersUnavailability, boolean synchronise, boolean UEInterlacing, boolean middayBreak, LocalTime startMiddayBreak, LocalTime endMiddayBreak, boolean middayGrouping, int weightMiddayGrouping, boolean lessonBalancing, int weightLessonBalancing, int weightLessonGrouping, boolean lessonGrouping) {
        this.calendar = calendar;
        this.name = name;
        this.scheduledLessons = new ArrayList<ScheduledLesson>();
        this.timestamp = LocalDateTime.now();
        this.globalUnavailability = globalUnavailability;
        this.weightGlobalUnavailability = weightGlobalUnavailability;
        this.lecturersUnavailability = lecturersUnavailability;
        this.weightLecturersUnavailability = weightLecturersUnavailability;
        this.synchronise = synchronise;
        this.constraintsSynchronisation = new ArrayList<>();
        this.constraintsOfUEs = new ArrayList<>();
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
        this.status = Status.CONFIG;
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
    
    public int getWeightMaxTimeWithoutLesson() {
		return weightMaxTimeWithoutLesson;
	}

	public void setWeightMaxTimeWithoutLesson(int weightMaxTimeWithoutLesson) {
		this.weightMaxTimeWithoutLesson = weightMaxTimeWithoutLesson;
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
    
    public boolean isMaxTimeWLUnitInDays() {
    	return this.getConstraintsOfUEs().stream().filter(c -> !c.isMaxTimeWLUnitInWeeks()).findAny().isPresent();
    }
    
    public boolean isMaxTimeWLUnitInWeeks() {
    	return this.getConstraintsOfUEs().stream().filter(c -> c.isMaxTimeWLUnitInWeeks()).findAny().isPresent();
    }
    
    public boolean isSpreadingUe() {
    	return this.getConstraintsOfUEs().stream().filter(c -> c.isSpreading()).findAny().isPresent();
    }
    
    public boolean isLessonCountInWeek() {
    	return this.getConstraintsOfUEs().stream().filter(c -> c.isLessonCountInWeek()).findAny().isPresent();
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    public void waitForProcessing() {
        this.status = Status.WAITING_TO_BE_PROCESSED;
    }

    public void startProcessing() {
        this.status = Status.PROCESSING;
    }

    public void endProcessing() {
        this.status = Status.GENERATED;
    }

    @Override
    public String toString() {
        return "Planning " + id +
                " { timestamp=" + timestamp +
                ", calendar=" + calendar.getId() +
                ", scheduledLesson= " + scheduledLessons +
                '}';
    }

    public void updateConfig(Config config) {
        if (config.getName() != null) this.setName(config.getName());
        if (config.isGlobalUnavailability() != null) this.setGlobalUnavailability(config.isGlobalUnavailability());
        if (config.getWeightGlobalUnavailability() != null) this.setWeightGlobalUnavailability(config.getWeightGlobalUnavailability());
        if (config.isLecturersUnavailability() != null) this.setLecturersUnavailability(config.isLecturersUnavailability());
        if (config.getWeightLecturersUnavailability() != null) this.setWeightLecturersUnavailability(config.getWeightLecturersUnavailability());
        if (config.isSynchronise() != null) this.setSynchronise(config.isSynchronise());
        if (config.getUEInterlacing() != null) this.setUEInterlacing(config.getUEInterlacing());
        if (config.isMiddayBreak() != null) this.setMiddayBreak(config.isMiddayBreak());
        if (config.getStartMiddayBreak() != null) this.setStartMiddayBreak(config.getStartMiddayBreak());
        if (config.getEndMiddayBreak() != null) this.setEndMiddayBreak(config.getEndMiddayBreak());
        if (config.isMiddayGrouping() != null) this.setMiddayGrouping(config.isMiddayGrouping());
        if (config.getWeightMiddayGrouping() != null) this.setWeightMiddayGrouping(config.getWeightMiddayGrouping());
        if (config.isLessonBalancing() != null) this.setLessonBalancing(config.isLessonBalancing());
        if (config.getWeightLessonBalancing() != null) this.setWeightLessonBalancing(config.getWeightLessonBalancing());
        if (config.isLessonGrouping() != null) this.setLessonGrouping(config.isLessonGrouping());
        if (config.getWeightLessonGrouping() != null) this.setWeightLessonGrouping(config.getWeightLessonGrouping());

        if (config.getConstraintsSynchronisation() != null && !config.getConstraintsSynchronisation().isEmpty()) {
            for (Config.CSyncrho cs : config.getConstraintsSynchronisation()) {
                if (cs.getOtherPlanning() != null) {
                    for (ConstraintSynchroniseWithTAF c :this.getConstraintsSynchronisation()) {
                        if (cs.getOtherPlanning() == c.getPlanning().getId()) {
                            c.updateConfig(cs);
                        }
                    }
                }
            }
        }

        if (config.getConstraintsOfUEs() != null && !config.getConstraintsOfUEs().isEmpty() ) {
            for (Config.CUE cue : config.getConstraintsOfUEs()) {
                if (cue.getUe() != null) {
                    for (ConstraintsOfUE c : this.constraintsOfUEs) {
                        if (cue.getUe() == c.getUe().getId()) {
                            c.updateConfig(cue);
                        }
                    }
                }
            }
        }
    }
    
    public static Planning setSettingsPlanning(Planning planning) {
		planning.setMiddayBreak(true);
		planning.setStartMiddayBreak(LocalTime.of(12, 00));
		planning.setEndMiddayBreak(LocalTime.of(13, 30));
		planning.setUEInterlacing(true);
		
		planning.setGlobalUnavailability(true);
		planning.setWeightGlobalUnavailability(30);
		planning.setLecturersUnavailability(true);
		planning.setWeightLecturersUnavailability(19);
		planning.setLessonBalancing(true);
		planning.setWeightLessonBalancing(2);
		planning.setLessonGrouping(true);
		planning.setWeightLessonGrouping(5);
		planning.setMiddayGrouping(true);
		planning.setWeightMiddayGrouping(1);
		
		planning.setWeightMaxTimeWithoutLesson(11);
		
		//Add Weight for MaxTimeWithoutLesson !
		//Add lecturerPreference !
		for (UE ue : planning.getCalendar().getTaf().getUes()) {
			ConstraintsOfUE cUe = new ConstraintsOfUE(ue, planning);
			//Max time without lesson of this ue (number of day/week without lessons
			//(i.e. for a duration of 1 week, only max one week in a row without lesson is prefered))
			cUe.setMaxTimeWithoutLesson(true);
			cUe.setMaxTimeWLUnitInWeeks(true);
			cUe.setMaxTimeWLDuration(1);
			
			//Max Lessons in a week for this UE.
			cUe.setLessonCountInWeek(true);
			cUe.setMaxLessonInWeek(6);
			cUe.setMinLessonInWeek(1);
			
			//Min Max number of weeks to do all the lessons of the Ue.
			cUe.setSpreading(false);
			cUe.setMaxSpreading(12);
			cUe.setMinSpreading(2);
			
			planning.getConstraintsOfUEs().add(cUe);
		}
		System.out.println("Planning parameters set !");
		return planning;
	}

    public void addConstraintSynchroniseWithTAF(ConstraintSynchroniseWithTAF c) {
        this.constraintsSynchronisation.add(c);
    }
}