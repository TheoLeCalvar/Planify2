package com.planify.server.repo;

import com.planify.server.models.Planning;
import com.planify.server.models.UE;
import com.planify.server.models.constraints.ConstraintsOfUE;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConstraintsOfUERepository extends JpaRepository<ConstraintsOfUE, ConstraintsOfUE.ConstraintsOfUEId> {

    List<ConstraintsOfUE> findAll();

    Optional<ConstraintsOfUE> findById(ConstraintsOfUE.ConstraintsOfUEId constraintsOfUEId);

    ConstraintsOfUE save(ConstraintsOfUE constraints);

    @Override
    void delete(ConstraintsOfUE entity);

    @Override
    void deleteById(ConstraintsOfUE.ConstraintsOfUEId constraintsOfUEId);

    List<ConstraintsOfUE> findByUe(UE ue);

    List<ConstraintsOfUE> findByPlanning(Planning planning);
}
