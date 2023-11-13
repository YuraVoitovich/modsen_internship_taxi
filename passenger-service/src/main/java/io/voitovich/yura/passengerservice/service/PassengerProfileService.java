package io.voitovich.yura.passengerservice.service;

import io.voitovich.yura.passengerservice.dto.request.PassengerProfileRequest;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfileResponse;

import java.util.UUID;

public interface PassengerProfileService {

    PassengerProfileResponse getProfileById(UUID uuid);

    PassengerProfileResponse updateProfile(PassengerProfileRequest profileDto);

    PassengerProfileResponse saveProfile(PassengerProfileRequest profileDto);

    void deleteProfile(UUID uuid);
}
