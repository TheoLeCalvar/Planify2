package com.planify.server.repo;

import com.planify.server.models.Antecedence;
import com.planify.server.models.Lesson;
import com.planify.server.models.Planning;
import com.planify.server.models.constraints.ConstraintSynchroniseWithTAF;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConstraintSynchroniseWithTAFRepository extends JpaRepository<ConstraintSynchroniseWithTAF, Long> {

    List<ConstraintSynchroniseWithTAF> findAll();

    Optional<ConstraintSynchroniseWithTAF> findById(Long id);

    ConstraintSynchroniseWithTAF save(ConstraintSynchroniseWithTAF constraint);

    void deleteById(Long id);

    List<ConstraintSynchroniseWithTAF> findByPlanning(Planning planning);

    @Override
    boolean existsById(Long aLong);

}
