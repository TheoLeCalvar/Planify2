package com.planify.server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planify.server.models.Calendar;
import com.planify.server.models.TAF;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar,Long>{

    List<Calendar> findAll();

    Optional<Calendar> findById(Long id);

    List<Calendar> findByTaf(TAF taf);

    Calendar save(Calendar calendar);

    void deleteById(Calendar calendar);
    
}
