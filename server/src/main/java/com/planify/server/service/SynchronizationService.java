package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planify.server.models.Lesson;
import com.planify.server.models.LessonLecturer;
import com.planify.server.models.Synchronization;
import com.planify.server.models.Synchronization.SynchronizationId;
import com.planify.server.repo.SynchronizationRepository;

@Service
public class SynchronizationService {

    @Autowired
    private SynchronizationRepository synchronizationRepository;

    private LessonService lessonService;

    public Synchronization addSynchronization(Lesson lesson1, Lesson lesson2) {
        Synchronization synchronization = new Synchronization(lesson1, lesson2);

        // Add synchronization in the Synchronization's table
        synchronizationRepository.save(synchronization);

        // Add synchronization in the synchronization of the lesson 1
        List<Synchronization> list1 = lesson1.getSynchronizations1();
        list1.add(synchronization);
        lesson1.setSynchronizations1(list1);
        lessonService.save(lesson1);

        // Add synchronization in the synchronization of the lesson 2
        List<Synchronization> list2 = lesson2.getSynchronizations2();
        list2.add(synchronization);
        lesson2.setSynchronizations2(list2);
        lessonService.save(lesson2);

        return synchronization;
    }

    public void save(Synchronization synchronization) {
        synchronizationRepository.save(synchronization);
    }

    public boolean deleteSynchronization(SynchronizationId id) {
        if (synchronizationRepository.existsById(id)) {
            Synchronization sync = synchronizationRepository.findById(id).get();

            // Delete sync in the lesson1 list
            Lesson lesson1 = sync.getLesson1();
            List<Synchronization> list1 = lesson1.getSynchronizations1();
            list1.remove(sync);
            lesson1.setSynchronizations1(list1);
            lessonService.save(lesson1);

            // Delete sync in the lesson2 list
            Lesson lesson2 = sync.getLesson2();
            List<Synchronization> list2 = lesson2.getSynchronizations2();
            list2.remove(sync);
            lesson2.setSynchronizations1(list2);
            lessonService.save(lesson2);

            // Delete sync in the Synchronization's table
            synchronizationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Synchronization> findById(SynchronizationId id) {
        return synchronizationRepository.findById(id);
    }

    public List<Synchronization> findAll() {
        return synchronizationRepository.findAll();
    }

    public List<Synchronization> findByLesson(Lesson lesson) {
        List<Synchronization> listSync1 = synchronizationRepository.findByLesson1(lesson);
        List<Synchronization> listSync2 = synchronizationRepository.findByLesson2(lesson);
        listSync1.addAll(listSync2);
        return listSync1;
    }

}
