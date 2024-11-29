package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Antecedence;
import com.planify.server.models.Lesson;
import com.planify.server.models.Sequencing;
import com.planify.server.models.Synchronization;
import com.planify.server.models.UE;
import com.planify.server.repo.LessonRepository;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    @Transactional
    public Lesson add(String name, UE ue) {
        Lesson lesson = new Lesson(name, ue);

        lessonRepository.save(lesson);
        return lesson;
    }

    public void save(Lesson lesson) {
        lessonRepository.save(lesson);
    }

    public Optional<Lesson> findById(Long id) {
        Optional<Lesson> lesson = lessonRepository.findById(id);
        return lesson;
    }

    public boolean delete(Long id) {
        if (lessonRepository.existsById(id)) {
            lessonRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
