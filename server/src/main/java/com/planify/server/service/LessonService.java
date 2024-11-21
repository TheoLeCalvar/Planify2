package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.Block;
import com.planify.server.models.Calendar;
import com.planify.server.models.Lesson;
import com.planify.server.models.UE;
import com.planify.server.repo.BlockRepository;
import com.planify.server.repo.LessonRepository;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    public Lesson add(String name, UE ue) {
        Lesson lesson = new Lesson(name,ue);

        // Update lessons list for ue
        List<Lesson> lessons = ue.getLessons();
        lessons.addLast(lesson);
        ue.setLessons(lessons);

        lessonRepository.save(lesson);
        return lesson;
    }

    public Optional<Lesson> findById(Long id) {
        Optional<Lesson> lesson = lessonRepository.findById(id);
        return lesson;
    }

    public boolean delete(Long id) {
        if (lessonRepository.existsById(id)) {

            Lesson lesson = lessonRepository.findById(id).get();

            // Update lessons list for ue
            List<Lesson> lessons = lesson.getUe().getLessons();
            lessons.remove(lesson);
            lesson.getUe().setLessons(lessons);

            lessonRepository.deleteById(id);
            return true;
        }
        else {
            return false;
        }
    }
    
}
