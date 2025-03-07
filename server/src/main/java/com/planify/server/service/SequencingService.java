package com.planify.server.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Sequencing.SequencingId;
import com.planify.server.models.UE;
import com.planify.server.models.Lesson;
import com.planify.server.models.LessonLecturer;
import com.planify.server.models.Sequencing;
import com.planify.server.repo.SequencingRepository;

@Service
public class SequencingService {

    @Autowired
    private SequencingRepository sequencingRepository;

    @Autowired
    private LessonService lessonService;

    public Sequencing add(Lesson previousLesson, Lesson nextLesson) {
        Sequencing sequencing = new Sequencing(previousLesson, nextLesson);

        // Save new object in repository
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

    public List<Sequencing> findAll() {
        return sequencingRepository.findAll();
    }

    public boolean delete(SequencingId id) {
        if (sequencingRepository.existsById(id)) {
            sequencingRepository.deleteById(id);

            return true;
        } else {
            return false;
        }
    }

    public List<Sequencing> getSequencingOf(Long idTaf) {
        List<Sequencing> sequencings = sequencingRepository.findAll().stream()
                .filter(sequencing -> sequencing.getPreviousLesson().getUe().getTaf().getId() == idTaf)
                .collect(Collectors.toList());
        return sequencings;
    }

}
