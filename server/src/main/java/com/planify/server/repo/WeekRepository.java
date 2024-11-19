package com.planify.server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planify.server.models.Week;

@Repository
public interface WeekRepository extends JpaRepository<Week, Long> {

    List<Week> findAll();

    Optional<Week> findById(Long id);

    List<Week> findByYear(Integer year);

    List<Week> findByNumber(int n);

    Week save(Week week);

    void deleteById(Long id);

}