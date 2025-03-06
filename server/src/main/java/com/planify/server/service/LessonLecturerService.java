package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.LessonLecturer.LessonLecturerId;
import com.planify.server.models.GlobalUnavailability;
import com.planify.server.models.Lesson;
import com.planify.server.models.LessonLecturer;
import com.planify.server.models.User;
import com.planify.server.repo.LessonLecturerRepository;

@Service
public class LessonLecturerService {

    @Autowired
    private LessonLecturerRepository lessonLecturerRepository;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private UserService userService;

    public LessonLecturer addLessonLecturer(User user, Lesson lesson) {
        LessonLecturer lessonLecturer = new LessonLecturer(user, lesson);

        // Save new object in repository
        lessonLecturerRepository.save(lessonLecturer);
        
        
        return lessonLecturer;
    }

    public void save(LessonLecturer lessonLecturer) {
        lessonLecturerRepository.save(lessonLecturer);
    }

    public Optional<LessonLecturer> findById(LessonLecturerId id) {
        Optional<LessonLecturer> lessonLecturer = lessonLecturerRepository.findById(id);
        return lessonLecturer;
    }

    public List<LessonLecturer> findAll() {
        return lessonLecturerRepository.findAll();
    }

    public boolean deleteLessonLecturer(LessonLecturerId id) {
        if (lessonLecturerRepository.existsById(id)) {
            lessonLecturerRepository.deleteById(id);

            return true;
        } else {
        	System.out.println("Not Found !!!!");
            return false;
        }
    }
}
