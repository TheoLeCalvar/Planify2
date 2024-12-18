package com.planify.server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planify.server.models.Lesson;
import com.planify.server.models.UE;

@Repository
public interface LessonRepository extends JpaRepository<Lesson,Long>{

    List<Lesson> findAll();

    Optional<Lesson> findById(Long id);

    List<Lesson> findByUe(UE ue);

    Lesson save(Lesson lesson);

    void deleteById(Lesson lesson);
    
}
