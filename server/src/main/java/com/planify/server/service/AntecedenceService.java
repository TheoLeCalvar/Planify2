package com.planify.server.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.Antecedence;
import com.planify.server.models.Lesson;
import com.planify.server.models.Antecedence.AntecedenceId;
import com.planify.server.repo.AntecedenceRepository;
import com.planify.server.repo.LessonRepository;

@Service
public class AntecedenceService {

    @Autowired
    private AntecedenceRepository antecedenceRepository;

    @Autowired
    private LessonRepository lessonService;

    public Antecedence addAntecedence(Lesson previousLesson, Lesson nextLesson) {
        Antecedence antecedence = new Antecedence(previousLesson, nextLesson);

        // Update previous antecedences for lesson
        List<Antecedence> antecedencesAsPrevious = previousLesson.getAntecedencesAsPrevious();
        antecedencesAsPrevious.addLast(antecedence);
        previousLesson.setAntecedencesAsPrevious(antecedencesAsPrevious);
        lessonService.save(previousLesson);

        // Update next antecedences for lesson
        List<Antecedence> antecedencesAsNext = nextLesson.getAntecedencesAsNext();
        antecedencesAsNext.addLast(antecedence);
        nextLesson.setAntecedencesAsNext(antecedencesAsNext);
        lessonService.save(nextLesson);

        // Save new object in repository
        antecedenceRepository.save(antecedence);
        return antecedence;
    }

    public void save(Antecedence antecedence) {
        antecedenceRepository.save(antecedence);
    }

    public Optional<Antecedence> findById(AntecedenceId id) {
        Optional<Antecedence> antecedence = antecedenceRepository.findById(id);
        return antecedence;
    }

    public List<Antecedence> findAll() {
        return antecedenceRepository.findAll();
    }

    public List<Antecedence> getAntecedencesByIdTaf(Long idTaf) {

        List<Antecedence> antecedences = findAll().stream()
            .filter(antecedence -> antecedence.getPreviousLesson().getUe().getTaf().getId() == idTaf)
            .collect(Collectors.toList());

        return antecedences;
    }

    public boolean deleteAntecedence(AntecedenceId id) {
        Optional<Antecedence> antecedenceOptional = antecedenceRepository.findById(id);

        if (antecedenceOptional.isPresent()) {
            Antecedence antecedence = antecedenceOptional.get();

            // Update previous antecedences for lesson
            List<Antecedence> antecedencesAsPrevious = antecedence.getPreviousLesson().getAntecedencesAsPrevious();
            antecedencesAsPrevious.remove(antecedence);
            antecedence.getPreviousLesson().setAntecedencesAsPrevious(antecedencesAsPrevious);
            lessonService.save(antecedence.getPreviousLesson());

            // Update next antecedences for lesson
            List<Antecedence> antecedencesAsNext = antecedence.getNextLesson().getAntecedencesAsNext();
            antecedencesAsNext.remove(antecedence);
            antecedence.getNextLesson().setAntecedencesAsNext(antecedencesAsNext);
            lessonService.save(antecedence.getNextLesson());

            // Then delete it
            antecedenceRepository.deleteById(id);

            return true;
        } else {
            return false;
        }
    }

}
