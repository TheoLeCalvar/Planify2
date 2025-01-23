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
        
        // Update lesson lecturers for user
        List<LessonLecturer> lessonLecturers = user.getLessonLecturers();
        lessonLecturers.addLast(lessonLecturer);
        user.setLessonLecturers(lessonLecturers);
        //userService.save(user);

        // Update lesson lecturers for lesson
        List<LessonLecturer> lessonLecturers2 = lesson.getLessonLecturers();
        lessonLecturers2.addLast(lessonLecturer);
        lesson.setLessonLecturers(lessonLecturers2);
        //lessonService.save(lesson);
        
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
            LessonLecturer lessonLecturer = lessonLecturerRepository.findById(id).get();

            // Update lesson lecturers for user
            List<LessonLecturer> lessonLecturers = lessonLecturer.getUser().getLessonLecturers();
            lessonLecturers.remove(lessonLecturer);
            lessonLecturer.getUser().setLessonLecturers(lessonLecturers);
            userService.save(lessonLecturer.getUser());

            // Update lesson lecturers for lesson
            List<LessonLecturer> lessonLecturers2 = lessonLecturer.getLesson().getLessonLecturers();
            lessonLecturers2.remove(lessonLecturer);
            lessonLecturer.getLesson().setLessonLecturers(lessonLecturers2);
            lessonService.save(lessonLecturer.getLesson());

            // Then delete it
            lessonLecturerRepository.deleteById(id);

            return true;
        } else {
        	System.out.println("Not Found !!!!");
            return false;
        }
    }
    
    public boolean deleteLessonLecturerFromLesson(LessonLecturerId id) {
    	
    	if (lessonLecturerRepository.existsById(id)) {
            LessonLecturer lessonLecturer = lessonLecturerRepository.findById(id).get();

            // Update lesson lecturers for user
            List<LessonLecturer> lessonLecturers = lessonLecturer.getUser().getLessonLecturers();
            lessonLecturers.remove(lessonLecturer);
            lessonLecturer.getUser().setLessonLecturers(lessonLecturers);
            userService.save(lessonLecturer.getUser());
            
            // Update lesson lecturers for lesson
            /*System.out.println("Lesson : " + lessonLecturer.getLesson());
            List<LessonLecturer> lessonLecturers2 = lessonLecturer.getLesson().getLessonLecturers();
            lessonLecturers2.remove(lessonLecturer);
            lessonLecturer.getLesson().setLessonLecturers(lessonLecturers2);
            lessonService.save(lessonLecturer.getLesson());*/
            
            // Then delete it
            lessonLecturerRepository.deleteById(id);

            return true;
        } else {
            return false;
        }
    }

}
