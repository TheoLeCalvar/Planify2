package com.planify.server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planify.server.models.Day;
import com.planify.server.models.Week;

@Repository
public interface DayRepository extends JpaRepository<Day,Long>{

    List<Day> findAll();

    Optional<Day> findById(Long id);

    List<Day> findByWeek(Week week);

    Day save(Day day);

    void deleteById(Day day);
    
}
