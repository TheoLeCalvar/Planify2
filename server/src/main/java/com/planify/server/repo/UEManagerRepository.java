package com.planify.server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planify.server.models.UE;
import com.planify.server.models.UEManager;
import com.planify.server.models.UEManager.UEManagerId;
import com.planify.server.models.User;

@Repository
public interface UEManagerRepository extends JpaRepository<UEManager, UEManagerId> {

    List<UEManager> findAll();

    Optional<UEManager> findById(UEManagerId id);

    List<UEManager> findByUser(User user);

    List<UEManager> findByUe(UE ue);

    UEManager save(UEManager ueManager);

    void deleteById(UEManagerId id);

}
