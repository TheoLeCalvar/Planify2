package com.planify.server.service;

import java.util.List;
import java.util.Optional;

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

    public boolean deleteAntecedence(AntecedenceId id) {
        Optional<Antecedence> antecedenceOptional = antecedenceRepository.findById(id);

        if (antecedenceOptional.isPresent()) {
            antecedenceRepository.deleteById(id);

            return true;
        } else {
            return false;
        }
    }

}
