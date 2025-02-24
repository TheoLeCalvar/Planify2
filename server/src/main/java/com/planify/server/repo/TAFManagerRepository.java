package com.planify.server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planify.server.models.TAF;
import com.planify.server.models.TAFManager;
import com.planify.server.models.TAFManager.TAFManagerId;
import com.planify.server.models.User;

@Repository
public interface TAFManagerRepository extends JpaRepository<TAFManager, TAFManagerId> {

    List<TAFManager> findAll();

    Optional<TAFManager> findById(TAFManagerId id);

    List<TAFManager> findByUser(User user);

    List<TAFManager> findByTaf(TAF taf);

    boolean existsByUser(User user);

    TAFManager save(TAFManager tafManager);

    void deleteById(TAFManagerId id);
}
