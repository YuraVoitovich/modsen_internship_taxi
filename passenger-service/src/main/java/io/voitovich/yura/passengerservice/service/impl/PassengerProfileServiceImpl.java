package io.voitovich.yura.passengerservice.service.impl;

import io.voitovich.yura.passengerservice.dto.mapper.PassengerProfileMapper;
import io.voitovich.yura.passengerservice.dto.request.PassengerProfilePageRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerProfileUpdateRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerSaveProfileRequest;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfilePageResponse;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfileResponse;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfilesResponse;
import io.voitovich.yura.passengerservice.entity.PassengerProfile;
import io.voitovich.yura.passengerservice.exception.NoSuchRecordException;
import io.voitovich.yura.passengerservice.exception.NotUniquePhoneException;
import io.voitovich.yura.passengerservice.model.RecalculateRatingModel;
import io.voitovich.yura.passengerservice.repository.PassengerProfileRepository;
import io.voitovich.yura.passengerservice.service.PassengerProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PassengerProfileServiceImpl implements PassengerProfileService {

    private final int INITIAL_PASSENGER_RATINGS_COUNT = 1;
    private final int ADDITIONAL_RATINGS_COUNT_ON_UPDATE = 1;

    private final int SCALE = 1;

    private final PassengerProfileMapper mapper;
    private final String NO_SUCH_RECORD_EXCEPTION_MESSAGE = "Passenger profile with id: {%s} not found";
    private final String NOT_UNIQUE_PHONE_EXCEPTION_MESSAGE = "Passenger profile with phone number: {%s} already exists";
    private final PassengerProfileRepository repository;

    private final BigDecimal START_RATING = BigDecimal.valueOf(5);


    @Override
    public PassengerProfileResponse getProfileById(UUID uuid) {
        log.info("Getting passenger profile by id: {}", uuid);
        return mapper.toProfileResponse(getIfPresent(uuid));
    }
    @Override
    public PassengerProfileResponse updateProfile(PassengerProfileUpdateRequest request) {
        log.info("Updating passenger profile: {}", request);
        PassengerProfile profile = getIfPresent(request.id());
        if (!profile.getPhoneNumber().equals(request.phoneNumber())) {
            checkPhoneNumberUnique(request.phoneNumber());
        }
        mapper.updateEntityFromUpdateRequest(request, profile);
        profile = repository.save(profile);
        return mapper.toProfileResponse(profile);

    }

    @Override
    public PassengerProfileResponse saveProfile(PassengerSaveProfileRequest profileRequest) {
        log.info("Save passenger profile: {}", profileRequest);
        checkPhoneNumberUnique(profileRequest.phoneNumber());

        PassengerProfile profile = mapper.fromSaveRequestToEntity(profileRequest);
        profile.setRating(START_RATING);
        profile = repository.save(profile);
        return mapper.toProfileResponse(profile);
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
                .profiles(page.getContent().stream().map(mapper::toProfileResponse).toList())
                .build();
    }

    @Override
    public void deleteProfile(UUID uuid) {
        log.info("Deleting passenger profile by id: {}", uuid);
        PassengerProfile profile = getIfPresent(uuid);
        repository.deleteById(uuid);

    }

    @Override
    public PassengerProfile getIfPresent(UUID uuid) {
        return repository.getPassengerProfileById(uuid)
                .orElseThrow(() -> new NoSuchRecordException(String
                        .format(NO_SUCH_RECORD_EXCEPTION_MESSAGE, uuid)));
    }

    private void checkPhoneNumberUnique(String phoneNumber) {
        if (repository.existsByPhoneNumber(phoneNumber)) {
            throw new NotUniquePhoneException(String
                    .format(NOT_UNIQUE_PHONE_EXCEPTION_MESSAGE,
                            phoneNumber));
        }
    }

    @Override
    public PassengerProfile getPassengerProfileAndRecalculateRating(RecalculateRatingModel model) {
        log.info("Recalculating passenger rating with model: {}", model);
        PassengerProfile profile = getIfPresent(model.passengerProfileId());
        BigDecimal currentRating = profile.getRating();
        BigDecimal ratingToAdd = model.newRating();
        long ratingCount = model.ratingsCount() + INITIAL_PASSENGER_RATINGS_COUNT;
        BigDecimal newRating = currentRating
                .multiply(BigDecimal.valueOf(ratingCount))
                .add(ratingToAdd)
                .divide(BigDecimal
                        .valueOf(ratingCount + ADDITIONAL_RATINGS_COUNT_ON_UPDATE),
                        SCALE,
                        RoundingMode.CEILING);
        profile.setRating(newRating);
        return repository.save(profile);
    }

    @Override
    public PassengerProfilesResponse getByIds(List<UUID> uuids) {
        log.info("Getting profiles by ids: {}", uuids);
        var profiles = repository.findAllById(uuids);
        return PassengerProfilesResponse.builder()
                .profiles(profiles
                        .stream()
                        .map(mapper::toProfileResponse)
                        .toList())
                .build();
    }
}
