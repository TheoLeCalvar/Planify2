package com.planify.server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planify.server.models.Lesson;
import com.planify.server.models.Sequencing;
import com.planify.server.models.Sequencing.SequencingId;

@Repository
public interface SequencingRepository extends JpaRepository<Sequencing,SequencingId>{

    List<Sequencing> findAll();
    
    Optional<Sequencing> findById(SequencingId id);

    List<Sequencing> findByPreviousLesson(Lesson lesson);

    List<Sequencing> findByNextLesson(Lesson lesson);

    Sequencing save(Sequencing sequencing);

    void deleteById(SequencingId id);
    
}
