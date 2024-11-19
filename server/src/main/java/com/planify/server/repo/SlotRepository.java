package com.planify.server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planify.server.models.Calendar;
import com.planify.server.models.Day;
import com.planify.server.models.Slot;

@Repository
public interface SlotRepository extends JpaRepository<Slot,Long>{

    List<Slot> findAll();

    Optional<Slot> findById(Long id);

    List<Slot> findByDay(Day day);

    List<Slot> findByCalendar(Calendar calendar);

    Slot save(Slot slot);

    void deleteById(Slot slot);
    
}
