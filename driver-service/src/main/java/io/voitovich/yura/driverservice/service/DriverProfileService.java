package io.voitovich.yura.driverservice.service;

import io.voitovich.yura.driverservice.dto.DriverProfileDto;

import java.util.UUID;

public interface DriverProfileService {
    DriverProfileDto getProfileById(UUID uuid);
    DriverProfileDto saveProfile(DriverProfileDto profileDto);
    DriverProfileDto updateProfile(DriverProfileDto profileDto);
    void deleteProfileById(UUID uuid);
}
