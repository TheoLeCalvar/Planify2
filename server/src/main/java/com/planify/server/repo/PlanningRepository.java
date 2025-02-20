package com.planify.server.repo;

import com.planify.server.models.Block;
import com.planify.server.models.Calendar;
import com.planify.server.models.Planning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanningRepository extends JpaRepository<Planning, Long> {

    List<Planning> findAll();

    Optional<Planning> findById(Long id);

    List<Planning> findByCalendar(Calendar calendar);

    Planning save(Planning planning);

    void deleteById(Long id);

    boolean existsById(Long id);
}
