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
import com.planify.server.models.Block;
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

    @Lazy
    @Autowired
    private LessonLecturerService lessonLecturerService;
    
    @Lazy
    @Autowired
    private GlobalUnavailabilityService globalUnavailabilityService;
    
    @Lazy
    @Autowired
    private DayService dayService;

    @Lazy
    @Autowired
    private BlockService blockService;

    @Transactional
    public Lesson add(String name, String description, UE ue) {
        Lesson lesson = new Lesson();
        lesson.setName(name);
        lesson.setUe(ue);
        lesson.setDescription(description);

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

    public List<Slot> findLessonLecturersUnavailabilitiesByLessonAndCalendarWUD(Lesson lesson, Calendar calendar) {
        List<LessonLecturer> lessonLecturers = findLessonLecturersByLesson(lesson);
        List<UserUnavailability> userUnavailabilities = new ArrayList<UserUnavailability>();

        for (LessonLecturer lecturer : lessonLecturers) {
            List<UserUnavailability> unavailabilities = lecturer.getUser().getUserUnavailabilities();
            userUnavailabilities.addAll(unavailabilities);
        }

        List<Slot> slots = userUnavailabilities.stream()
                .filter(unavailability -> unavailability.getStrict())
                .map(unavailability -> unavailability.getSlot())
                .filter(slot -> slot.getCalendar().getId() == calendar.getId())
                .filter(slot -> dayService.isDayAvailableForCalendar(slot.getDay(), calendar))
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
                .filter(slot -> dayService.isDayAvailableForCalendar(slot.getDay(), calendar))
                .collect(Collectors.toList());

        return slots;
    }

    @Transactional
    public boolean delete(Long id) {
        if (lessonRepository.existsById(id)) {

            Lesson lesson = lessonRepository.findById(id).get();

            // Delete the block if the lesson is the first of one
            List<Block> blocks = blockService.findByFirstLesson(lesson);
            if (blocks != null && !blocks.isEmpty()) {
                while (!blocks.isEmpty()) {
                    Block block = blocks.remove(0);
                    blockService.deleteBlock(block.getId());
                }
            }

            // Changing the order of the lessons
            List<Antecedence> listWhereLessonIsPrevious = lesson.getAntecedencesAsPrevious();
            List<Antecedence> listWhereLessonsIsNext = lesson.getAntecedencesAsNext();
            if (listWhereLessonIsPrevious != null && !listWhereLessonIsPrevious.isEmpty()) {
                while (!listWhereLessonIsPrevious.isEmpty()) {
                    Antecedence a = listWhereLessonIsPrevious.removeFirst();
                    antecedenceService.deleteAntecedence(a.getId());
                }
            }
            if (listWhereLessonsIsNext != null && !listWhereLessonsIsNext.isEmpty()) {
                while (!listWhereLessonsIsNext.isEmpty()) {
                    Antecedence a = listWhereLessonsIsNext.removeFirst();
                    antecedenceService.deleteAntecedence(a.getId());
                }
            }

            // Changing the sequencing of the lesson
            // Changing the order of the lessons
            List<Sequencing> listPrevious = lesson.getSequencingsAsPrevious();
            List<Sequencing> listNext = lesson.getSequencingsAsNext();
            if (listPrevious != null && !listPrevious.isEmpty()) {
                while (!listPrevious.isEmpty()) {
                    Sequencing s = listPrevious.removeFirst();
                    sequencingService.delete(s.getId());
                }
            }
            if (listNext != null && !listNext.isEmpty()) {
                while (!listNext.isEmpty()) {
                    Sequencing s = listNext.removeFirst();
                    sequencingService.delete(s.getId());
                }
            }

            // Delete the synchronization of this lesson
            List<Synchronization> s1 = lesson.getSynchronizations1();
            List<Synchronization> s2 = lesson.getSynchronizations2();
            s1.forEach(s -> synchronizationService.deleteSynchronization(s.getId()));
            s2.forEach(s -> synchronizationService.deleteSynchronization(s.getId()));

            // Delete the lessonLecturer of the lesson
            lesson.getLessonLecturers().forEach(ll -> lessonLecturerService.deleteLessonLecturer(ll.getId()));

            // Delete the lesson in the lesson's table
            lessonRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public List<Lesson> findByUE(UE ue) {
        return lessonRepository.findByUe(ue);
    }

    public void deleteDependencies(Lesson lesson) {
        lesson.getAntecedencesAsNext().forEach(a -> antecedenceService.deleteAntecedence(a.getId()));
        lesson.getAntecedencesAsPrevious().forEach(a -> antecedenceService.deleteAntecedence(a.getId()));
        lesson.getSequencingsAsNext().forEach(s -> sequencingService.delete(s.getId()));
        lesson.getSequencingsAsPrevious().forEach(s -> sequencingService.delete(s.getId()));
        if (lesson.getLessonLecturers() != null) {
            List<LessonLecturer> lecturers = new ArrayList<LessonLecturer>(lesson.getLessonLecturers());
            lecturers.forEach(ll -> lessonLecturerService.deleteLessonLecturer(ll.getId()));
        }
    }

}
