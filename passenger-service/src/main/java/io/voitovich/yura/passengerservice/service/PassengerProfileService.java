package io.voitovich.yura.passengerservice.service;

import io.voitovich.yura.passengerservice.dto.PassengerProfileDto;

import java.util.UUID;

public interface PassengerProfileService {

    PassengerProfileDto getProfileById(UUID uuid);

    PassengerProfileDto updateProfile(PassengerProfileDto profileDto);

    PassengerProfileDto saveProfile(PassengerProfileDto profileDto);

    void deleteProfile(UUID uuid);
}
