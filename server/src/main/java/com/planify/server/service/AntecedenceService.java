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
            antecedenceRepository.deleteById(id);

            return true;
        } else {
            return false;
        }
    }

    public List<Antecedence> findByNextLesson(Lesson lesson) {
        return antecedenceRepository.findByNextLesson(lesson);
    }

}
