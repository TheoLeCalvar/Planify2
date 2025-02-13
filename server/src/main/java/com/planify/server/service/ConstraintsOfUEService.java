package com.planify.server.service;

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

    List<ConstraintsOfUE> findAll() {
        return constraintsOfUERepository.findAll();
    }

    Optional<ConstraintsOfUE> findById(ConstraintsOfUE.ConstraintsOfUEId id) {
        return constraintsOfUERepository.findById(id);
    }

    ConstraintsOfUE add(UE ue, Planning planning, boolean lessonCount, int maxLesson, int minLesson, boolean maxTimeWithoutLesson, boolean maxTimeWLUnitInWeeks, int maxTimeWLDuration, boolean spreading, int maxSpreading, int minSpreading) {
        return  constraintsOfUERepository.save(new ConstraintsOfUE(ue, planning, lessonCount, maxLesson, minLesson, maxTimeWithoutLesson, maxTimeWLUnitInWeeks, maxTimeWLDuration, spreading,maxSpreading, minSpreading));
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
}
