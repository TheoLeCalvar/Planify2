package com.planify.server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planify.server.models.Slot;
import com.planify.server.models.User;
import com.planify.server.models.UserUnavailability;
import com.planify.server.models.UserUnavailability.UserUnavailabilityId;

@Repository
public interface UserUnavailabilityRepository extends JpaRepository<UserUnavailability, UserUnavailabilityId> {

    List<UserUnavailability> findAll();

    Optional<UserUnavailability> findById(UserUnavailabilityId id);

    List<UserUnavailability> findByUser(User user);

    List<UserUnavailability> findBySlot(Slot slot);

    Optional<UserUnavailability> findBySlotAndUser(Slot slot, User user);

    boolean existsBySlot(Slot slot);

    boolean existsBySlotAndUser(Slot slot, User user);

    UserUnavailability save(UserUnavailability userUnavailability);

    void deleteById(UserUnavailability id);

}
