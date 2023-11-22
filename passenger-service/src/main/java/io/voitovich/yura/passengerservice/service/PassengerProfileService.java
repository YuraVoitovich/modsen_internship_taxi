package io.voitovich.yura.passengerservice.service;

import io.voitovich.yura.passengerservice.dto.request.PassengerProfilePageRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerProfileUpdateRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerSaveProfileRequest;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfilePageResponse;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfileResponse;
import io.voitovich.yura.passengerservice.entity.PassengerProfile;
import io.voitovich.yura.passengerservice.model.RecalculateRatingModel;

import java.util.UUID;

public interface PassengerProfileService {

    PassengerProfileResponse getProfileById(UUID uuid);

    PassengerProfileResponse updateProfile(PassengerProfileUpdateRequest profileDto);

    PassengerProfileResponse saveProfile(PassengerSaveProfileRequest profileDto);

    PassengerProfilePageResponse getProfilePage(PassengerProfilePageRequest pageRequest);

    void deleteProfile(UUID uuid);

    PassengerProfile getIfPresent(UUID id);

    PassengerProfile getPassengerProfileAndRecalculateRating(RecalculateRatingModel model);
}
