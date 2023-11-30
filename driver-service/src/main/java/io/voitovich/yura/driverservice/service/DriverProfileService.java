package io.voitovich.yura.driverservice.service;

import io.voitovich.yura.driverservice.dto.request.DriverProfilePageRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfileSaveRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfileUpdateRequest;
import io.voitovich.yura.driverservice.dto.response.DriverProfilePageResponse;
import io.voitovich.yura.driverservice.dto.response.DriverProfileResponse;
import io.voitovich.yura.driverservice.entity.DriverProfile;
import io.voitovich.yura.driverservice.model.RecalculateRatingModel;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
public interface DriverProfileService {
    DriverProfileResponse getProfileById(UUID uuid);
    DriverProfileResponse saveProfile(DriverProfileSaveRequest request);
    DriverProfileResponse updateProfile(DriverProfileUpdateRequest profileDto);
    DriverProfilePageResponse getProfilePage(@Valid DriverProfilePageRequest pageRequest);
    void deleteProfileById(UUID uuid);
    DriverProfile getPassengerProfileAndRecalculateRating(RecalculateRatingModel model);
}
