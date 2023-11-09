package io.voitovich.yura.passengerservice.service.impl;

import io.voitovich.yura.passengerservice.dto.PassengerProfileDto;
import io.voitovich.yura.passengerservice.entity.PassengerProfile;
import io.voitovich.yura.passengerservice.exception.NoSuchRecordException;
import io.voitovich.yura.passengerservice.exception.NotUniquePhoneException;
import io.voitovich.yura.passengerservice.repository.PassengerProfileRepository;
import io.voitovich.yura.passengerservice.service.PassengerProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static io.voitovich.yura.passengerservice.dto.mapper.PassengerProfileMapper.INSTANCE;

@Service
@Slf4j
public class PassengerProfileServiceImpl implements PassengerProfileService {

    private final PassengerProfileRepository repository;

    public PassengerProfileServiceImpl(PassengerProfileRepository repository) {
        this.repository = repository;
    }


    @Override
    public PassengerProfileDto getProfileById(UUID uuid) {
        log.info("Getting passenger profile by id: {}", uuid);
        return INSTANCE
                .toDto(repository
                        .getPassengerProfileById(uuid)
                        .orElseThrow(() -> new NoSuchRecordException(String
                                .format("Passenger profile with id: {%s} not found", uuid))));
    }

    @Override
    public void updateProfile(PassengerProfileDto profileDto) {
        log.info("Updating passenger profile: {}", profileDto);
        if (repository.existsByPhoneNumber(profileDto.getPhoneNumber())) {
            throw new NotUniquePhoneException(String
                    .format("Passenger profile with phone number: {%s} already exists", profileDto.getPhoneNumber()));
        }
        repository.save(INSTANCE.toEntity(profileDto));
    }

    @Override
    public PassengerProfileDto saveProfile(PassengerProfileDto profileDto) {
        log.info("Save passenger profile: {}", profileDto);
        if (repository.existsByPhoneNumber(profileDto.getPhoneNumber())) {
            throw new NotUniquePhoneException(String
                    .format("Passenger profile with phone number: {%s} already exists", profileDto.getPhoneNumber()));
        }
        return INSTANCE.toDto(repository
                .save(INSTANCE.toEntity(profileDto)));
    }

    @Override
    public void deleteProfile(UUID uuid) {
        log.info("Deleting passenger profile by id: {}", uuid);
        Optional<PassengerProfile> profile = repository.getPassengerProfileById(uuid);
        if (profile.isPresent()) {
            repository.deleteById(uuid);
        } else {
            throw new NoSuchRecordException(String.format("Passenger profile with id: {%s} not found", uuid));
        }

    }
}
