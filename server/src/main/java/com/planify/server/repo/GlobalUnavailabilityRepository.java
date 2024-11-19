package com.planify.server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planify.server.models.GlobalUnavailability;
import com.planify.server.models.Slot;

@Repository
public interface GlobalUnavailabilityRepository extends JpaRepository<GlobalUnavailability,Slot>{

    List<GlobalUnavailability> findAll();

    Optional<GlobalUnavailability> findById(Slot slot);

    List<GlobalUnavailability> findByStrict(Boolean bool);

    GlobalUnavailability save(GlobalUnavailability globalUnavailability);

    void deleteById(GlobalUnavailability globalUnavailability);
    
}
