package io.voitovich.yura.driverservice.service;

import io.voitovich.yura.driverservice.dto.request.DriverProfileRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfilePageRequest;
import io.voitovich.yura.driverservice.dto.response.DriverProfilePageResponse;

import java.util.UUID;

public interface DriverProfileService {
    DriverProfileRequest getProfileById(UUID uuid);
    DriverProfileRequest saveProfile(DriverProfileRequest profileDto);
    DriverProfileRequest updateProfile(DriverProfileRequest profileDto);
    DriverProfilePageResponse getProfilePage(DriverProfilePageRequest pageRequest);
    void deleteProfileById(UUID uuid);
}
