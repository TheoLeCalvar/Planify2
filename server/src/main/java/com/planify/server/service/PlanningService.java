package com.planify.server.service;

import com.planify.server.controller.returnsClass.Config;
import com.planify.server.models.*;
import com.planify.server.models.Planning.Status;
import com.planify.server.models.constraints.ConstraintSynchroniseWithTAF;
import com.planify.server.models.constraints.ConstraintsOfUE;
import com.planify.server.repo.PlanningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanningService {

    @Autowired
    private PlanningRepository planningRepository;

    @Autowired
    private  SlotService slotService;

    @Autowired
    private  LessonService lessonService;
    
    @Autowired
    private ConstraintsOfUEService constraintsOfUEService;
    
    @Autowired
    private ConstraintSynchroniseWithTAFService constraintSynchroniseWithTAFService;

    final String RESET = "\u001B[0m";
    final String RED = "\u001B[31m";
    final String GREEN = "\u001B[32m";

    public Planning addPlanning(Calendar calendar) {
        Planning planning = new Planning(calendar);
        planningRepository.save(planning);
        return planning;
    }

    public Planning addPlanning(Calendar calendar, String name, boolean globalUnavailability, int weightGlobalUnavailability, boolean lecturersUnavailability, int weightLecturersUnavailability, boolean synchronise, int weightMaxTimeWithoutLesson, boolean UEInterlacing, boolean middayBreak, LocalTime startMiddayBreak, LocalTime endMiddayBreak, boolean middayGrouping, int weightMiddayGrouping, boolean lessonBalancing, int weightLessonBalancing, int weightLessonGrouping, boolean lessonGrouping, LocalTime maxSolveDuration) {
        Planning planning = new Planning(calendar, name, globalUnavailability, weightGlobalUnavailability, lecturersUnavailability, weightLecturersUnavailability, synchronise, weightMaxTimeWithoutLesson, UEInterlacing, middayBreak, startMiddayBreak, endMiddayBreak,middayGrouping, weightMiddayGrouping,lessonBalancing, weightLessonBalancing, weightLessonGrouping, lessonGrouping, maxSolveDuration);
        planningRepository.save(planning);
        return planning;
    }


    public  void save(Planning p) {
        System.out.println("PlanningService" + p.toString());
        planningRepository.save(p);
    }

    public Optional<Planning> findById(Long id) {
        return planningRepository.findById(id);
    }

    public boolean existById(Long id) {
        return planningRepository.existsById(id);
    }

    public List<Planning> findByCalendar(Calendar calendar) {
        return planningRepository.findByCalendar(calendar);
    }

    public  boolean delete(Long id) {
        if (planningRepository.existsById(id)) {
        	Planning planning = planningRepository.findById(id).get();
        	planning.getConstrainedSynchronisations().forEach(cedS -> constraintSynchroniseWithTAFService.deleteById(cedS.getId()));
        	planning.getConstraintsSynchronisation().forEach(cS -> constraintSynchroniseWithTAFService.deleteById(cS.getId()));
        	planning.getConstraintsOfUEs().forEach(cUe -> constraintsOfUEService.deleteById(cUe.getId()));
        	
            planningRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public void addScheduledLessons(Planning planning, List<Result> results) throws IllegalArgumentException {
        System.out.println(GREEN + "dans add scheduled lesson" + RESET);
        List<ScheduledLesson> scheduledLessons = new ArrayList<>();
        for (Result result: results) {
            System.out.println(result.toString());
            Optional<Slot> oSlot = slotService.findById(result.getId());
            Slot slot = oSlot.orElseThrow(() -> new IllegalArgumentException("The Slot does not exist"));
            LocalDateTime start = slot.getStart();
            LocalDateTime end = slot.getEnd();
            Optional<Lesson> oLesson = lessonService.findById(result.getIdLesson());
            Lesson lesson = oLesson.orElseThrow(() -> new IllegalArgumentException("The Lesson does not exist"));
            String ue = lesson.getUe().getName();
            String title = lesson.getName();
            String description = lesson.getDescription();
            List<String> lecturers = lesson.getLessonLecturers()
                    .stream()
                    .map(ll -> ll.getUser().getFullName())
                    .toList();
            ScheduledLesson scheduledLesson = new ScheduledLesson(result.getIdLesson(), start, end, ue, title, description, lecturers);
            scheduledLessons.add(scheduledLesson);
        }
        System.out.println(RED + scheduledLessons + RESET);
        planning.setScheduledLessons(scheduledLessons);
        planningRepository.save(planning);
    }
    
    public void updateConfig(Planning planning, Config config) {
        if (config.getName() != null) planning.setName(config.getName());
        if (config.isGlobalUnavailability() != null) planning.setGlobalUnavailability(config.isGlobalUnavailability());
        if (config.getWeightGlobalUnavailability() != null) planning.setWeightGlobalUnavailability(config.getWeightGlobalUnavailability());
        if (config.isLecturersUnavailability() != null) planning.setLecturersUnavailability(config.isLecturersUnavailability());
        if (config.getWeightLecturersUnavailability() != null) planning.setWeightLecturersUnavailability(config.getWeightLecturersUnavailability());
        if (config.isSynchronise() != null) planning.setSynchronise(config.isSynchronise());
        if (config.getUEInterlacing() != null) planning.setUEInterlacing(config.getUEInterlacing());
        if (config.isMiddayBreak() != null) planning.setMiddayBreak(config.isMiddayBreak());
        if (config.getStartMiddayBreak() != null) planning.setStartMiddayBreak(config.getStartMiddayBreak());
        if (config.getEndMiddayBreak() != null) planning.setEndMiddayBreak(config.getEndMiddayBreak());
        if (config.isMiddayGrouping() != null) planning.setMiddayGrouping(config.isMiddayGrouping());
        if (config.getWeightMiddayGrouping() != null) planning.setWeightMiddayGrouping(config.getWeightMiddayGrouping());
        if (config.isLessonBalancing() != null) planning.setLessonBalancing(config.isLessonBalancing());
        if (config.getWeightLessonBalancing() != null) planning.setWeightLessonBalancing(config.getWeightLessonBalancing());
        if (config.isLessonGrouping() != null) planning.setLessonGrouping(config.isLessonGrouping());
        if (config.getWeightLessonGrouping() != null) planning.setWeightLessonGrouping(config.getWeightLessonGrouping());
        if (config.getMaxSolveDuration() != null) planning.setMaxSolveDuration(config.getMaxSolveDuration());
        
        if (config.getConstraintsSynchronisation() != null && !config.getConstraintsSynchronisation().isEmpty()) {
            for (Config.CSyncrho cs : config.getConstraintsSynchronisation()) {
                if (cs.getOtherPlanning() != null) {
                    for (ConstraintSynchroniseWithTAF c :planning.getConstraintsSynchronisation()) {
                        if (cs.getOtherPlanning() == c.getPlanning().getId()) {
                        	constraintSynchroniseWithTAFService.updateConfig(c, cs);
                        }
                    }
                }
            }
        }

        if (config.getConstraintsOfUEs() != null && !config.getConstraintsOfUEs().isEmpty() ) {
            for (Config.CUE cue : config.getConstraintsOfUEs()) {
                if (cue.getUe() != null) {
                    for (ConstraintsOfUE c : planning.getConstraintsOfUEs()) {
                        if (cue.getUe() == c.getUe().getId()) {
                            constraintsOfUEService.updateConfig(c, cue);
                        }
                    }
                }
            }
        }
        save(planning);
    }
    
    public Planning createPlanningForGeneration(Planning planning) {
    	Planning planningGeneration = addPlanning(planning.getCalendar(),
										        planning.getName(),
										        planning.isGlobalUnavailability(),
										        planning.getWeightGlobalUnavailability(),
										        planning.isLecturersUnavailability(),
										        planning.getWeightLecturersUnavailability(),
										        planning.isSynchronise(),
										        planning.getWeightMaxTimeWithoutLesson(),
										        planning.isUEInterlacing(),
										        planning.isMiddayBreak(),
										        planning.getStartMiddayBreak(),
										        planning.getEndMiddayBreak(),
										        planning.isMiddayGrouping(),
										        planning.getWeightMiddayGrouping(),
										        planning.isLessonBalancing(),
										        planning.getWeightLessonBalancing(),
										        planning.getWeightLessonGrouping(),
										        planning.isLessonGrouping(),
										    	planning.getMaxSolveDuration());
    	planningGeneration.waitForProcessing();
        this.save(planningGeneration);
    	planningGeneration.setConstraintsOfUEs(constraintsOfUEService.createForNewPlanning(planning.getConstraintsOfUEs(), planningGeneration));
    	planningGeneration.setConstraintsSynchronisation(constraintSynchroniseWithTAFService.createForNewPlanning(planning.getConstraintsSynchronisation(), planningGeneration));
    	System.out.println(planningGeneration.getConstraintsSynchronisation().size());
    	return planningGeneration;
    }
    

    
    public Planning[] createPlanningsForGeneration(Planning[] planningsToGenerate) {
    	Planning[] plannings = new Planning[planningsToGenerate.length];
    	for (int i = 0; i < plannings.length; i ++) plannings[i] = createPlanningForGeneration(planningsToGenerate[i]);
    	return plannings;
    }
}
