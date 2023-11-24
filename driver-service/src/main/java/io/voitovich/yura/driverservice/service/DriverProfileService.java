package io.voitovich.yura.driverservice.service;

import io.voitovich.yura.driverservice.dto.request.DriverProfilePageRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfileSaveRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfileUpdateRequest;
import io.voitovich.yura.driverservice.dto.response.DriverProfilePageResponse;
import io.voitovich.yura.driverservice.dto.response.DriverProfileResponse;

import java.util.UUID;

public interface DriverProfileService {
    DriverProfileResponse getProfileById(UUID uuid);
    DriverProfileResponse saveProfile(DriverProfileSaveRequest request);
    DriverProfileResponse updateProfile(DriverProfileUpdateRequest profileDto);
    DriverProfilePageResponse getProfilePage(DriverProfilePageRequest pageRequest);
    void deleteProfileById(UUID uuid);
}
