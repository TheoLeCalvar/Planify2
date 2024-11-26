package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.LessonLecturer.LessonLecturerId;
import com.planify.server.models.Lesson;
import com.planify.server.models.LessonLecturer;
import com.planify.server.models.User;
import com.planify.server.repo.LessonLecturerRepository;

@Service
public class LessonLecturerService {

    @Autowired
    private LessonLecturerRepository lessonLecturerRepository;

    public LessonLecturer add(User user, Lesson lesson) {
        LessonLecturer lessonLecturer = new LessonLecturer(user,lesson);

        // Update lesson lecturers for user
        List<LessonLecturer> lessonLecturers = user.getLessonLecturers();
        lessonLecturers.addLast(lessonLecturer);
        user.setLessonLecturers(lessonLecturers);

        // Update lesson lecturers for lesson
        List<LessonLecturer> lessonLecturers2 = lesson.getLessonLecturers();
        lessonLecturers2.addLast(lessonLecturer);
        lesson.setLessonLecturers(lessonLecturers2);

        // Save new object in repository
        lessonLecturerRepository.save(lessonLecturer);
        return lessonLecturer;
    }

    public Optional<LessonLecturer> findById(LessonLecturerId id) {
        Optional<LessonLecturer> lessonLecturer = lessonLecturerRepository.findById(id);
        return lessonLecturer;
    }

    public boolean delete(LessonLecturerId id) {

        if (lessonLecturerRepository.existsById(id)) {
            LessonLecturer lessonLecturer = lessonLecturerRepository.findById(id).get();

            // Update lesson lecturers for user
            List<LessonLecturer> lessonLecturers = lessonLecturer.getUser().getLessonLecturers();
            lessonLecturers.remove(lessonLecturer);
            lessonLecturer.getUser().setLessonLecturers(lessonLecturers);

            // Update lesson lecturers for lesson
            List<LessonLecturer> lessonLecturers2 = lessonLecturer.getLesson().getLessonLecturers();
            lessonLecturers2.remove(lessonLecturer);
            lessonLecturer.getLesson().setLessonLecturers(lessonLecturers2);

            // Then delete it
            lessonLecturerRepository.deleteById(id);

            return true;
        } else {
            return false;
        }
    }
    
}
