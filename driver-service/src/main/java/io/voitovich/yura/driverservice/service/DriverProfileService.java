package io.voitovich.yura.driverservice.service;

import io.voitovich.yura.driverservice.dto.request.DriverProfilePageRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfileRequest;
import io.voitovich.yura.driverservice.dto.response.DriverProfilePageResponse;
import io.voitovich.yura.driverservice.dto.response.DriverProfileResponse;

import java.util.UUID;

public interface DriverProfileService {
    DriverProfileResponse getProfileById(UUID uuid);
    DriverProfileResponse saveProfile(DriverProfileRequest profileDto);
    DriverProfileResponse updateProfile(DriverProfileRequest profileDto);
    DriverProfilePageResponse getProfilePage(DriverProfilePageRequest pageRequest);
    void deleteProfileById(UUID uuid);
}
