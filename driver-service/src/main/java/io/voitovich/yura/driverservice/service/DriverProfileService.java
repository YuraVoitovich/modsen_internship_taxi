package io.voitovich.yura.driverservice.service;

import io.voitovich.yura.driverservice.dto.DriverProfileDto;
import io.voitovich.yura.driverservice.dto.DriverProfilePageRequest;
import io.voitovich.yura.driverservice.dto.DriverProfilePageResponse;

import java.util.UUID;

public interface DriverProfileService {
    DriverProfileDto getProfileById(UUID uuid);
    DriverProfileDto saveProfile(DriverProfileDto profileDto);
    DriverProfileDto updateProfile(DriverProfileDto profileDto);
    DriverProfilePageResponse getProfilePage(DriverProfilePageRequest pageRequest);
    void deleteProfileById(UUID uuid);
}
