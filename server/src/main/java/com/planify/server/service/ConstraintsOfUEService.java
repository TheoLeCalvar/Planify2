package com.planify.server.service;

import com.planify.server.controller.returnsClass.Config;
import com.planify.server.models.Lesson;
import com.planify.server.models.Planning;
import com.planify.server.models.UE;
import com.planify.server.models.constraints.ConstraintsOfUE;
import com.planify.server.repo.ConstraintsOfUERepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class ConstraintsOfUEService {

    @Autowired
    private ConstraintsOfUERepository constraintsOfUERepository;
    
    public void save(ConstraintsOfUE constraintsOfUE) {
    	constraintsOfUERepository.save(constraintsOfUE);
    }

    List<ConstraintsOfUE> findAll() {
        return constraintsOfUERepository.findAll();
    }

    Optional<ConstraintsOfUE> findById(ConstraintsOfUE.ConstraintsOfUEId id) {
        return constraintsOfUERepository.findById(id);
    }

    public ConstraintsOfUE add(UE ue, Planning planning, boolean lessonCount, int maxLesson, int minLesson, boolean maxTimeWithoutLesson, boolean maxTimeWLUnitInWeeks, int maxTimeWLDuration, boolean spreading, int maxSpreading, int minSpreading, int[] lessonGroupingNbLessons) {
        return  constraintsOfUERepository.save(new ConstraintsOfUE(ue, planning, lessonCount, maxLesson, minLesson, maxTimeWithoutLesson, maxTimeWLUnitInWeeks, maxTimeWLDuration, spreading,maxSpreading, minSpreading, lessonGroupingNbLessons));
    }

    boolean deleteById(ConstraintsOfUE.ConstraintsOfUEId id) {
        if (constraintsOfUERepository.existsById(id)) {
            constraintsOfUERepository.deleteById(id);
            return true;
        }
        return  false;
    }

    List<ConstraintsOfUE> findByUe(UE ue) {
        return  constraintsOfUERepository.findByUe(ue);
    }

    List<ConstraintsOfUE> findByPlanning(Planning planning) {
        return constraintsOfUERepository.findByPlanning(planning);
    }
    
    public List<ConstraintsOfUE> createForNewPlanning(List<ConstraintsOfUE> cUes, Planning newPlanning){
    	List<ConstraintsOfUE> newCUes = cUes.stream().map(cUe -> add(cUe.getUe(), newPlanning, cUe.isLessonCountInWeek(), cUe.getMaxLessonInWeek(), cUe.getMinLessonInWeek(), cUe.isMaxTimeWithoutLesson(), cUe.isMaxTimeWLUnitInWeeks(), cUe.getMaxTimeWLDuration(), cUe.isSpreading(), cUe.getMaxSpreading(), cUe.getMinSpreading(), cUe.getLessonGroupingNbLessons())).toList();
    	for (ConstraintsOfUE coue : newCUes) {
            constraintsOfUERepository.save(coue);
        }
    	return newCUes;
    }
    

    public ConstraintsOfUE updateConfig(ConstraintsOfUE consUe, Config.CUE cue) {
        if (cue.isLessonCountInWeek() != null) consUe.setLessonCountInWeek(cue.isLessonCountInWeek());
        if (cue.getMaxLessonInWeek() != null) consUe.setMaxLessonInWeek(cue.getMaxLessonInWeek());
        if (cue.getMinLessonInWeek() != null) consUe.setMinLessonInWeek(cue.getMinLessonInWeek());
        if (cue.isMaxTimeWithoutLesson() != null) consUe.setMaxTimeWithoutLesson(cue.isMaxTimeWithoutLesson());
        if (cue.isMaxTimeWLUnitInWeeks() != null) consUe.setMaxTimeWLUnitInWeeks(cue.isMaxTimeWLUnitInWeeks());
        if (cue.getMaxTimeWLDuration() != null) consUe.setMaxTimeWLDuration(cue.getMaxTimeWLDuration());
        if (cue.isSpreading() != null) consUe.setSpreading(cue.isSpreading());
        if (cue.getMaxSpreading() != null) consUe.setMaxSpreading(cue.getMaxSpreading());
        if (cue.getMinSpreading() != null) consUe.setMinSpreading(cue.getMinSpreading());
        if (cue.getLessonGroupingNbLessons() != null) consUe.setLessonGroupingNbLessons(cue.getLessonGroupingNbLessons());
        save(consUe);
        return consUe;
    }
}
