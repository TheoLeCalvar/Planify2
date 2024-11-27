package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.Sequencing.SequencingId;
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

        // Update previous sequencings for lesson
        List<Sequencing> sequencingsAsPrevious = previousLesson.getSequencingsAsPrevious();
        sequencingsAsPrevious.addLast(sequencing);
        previousLesson.setSequencingsAsPrevious(sequencingsAsPrevious);
        lessonService.save(previousLesson);

        // Update next sequencings for lesson
        List<Sequencing> sequencingsAsNext = nextLesson.getSequencingsAsNext();
        sequencingsAsNext.addLast(sequencing);
        nextLesson.setSequencingsAsNext(sequencingsAsNext);
        lessonService.save(nextLesson);

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

    public boolean delete(SequencingId id) {
        if (sequencingRepository.existsById(id)) {
            Sequencing sequencing = sequencingRepository.findById(id).get();

            // Update sequencings for previous lesson
            List<Sequencing> sequencings = sequencing.getPreviousLesson().getSequencingsAsPrevious();
            sequencings.remove(sequencing);
            sequencing.getPreviousLesson().setSequencingsAsPrevious(sequencings);
            lessonService.save(sequencing.getPreviousLesson());

            // Update sequencings for next lesson
            List<Sequencing> sequencings2 = sequencing.getNextLesson().getSequencingsAsNext();
            sequencings2.remove(sequencing);
            sequencing.getNextLesson().setSequencingsAsNext(sequencings2);
            lessonService.save(sequencing.getNextLesson());

            // Then delete it
            sequencingRepository.deleteById(id);

            return true;
        } else {
            return false;
        }
    }

}
