package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.Lesson;
import com.planify.server.models.Slot;
import com.planify.server.models.Synchronization;
import com.planify.server.models.Synchronization.SynchronizationId;
import com.planify.server.repo.SynchronizationRepository;

@Service
public class SynchronizationService {

    @Autowired
    private SynchronizationRepository synchronizationRepository;

    public Synchronization addSynchronization(Lesson lesson1, Lesson lesson2) {
        Synchronization synchronization = new Synchronization(lesson1, lesson2);

        synchronizationRepository.save(synchronization);
        return synchronization;
    }

    public void save(Synchronization synchronization) {
        synchronizationRepository.save(synchronization);
    }

    public boolean deleteSynchronization(SynchronizationId id) {
        if (synchronizationRepository.existsById(id)) {
            synchronizationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Synchronization> findById(SynchronizationId id) {
        return synchronizationRepository.findById(id);
    }

    public List<Synchronization> findByLesson(Lesson lesson) {
        List<Synchronization> listSync1 = synchronizationRepository.findByLesson1(lesson);
        List<Synchronization> listSync2 = synchronizationRepository.findByLesson2(lesson);
        listSync1.addAll(listSync2);
        return listSync1;
    }

}
