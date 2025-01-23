package com.planify.server.service;

import com.planify.server.models.*;
import com.planify.server.repo.PlanningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanningService {

    @Autowired
    private PlanningRepository planningRepository;

    @Autowired
    private  SlotService slotService;

    @Autowired
    private  LessonService lessonService;

    final String RESET = "\u001B[0m";
    final String RED = "\u001B[31m";
    final String GREEN = "\u001B[32m";

    public Planning addPlanning(Calendar calendar) {
        Planning planning = new Planning(calendar);
        planningRepository.save(planning);
        return planning;
    }

    public  void save(Planning p) {
        planningRepository.save(p);
    }

    public Optional<Planning> findById(Long id) {
        return planningRepository.findById(id);
    }

    public List<Planning> findByCalendar(Calendar calendar) {
        return planningRepository.findByCalendar(calendar);
    }

    public  boolean delete(Long id) {
        if (planningRepository.existsById(id)) {
            planningRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public void addScheduledLessons(Planning planning, List<Result> results) throws IllegalArgumentException {
        System.out.println(GREEN + "dans add scheduled lesson" + RESET);
        List<ScheduledLesson> scheduledLessons = new ArrayList<>();
        for (Result result: results) {
            System.out.println(result.toString());
            Optional<Slot> oSlot = slotService.findById(result.getId());
            Slot slot = oSlot.orElseThrow(() -> new IllegalArgumentException("The Slot does not exist"));
            LocalDateTime start = slot.getStart();
            LocalDateTime end = slot.getEnd();
            Optional<Lesson> oLesson = lessonService.findById(result.getIdLesson());
            Lesson lesson = oLesson.orElseThrow(() -> new IllegalArgumentException("The Lesson does not exist"));
            String ue = lesson.getUe().getName();
            String title = lesson.getName();
            String description = lesson.getDescription();
            List<String> lecturers = lesson.getLessonLecturers()
                    .stream()
                    .map(ll -> ll.getUser().getFullName())
                    .toList();
            ScheduledLesson scheduledLesson = new ScheduledLesson(result.getIdLesson(), start, end, ue, title, description, lecturers);
            scheduledLessons.add(scheduledLesson);
        }
        System.out.println(RED + scheduledLessons + RESET);
        planning.setScheduledLessons(scheduledLessons);
        planningRepository.save(planning);
    }

}
