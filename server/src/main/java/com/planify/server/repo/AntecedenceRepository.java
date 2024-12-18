package com.planify.server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planify.server.models.Antecedence;
import com.planify.server.models.Antecedence.AntecedenceId;
import com.planify.server.models.Lesson;

@Repository
public interface AntecedenceRepository extends JpaRepository<Antecedence,AntecedenceId>{

    List<Antecedence> findAll();
    
    Optional<Antecedence> findById(AntecedenceId id);

    List<Antecedence> findByPreviousLesson(Lesson lesson);

    List<Antecedence> findByNextLesson(Lesson lesson);

    Antecedence save(Antecedence antecedence);

    void deleteById(AntecedenceId id);
    
}
