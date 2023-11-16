package io.voitovich.yura.passengerservice.repository;

import io.voitovich.yura.passengerservice.entity.PassengerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PassengerProfileRepository extends JpaRepository<PassengerProfile, UUID> {
    Optional<PassengerProfile> getPassengerProfileById(UUID uuid);

    boolean existsByPhoneNumber(String phoneNumber);

}