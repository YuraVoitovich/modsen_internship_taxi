package io.voitovich.yura.driverservice.repository;

import io.voitovich.yura.driverservice.entity.DriverProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, UUID> {
    Optional<DriverProfile> getDriverProfilesById(UUID id);

    boolean existsDriverProfileByPhoneNumber(String number);
}
