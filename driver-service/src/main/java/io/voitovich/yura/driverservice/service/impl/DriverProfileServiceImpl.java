package io.voitovich.yura.driverservice.service.impl;

import io.voitovich.yura.driverservice.dto.mapper.DriverProfileMapper;
import io.voitovich.yura.driverservice.dto.request.DriverProfilePageRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfileSaveRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfileUpdateRequest;
import io.voitovich.yura.driverservice.dto.response.DriverProfilePageResponse;
import io.voitovich.yura.driverservice.dto.response.DriverProfileResponse;
import io.voitovich.yura.driverservice.dto.response.DriverProfilesResponse;
import io.voitovich.yura.driverservice.entity.DriverProfile;
import io.voitovich.yura.driverservice.exception.NoSuchRecordException;
import io.voitovich.yura.driverservice.exception.NotUniquePhoneException;
import io.voitovich.yura.driverservice.model.RecalculateRatingModel;
import io.voitovich.yura.driverservice.repository.DriverProfileRepository;
import io.voitovich.yura.driverservice.service.DriverProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DriverProfileServiceImpl implements DriverProfileService {

    private final DriverProfileRepository repository;
    private final BigDecimal START_RATING = BigDecimal.valueOf(5);

    private final int INITIAL_PASSENGER_RATINGS_COUNT = 1;
    private final int ADDITIONAL_RATINGS_COUNT_ON_UPDATE = 1;

    private final int SCALE = 1;

    private final DriverProfileMapper mapper;

    private final String NO_SUCH_RECORD_EXCEPTION_MESSAGE = "Driver profile with id: {%s} not found";
    private final String NOT_UNIQUE_PHONE_EXCEPTION_MESSAGE = "Driver profile with phone number: {%s} already exists";



    @Override
    public DriverProfileResponse getProfileById(UUID uuid) {
        log.info("Getting driver profile by id: {}", uuid);
        return mapper
                .toProfileResponse(getIfPresent(uuid));
    }

    @Override
    public DriverProfileResponse saveProfile(DriverProfileSaveRequest request) {
        log.info("Saving driver profile: {}", request);
        checkPhoneNumberUnique(request.phoneNumber());
        DriverProfile profile = mapper.toProfileFromSaveRequest(request);
        profile.setRating(START_RATING);
        return mapper.toProfileResponse(repository.save(profile));
    }

    @Override
    public DriverProfileResponse updateProfile(DriverProfileUpdateRequest request) {
        log.info("Updating driver profile: {}", request);
        DriverProfile profile = getIfPresent(request.id());
        if (!profile.getPhoneNumber().equals(request.phoneNumber())) {
            checkPhoneNumberUnique(request.phoneNumber());
        }
        mapper.updateProfileEntity(request, profile);
        profile = repository.save(profile);
        return mapper.toProfileResponse(profile);
    }

    @Override
    public DriverProfilePageResponse getProfilePage(DriverProfilePageRequest pageRequest) {
        Page<DriverProfile> page = repository.findAll(PageRequest
                .of(pageRequest.pageNumber() - 1,
                        pageRequest.pageSize(),
                        Sort.by(pageRequest.orderBy())));
        return DriverProfilePageResponse
                .builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageNumber(pageRequest.pageNumber())
                .profiles(page.getContent().stream().map(mapper::toProfileResponse).toList())
                .build();
    }

    @Override
    public void deleteProfileById(UUID uuid) {
        log.info("Deleting driver profile by id: {}", uuid);
        DriverProfile profile = getIfPresent(uuid);
        repository.deleteById(uuid);
    }

    @Override
    public DriverProfile getPassengerProfileAndRecalculateRating(RecalculateRatingModel model) {
        log.info("Recalculating driver rating with model: {}", model);
        DriverProfile profile = getIfPresent(model.passengerProfileId());
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
    public DriverProfilesResponse getByIds(List<UUID> uuids) {
        log.info("Getting profiles by ids: {}", uuids);
        var profiles = repository.findAllById(uuids);
        return DriverProfilesResponse.builder()
                .profiles(profiles
                        .stream()
                        .map(mapper::toProfileResponse)
                        .toList())
                .build();
    }

    private DriverProfile getIfPresent(UUID uuid) {
        return repository.getDriverProfilesById(uuid)
                .orElseThrow(() -> new NoSuchRecordException(
                        String.format(NO_SUCH_RECORD_EXCEPTION_MESSAGE, uuid)));
    }

    private void checkPhoneNumberUnique(String phoneNumber) {
        if (repository.existsDriverProfileByPhoneNumber(phoneNumber)) {
            throw new NotUniquePhoneException(String
                    .format(NOT_UNIQUE_PHONE_EXCEPTION_MESSAGE, phoneNumber));
        }
    }
}
