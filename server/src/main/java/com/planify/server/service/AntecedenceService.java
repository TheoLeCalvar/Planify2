package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.Antecedence;
import com.planify.server.models.Lesson;
import com.planify.server.models.Antecedence.AntecedenceId;
import com.planify.server.repo.AntecedenceRepository;

@Service
public class AntecedenceService {

    @Autowired
    private AntecedenceRepository antecedenceRepository;

    public Antecedence add(Lesson previousLesson, Lesson nextLesson) {
        Antecedence antecedence = new Antecedence(previousLesson,nextLesson);

        // Update previous antecedences for lesson
        List<Antecedence> antecedencesAsPrevious = previousLesson.getAntecedencesAsPrevious();
        antecedencesAsPrevious.addLast(antecedence);
        previousLesson.setAntecedencesAsPrevious(antecedencesAsPrevious);

        // Update next antecedences for lesson
        List<Antecedence> antecedencesAsNext = nextLesson.getAntecedencesAsNext();
        antecedencesAsNext.addLast(antecedence);
        nextLesson.setAntecedencesAsNext(antecedencesAsNext);

        // Save new object in repository
        antecedenceRepository.save(antecedence);
        return antecedence;
    }

    public Optional<Antecedence> findById(AntecedenceId id) {
        Optional<Antecedence> antecedence = antecedenceRepository.findById(id);
        return antecedence;
    }

    public boolean delete(AntecedenceId id) {
        Optional<Antecedence> antecedenceOptional = antecedenceRepository.findById(id);

        if (antecedenceOptional.isPresent()) {
            Antecedence antecedence = antecedenceOptional.get();

            // Update previous antecedences for lesson
            List<Antecedence> antecedencesAsPrevious = antecedence.getPreviouLesson().getAntecedencesAsPrevious();
            antecedencesAsPrevious.remove(antecedence);
            antecedence.getPreviouLesson().setAntecedencesAsPrevious(antecedencesAsPrevious);

            // Update next antecedences for lesson
            List<Antecedence> antecedencesAsNext = antecedence.getNextLesson().getAntecedencesAsNext();
            antecedencesAsNext.remove(antecedence);
            antecedence.getNextLesson().setAntecedencesAsNext(antecedencesAsNext);

            // Then delete it
            antecedenceRepository.deleteById(id);

            return true;
        } else {
            return false;
        }
    }
    
}
