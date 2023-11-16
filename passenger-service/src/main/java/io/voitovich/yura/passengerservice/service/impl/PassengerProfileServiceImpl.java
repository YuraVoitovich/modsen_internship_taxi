package io.voitovich.yura.passengerservice.service.impl;

import io.voitovich.yura.passengerservice.dto.request.PassengerProfilePageRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerProfileRequest;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfilePageResponse;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfileResponse;
import io.voitovich.yura.passengerservice.entity.PassengerProfile;
import io.voitovich.yura.passengerservice.exception.NoSuchRecordException;
import io.voitovich.yura.passengerservice.exception.NotUniquePhoneException;
import io.voitovich.yura.passengerservice.repository.PassengerProfileRepository;
import io.voitovich.yura.passengerservice.service.PassengerProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static io.voitovich.yura.passengerservice.dto.mapper.PassengerProfileMapper.INSTANCE;

@Service
@Slf4j
public class PassengerProfileServiceImpl implements PassengerProfileService {

    private final PassengerProfileRepository repository;

    private final BigDecimal START_RATING = BigDecimal.valueOf(5);

    public PassengerProfileServiceImpl(PassengerProfileRepository repository) {
        this.repository = repository;
    }


    @Override
    public PassengerProfileResponse getProfileById(UUID uuid) {
        log.info("Getting passenger profile by id: {}", uuid);
        return INSTANCE.toProfileResponse(repository
                        .getPassengerProfileById(uuid)
                        .orElseThrow(() -> new NoSuchRecordException(String
                                .format("Passenger profile with id: {%s} not found", uuid))));
    }

    @Override
    public PassengerProfileResponse updateProfile(PassengerProfileRequest profileRequest) {
        log.info("Updating passenger profile: {}", profileRequest);
        if (repository.existsByPhoneNumber(profileRequest.phoneNumber())) {
            throw new NotUniquePhoneException(String
                    .format("Passenger profile with phone number: {%s} already exists", profileRequest.phoneNumber()));
        }
        PassengerProfile profile = INSTANCE.toEntity(profileRequest);
        profile = repository.save(profile);
        return INSTANCE.toProfileResponse(profile);

    }

    @Override
    public PassengerProfileResponse saveProfile(PassengerProfileRequest profileRequest) {
        log.info("Save passenger profile: {}", profileRequest);
        if (repository.existsByPhoneNumber(profileRequest.phoneNumber())) {
            throw new NotUniquePhoneException(String
                    .format("Passenger profile with phone number: {%s} already exists", profileRequest.phoneNumber()));
        }

        PassengerProfile profile = INSTANCE.toEntity(profileRequest);
        profile.setRating(START_RATING);
        profile = repository.save(profile);
        return INSTANCE.toProfileResponse(profile);
    }

    @Override
    public PassengerProfilePageResponse getProfilePage(PassengerProfilePageRequest pageRequest) {
        Page<PassengerProfile> page = repository.findAll(PageRequest
                .of(pageRequest.pageNumber() - 1,
                        pageRequest.pageSize(),
                        Sort.by(pageRequest.orderBy())));
        return PassengerProfilePageResponse
                .builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageNumber(pageRequest.pageNumber())
                .profiles(page.getContent().stream().map(INSTANCE::toProfileResponse).toList())
                .build();
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
