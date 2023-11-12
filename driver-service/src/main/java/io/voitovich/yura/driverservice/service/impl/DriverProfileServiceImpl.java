package io.voitovich.yura.driverservice.service.impl;

import io.voitovich.yura.driverservice.dto.request.DriverProfileRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfilePageRequest;
import io.voitovich.yura.driverservice.dto.response.DriverProfilePageResponse;
import io.voitovich.yura.driverservice.dto.response.DriverProfileResponse;
import io.voitovich.yura.driverservice.entity.DriverProfile;
import io.voitovich.yura.driverservice.exception.NoSuchRecordException;
import io.voitovich.yura.driverservice.exception.NotUniquePhoneException;
import io.voitovich.yura.driverservice.repository.DriverProfileRepository;
import io.voitovich.yura.driverservice.service.DriverProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static io.voitovich.yura.driverservice.dto.mapper.DriverProfileMapper.INSTANCE;

@Service
@Slf4j
public class DriverProfileServiceImpl implements DriverProfileService {

    private final DriverProfileRepository repository;

    public DriverProfileServiceImpl(DriverProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public DriverProfileResponse getProfileById(UUID uuid) {
        log.info("Getting driver profile by id: {}", uuid);
        return INSTANCE
                .toProfileResponse(repository
                        .getDriverProfilesById(uuid)
                        .orElseThrow(() -> new NoSuchRecordException(String
                                .format("Driver profile with id: {%s} not found", uuid))));
    }

    @Override
    public DriverProfileResponse saveProfile(DriverProfileRequest profileDto) {
        log.info("Saving driver profile: {}", profileDto);
        if (repository.existsDriverProfileByPhoneNumber(profileDto.phoneNumber())) {
            throw new NotUniquePhoneException(String
                    .format("Driver profile with phone number: {%s} already exists", profileDto.phoneNumber()));
        }
        DriverProfile profile = INSTANCE.toProfileEntity(profileDto);
        profile.setRating(BigDecimal.valueOf(5));
        return INSTANCE.toProfileResponse(repository.save(profile));
    }

    @Override
    public DriverProfileResponse updateProfile(DriverProfileRequest profileDto) {
        log.info("Updating driver profile: {}", profileDto);
        if (repository.existsDriverProfileByPhoneNumber(profileDto.phoneNumber())) {
            throw new NotUniquePhoneException(String
                    .format("Driver profile with phone number: {%s} already exists", profileDto.phoneNumber()));
        }
        return INSTANCE.toProfileResponse(repository.save(INSTANCE.toProfileEntity(profileDto)));
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
                .profiles(page.getContent().stream().map(INSTANCE::toProfileRequest).toList())
                .build();
    }

    @Override
    public void deleteProfileById(UUID uuid) {
        log.info("Deleting driver profile by id: {}", uuid);
        Optional<DriverProfile> profile = repository.getDriverProfilesById(uuid);
        if (profile.isPresent()) {
            repository.deleteById(uuid);
        } else {
            throw new NoSuchRecordException(String.format("Driver profile with id: {%s} not found", uuid));
        }
    }
}
