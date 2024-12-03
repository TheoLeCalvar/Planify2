package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Antecedence;
import com.planify.server.models.Lesson;
import com.planify.server.models.LessonLecturer;
import com.planify.server.models.Sequencing;
import com.planify.server.models.Synchronization;
import com.planify.server.models.UE;
import com.planify.server.repo.LessonRepository;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    @Lazy
    @Autowired
    private UEService ueService;

    @Lazy
    @Autowired
    private AntecedenceService antecedenceService;

    @Lazy
    @Autowired
    private SequencingService sequencingService;

    @Lazy
    @Autowired
    private SynchronizationService synchronizationService;

    @Transactional
    public Lesson add(String name, UE ue) {
        Lesson lesson = new Lesson(name, ue);

        // Update lessons list for ue
        List<Lesson> lessons = ue.getLessons();
        lessons.addLast(lesson);
        ue.setLessons(lessons);
        ueService.save(ue);

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

    public List<Lesson> findAll() {
        return lessonRepository.findAll();
    }

    public List<LessonLecturer> findLessonLecturersByLesson(Lesson lesson) {
        List<LessonLecturer> lessonLecturers = lesson.getLessonLecturers();
        return lessonLecturers;
    }

    @Transactional
    public boolean delete(Long id) {
        if (lessonRepository.existsById(id)) {

            Lesson lesson = lessonRepository.findById(id).get();

            // Update lessons list for ue
            List<Lesson> lessons = lesson.getUe().getLessons();
            lessons.remove(lesson);
            lesson.getUe().setLessons(lessons);

            // Changing the order of the lessons
            List<Antecedence> listWhereLessonIsPrevious = lesson.getAntecedencesAsPrevious();
            List<Antecedence> listWhereLessonsIsNext = lesson.getAntecedencesAsNext();
            for (Antecedence a : listWhereLessonIsPrevious) {
                antecedenceService.deleteAntecedence(a.getId());
            }
            for (Antecedence a : listWhereLessonsIsNext) {
                antecedenceService.deleteAntecedence(a.getId());
            }

            // Changing the sequencing of the lesson
            // Changing the order of the lessons
            List<Sequencing> listPrevious = lesson.getSequencingsAsPrevious();
            List<Sequencing> listNext = lesson.getSequencingsAsNext();
            for (Sequencing s : listPrevious) {
                sequencingService.delete(s.getId());
            }
            for (Sequencing s : listNext) {
                sequencingService.delete(s.getId());
            }

            // Delete the synchronization of this lesson
            List<Synchronization> s1 = lesson.getSynchronizations1();
            List<Synchronization> s2 = lesson.getSynchronizations2();
            for (Synchronization s : s1) {
                synchronizationService.deleteSynchronization(s.getId());
            }
            for (Synchronization s : s2) {
                synchronizationService.deleteSynchronization(s.getId());
            }

            // Delete the lesson in the lesson's table
            lessonRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}