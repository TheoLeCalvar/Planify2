package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Sequencing.SequencingId;
import com.planify.server.models.Lesson;
import com.planify.server.models.Sequencing;
import com.planify.server.repo.SequencingRepository;

@Service
public class SequencingService {

    @Autowired
    private SequencingRepository sequencingRepository;

    public Sequencing add(Lesson previousLesson, Lesson nextLesson) {
        Sequencing sequencing = new Sequencing(previousLesson, nextLesson);
        sequencingRepository.save(sequencing);
        return sequencing;
    }

    public void save(Sequencing sequencing) {
        sequencingRepository.save(sequencing);
    }

    public Optional<Sequencing> findById(SequencingId id) {
        Optional<Sequencing> sequencing = sequencingRepository.findById(id);
        return sequencing;
    }

    public boolean delete(SequencingId id) {
        if (sequencingRepository.existsById(id)) {
            sequencingRepository.deleteById(id);

            return true;
        } else {
            return false;
        }
    }

}