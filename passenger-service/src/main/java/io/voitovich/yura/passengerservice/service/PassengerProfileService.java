package io.voitovich.yura.passengerservice.service;

import io.voitovich.yura.passengerservice.dto.PassengerProfileDto;
import io.voitovich.yura.passengerservice.entity.PassengerProfile;

import java.util.UUID;

public interface PassengerProfileService {

    PassengerProfileDto getProfileById(UUID uuid);

    void updateProfile(PassengerProfileDto profileDto);

    void saveProfile(PassengerProfileDto profileDto);

    void deleteProfile(UUID uuid);
}
