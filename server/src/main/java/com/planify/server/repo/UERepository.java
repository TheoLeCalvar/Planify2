package com.planify.server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planify.server.models.TAF;
import com.planify.server.models.UE;

@Repository
public interface UERepository extends JpaRepository<UE, Long> {

    List<UE> findAll();

    Optional<UE> findById(Long id);

    List<UE> findByTaf(TAF taf);

    UE save(UE ue);

    void deleteById(Long id);
}
