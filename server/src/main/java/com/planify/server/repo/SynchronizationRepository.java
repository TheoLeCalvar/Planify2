package com.planify.server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planify.server.models.Lesson;
import com.planify.server.models.Synchronization;
import com.planify.server.models.Synchronization.SynchronizationId;

@Repository
public interface SynchronizationRepository extends JpaRepository<Synchronization,SynchronizationId> {
    
    List<Synchronization> findAll();
    
    Optional<Synchronization> findById(SynchronizationId id);

    List<Synchronization> findByLesson1(Lesson lesson1);

    List<Synchronization> findByLesson2(Lesson lesson2);

    Synchronization save(Synchronization synchronization);

    void deleteById(SynchronizationId id);
}
