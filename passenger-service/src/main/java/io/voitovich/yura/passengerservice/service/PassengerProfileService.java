package io.voitovich.yura.passengerservice.service;

import io.voitovich.yura.passengerservice.dto.request.PassengerProfileRequest;

import java.util.UUID;

public interface PassengerProfileService {

    PassengerProfileRequest getProfileById(UUID uuid);

    PassengerProfileRequest updateProfile(PassengerProfileRequest profileDto);

    PassengerProfileRequest saveProfile(PassengerProfileRequest profileDto);

    void deleteProfile(UUID uuid);
}
