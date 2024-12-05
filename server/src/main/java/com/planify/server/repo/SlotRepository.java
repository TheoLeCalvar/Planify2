package com.planify.server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.planify.server.models.Calendar;
import com.planify.server.models.Day;
import com.planify.server.models.Slot;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {

    List<Slot> findAll();

    Optional<Slot> findById(Long id);

    List<Slot> findByDay(Day day);

    List<Slot> findByCalendar(Calendar calendar);

    @Query("SELECT s FROM Slot s " +
            "JOIN Day d ON s.day.id = d.id " +
            "JOIN Week w ON d.week.id = w.id " +
            "WHERE s.calendar.id = :idCalendrier " +
            "ORDER BY w.year ASC, w.number ASC, d.number ASC, s.number ASC")
    List<Slot> findSlotByIdCalendrierOrdered(@Param("idCalendrier") Long idCalendrier);

    List<Slot> findByDayAndCalendar(Day day, Calendar calendar);

    Slot save(Slot slot);

    void deleteById(Slot slot);

}
