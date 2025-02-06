package com.planify.server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planify.server.models.Lesson;
import com.planify.server.models.LessonLecturer;
import com.planify.server.models.LessonLecturer.LessonLecturerId;
import com.planify.server.models.User;

@Repository
public interface LessonLecturerRepository extends JpaRepository<LessonLecturer,LessonLecturerId>{

    List<LessonLecturer> findAll();
    
    Optional<LessonLecturer> findById(LessonLecturerId id);

    List<LessonLecturer> findByUser(User user);

    List<LessonLecturer> findByLesson(Lesson lesson);

    boolean existsByUser(User user);

    LessonLecturer save(LessonLecturer lessonLecturer);

    void deleteById(LessonLecturerId id);
    
}
