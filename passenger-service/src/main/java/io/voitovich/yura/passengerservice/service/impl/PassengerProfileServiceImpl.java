package io.voitovich.yura.passengerservice.service.impl;

import io.voitovich.yura.passengerservice.dto.PassengerProfileDto;
import io.voitovich.yura.passengerservice.dto.mapper.PassengerProfileMapper;
import io.voitovich.yura.passengerservice.exception.NoSuchRecordException;
import io.voitovich.yura.passengerservice.repository.PassengerProfileRepository;
import io.voitovich.yura.passengerservice.service.PassengerProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
        return PassengerProfileMapper.INSTANCE
                .toDto(repository
                        .getPassengerProfileById(uuid)
                        .orElseThrow(() -> new NoSuchRecordException(String
                                .format("Passenger profile with id: {%s} not found", uuid))));
    }

    @Override
    public void updateProfile(PassengerProfileDto profileDto) {
        log.info("Updating passenger profile: {}", profileDto);
        repository.save(PassengerProfileMapper.INSTANCE.toEntity(profileDto));
    }

    @Override
    public void saveProfile(PassengerProfileDto profileDto) {
        log.info("Save passenger profile: {}", profileDto);
        repository.save(PassengerProfileMapper.INSTANCE.toEntity(profileDto));
    }

    @Override
    public void deleteProfile(UUID uuid) {
        log.info("Deleting passenger profile by id: {}", uuid);
        repository.deleteById(uuid);
    }
}
