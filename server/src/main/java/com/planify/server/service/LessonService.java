package com.planify.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Antecedence;
import com.planify.server.models.Calendar;
import com.planify.server.models.Lesson;
import com.planify.server.models.LessonLecturer;
import com.planify.server.models.Sequencing;
import com.planify.server.models.Slot;
import com.planify.server.models.Synchronization;
import com.planify.server.models.UE;
import com.planify.server.models.User;
import com.planify.server.models.UserUnavailability;
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
    public Lesson add(String name, String description, UE ue) {
        Lesson lesson = new Lesson();
        lesson.setName(name);
        lesson.setUe(ue);
        lesson.setDescription(description);

        lessonRepository.save(lesson);

        // Update lessons list for ue
        List<Lesson> lessons = ue.getLessons();
        lessons.addLast(lesson);
        ue.setLessons(lessons);
        // ueService.save(ue);

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

    public List<Slot> findLessonLecturersUnavailabilitiesByLessonAndCalendar(Lesson lesson, Calendar calendar) {
        List<LessonLecturer> lessonLecturers = findLessonLecturersByLesson(lesson);
        List<UserUnavailability> userUnavailabilities = new ArrayList<UserUnavailability>();

        for (LessonLecturer lecturer : lessonLecturers) {
            List<UserUnavailability> unavailabilities = lecturer.getUser().getUserUnavailabilities();
            userUnavailabilities.addAll(unavailabilities);
        }

        List<Slot> slots = userUnavailabilities.stream()
                .filter(unavailability -> unavailability.getStrict())
                .map(unavailability -> unavailability.getSlot())
                .filter(slot -> slot.getCalendar() == calendar)
                .collect(Collectors.toList());

        return slots;
    }

    public List<Slot> findNotPreferedSlotsByLessonAndCalendar(Lesson lesson, Calendar calendar) {
        List<Slot> slots = lesson.getLessonLecturers().stream()
                .map(lecturer -> lecturer.getUser())
                .flatMap(user -> user.getUserUnavailabilities().stream())
                .filter(unavailability -> (!unavailability.getStrict())
                        && (unavailability.getSlot().getCalendar() == calendar))
                .map(UserUnavailability::getSlot)
                .collect(Collectors.toList());

        return slots;
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
