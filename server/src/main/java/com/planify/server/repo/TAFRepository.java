package com.planify.server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planify.server.models.TAF;

@Repository
public interface TAFRepository extends JpaRepository<TAF, Long> {

    List<TAF> findAll();

    Optional<TAF> findById(Long id);

    List<TAF> findByName(String name);

    TAF save(TAF taf);

    void deleteById(TAF taf);

}
