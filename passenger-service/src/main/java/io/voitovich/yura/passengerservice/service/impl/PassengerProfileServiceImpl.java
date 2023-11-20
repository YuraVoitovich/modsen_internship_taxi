package io.voitovich.yura.passengerservice.service.impl;

import io.voitovich.yura.passengerservice.dto.request.PassengerProfilePageRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerProfileUpdateRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerSaveProfileRequest;
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
        return INSTANCE.toProfileResponse(getIfPresent(uuid));
    }
    @Override
    public PassengerProfileResponse updateProfile(PassengerProfileUpdateRequest request) {
        log.info("Updating passenger profile: {}", request);
        PassengerProfile profile = getIfPresent(request.id());
        if (!profile.getPhoneNumber().equals(request.phoneNumber())) {
            checkPhoneNumberUnique(request.phoneNumber());
        }
        INSTANCE.updateEntityFromUpdateRequest(request, profile);
        profile = repository.save(profile);
        return INSTANCE.toProfileResponse(profile);

    }

    @Override
    public PassengerProfileResponse saveProfile(PassengerSaveProfileRequest profileRequest) {
        log.info("Save passenger profile: {}", profileRequest);
        checkPhoneNumberUnique(profileRequest.phoneNumber());

        PassengerProfile profile = INSTANCE.fromSaveRequestToEntity(profileRequest);
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
        PassengerProfile profile = getIfPresent(uuid);
        repository.deleteById(uuid);

    }

    private PassengerProfile getIfPresent(UUID uuid) {
        return repository.getPassengerProfileById(uuid)
                .orElseThrow(() -> new NoSuchRecordException(String
                        .format("Passenger profile with id: {%s} not found", uuid)));
    }

    private void checkPhoneNumberUnique(String phoneNumber) {
        if (repository.existsByPhoneNumber(phoneNumber)) {
            throw new NotUniquePhoneException(String
                    .format("Passenger profile with phone number: {%s} already exists",
                            phoneNumber));
        }
    }
}
