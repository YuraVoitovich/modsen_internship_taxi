package io.voitovich.yura.driverservice.unit;

import io.voitovich.yura.driverservice.dto.mapper.DriverProfileMapper;
import io.voitovich.yura.driverservice.dto.request.DriverProfilePageRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfileSaveRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfileUpdateRequest;
import io.voitovich.yura.driverservice.dto.response.DriverProfilePageResponse;
import io.voitovich.yura.driverservice.dto.response.DriverProfileResponse;
import io.voitovich.yura.driverservice.entity.DriverProfile;
import io.voitovich.yura.driverservice.exception.NoSuchRecordException;
import io.voitovich.yura.driverservice.exception.NotUniquePhoneException;
import io.voitovich.yura.driverservice.repository.DriverProfileRepository;
import io.voitovich.yura.driverservice.service.impl.DriverProfileServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.voitovich.yura.driverservice.unit.util.UnitTestsUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverProfileServiceTest {


    @Mock
    private DriverProfileRepository repository;
    @InjectMocks
    private DriverProfileServiceImpl service;

    @Test
    public void deleteDriverProfileById_driverProfileNotFound_throwNoSuchRecordException () {
        UUID uuid = UUID.randomUUID();
        assertThrows(NoSuchRecordException.class, () -> service.deleteProfileById(uuid));
        verify(repository, times(1)).getDriverProfilesById(uuid);
    }

    @Test
    public void deleteDriverProfile_driverWasFound_shouldDeleteDriverProfile () {
        UUID uuid = UUID.randomUUID();
        DriverProfile profile = createDefaultDriverProfileWithId(uuid);
        doReturn(Optional.of(profile)).when(repository)
                .getDriverProfilesById(uuid);


        service.deleteProfileById(uuid);


        verify(repository, times(1)).getDriverProfilesById(uuid);
        verify(repository, times(1)).deleteById(uuid);
    }

    @Test
    public void saveDriverProfile_correctDriverProfile_shouldSaveDriverProfile () {
        UUID uuid = UUID.randomUUID();
        DriverProfileSaveRequest request = createDefaultDriverProfileSaveRequest();
        DriverProfile profile = DriverProfileMapper.INSTANCE.toProfileFromSaveRequest(request);
        profile.setId(uuid);
        profile.setRating(BigDecimal.valueOf(5));
        doReturn(false).when(repository).existsDriverProfileByPhoneNumber(any());
        doReturn(profile).when(repository).save(any(DriverProfile.class));

        DriverProfileResponse expected = DriverProfileResponse
                .builder()
                .id(uuid)
                .surname(request.surname())
                .name(request.name())
                .rating(BigDecimal.valueOf(5))
                .phoneNumber(request.phoneNumber())
                .experience(3)
                .build();


        DriverProfileResponse savedProfile = service.saveProfile(request);


        assertEquals(expected, savedProfile);
        verify(repository, times(1)).existsDriverProfileByPhoneNumber(any());
        verify(repository, times(1)).save(any());

    }

    @Test
    public void saveDriverProfile_phoneNumberExists_shouldThrowNotUniquePhoneException () {
        UUID uuid = UUID.randomUUID();
        DriverProfileSaveRequest request = createDefaultDriverProfileSaveRequest();
        DriverProfile profile = DriverProfileMapper.INSTANCE.toProfileFromSaveRequest(request);
        profile.setId(uuid);
        profile.setRating(BigDecimal.valueOf(5));
        doReturn(true).when(repository).existsDriverProfileByPhoneNumber(any());


        assertThrows(NotUniquePhoneException.class, () -> service.saveProfile(request));



        verify(repository, times(1)).existsDriverProfileByPhoneNumber(any());

    }

    @Test
    public void updateDriverProfile_phoneNumberExists_shouldThrowNotUniquePhoneException () {
        UUID uuid = UUID.randomUUID();

        DriverProfile profile = createDefaultDriverProfileWithId(uuid);
        profile.setId(uuid);
        profile.setRating(BigDecimal.valueOf(5));
        profile.setPhoneNumber("+375295432550");
        DriverProfileUpdateRequest request = createDefaultDriverProfileUpdateRequest(uuid);

        doReturn(Optional.of(profile)).when(repository).getDriverProfilesById(uuid);

        doReturn(true).when(repository).existsDriverProfileByPhoneNumber(any());



        assertThrows(NotUniquePhoneException.class, () -> service.updateProfile(request));



        verify(repository, times(1)).getDriverProfilesById(any());

    }

    @Test
    public void updateDriverProfile_DriverProfileNotExists_shouldThrowNoSuchRecordException () {
        UUID uuid = UUID.randomUUID();

        DriverProfile profile = createDefaultDriverProfileWithId(uuid);
        profile.setId(uuid);
        profile.setRating(BigDecimal.valueOf(5));
        DriverProfileUpdateRequest request = createDefaultDriverProfileUpdateRequest(uuid);


        assertThrows(NoSuchRecordException.class, () -> service.updateProfile(request));



        verify(repository).getDriverProfilesById(uuid);

    }


    @Test
    public void updateDriverProfile_correctDriverProfile_shouldUpdateDriverProfile () {
        UUID uuid = UUID.randomUUID();

        DriverProfile profile = createDefaultDriverProfileWithId(uuid);
        profile.setId(uuid);
        profile.setRating(BigDecimal.valueOf(5));

        DriverProfileUpdateRequest request = createDefaultDriverProfileUpdateRequest(uuid);

        DriverProfileResponse expected = DriverProfileResponse
                .builder()
                .id(uuid)
                .surname(request.surname())
                .name(request.name())
                .rating(BigDecimal.valueOf(5))
                .phoneNumber(request.phoneNumber())
                .experience(4)
                .build();

        doReturn(Optional.of(profile)).when(repository).getDriverProfilesById(uuid);
        doReturn(profile).when(repository).save(any(DriverProfile.class));



        DriverProfileResponse updateProfile = service.updateProfile(request);

        assertEquals(expected, updateProfile);
        verify(repository, times(1)).save(any());

    }


    @Test
    public void getDriverProfileById_DriverProfileExists_shouldReturnDriverProfile() {

        UUID uuid = UUID.randomUUID();

        DriverProfile profile = createDefaultDriverProfileWithId(uuid);
        profile.setId(uuid);
        profile.setRating(BigDecimal.valueOf(5));

        DriverProfileResponse expected = DriverProfileResponse
                .builder()
                .id(uuid)
                .surname(profile.getSurname())
                .name(profile.getName())
                .rating(BigDecimal.valueOf(5))
                .phoneNumber(profile.getPhoneNumber())
                .experience(3)
                .build();

        doReturn(Optional.of(profile)).when(repository).getDriverProfilesById(uuid);

        DriverProfileResponse foundProfile = service.getProfileById(uuid);

        assertEquals(expected, foundProfile);
        verify(repository, times(1)).getDriverProfilesById(uuid);

    }


    @Test
    public void getDriverProfileById_DriverProfileNotExists_shouldThrowNoSuchRecordException() {

        UUID uuid = UUID.randomUUID();

        DriverProfile profile = createDefaultDriverProfileWithId(uuid);
        profile.setId(uuid);
        profile.setRating(BigDecimal.valueOf(5));

        doReturn(Optional.empty()).when(repository).getDriverProfilesById(uuid);



        assertThrows(NoSuchRecordException.class, () -> service.getProfileById(uuid));


        verify(repository, times(1)).getDriverProfilesById(uuid);

    }


    @Test
    public void getDriverProfilePage_DriverProfilePageEmptyPage_shouldReturnDriverProfilePage() {

        UUID uuid = UUID.randomUUID();

        DriverProfile profile = createDefaultDriverProfileWithId(uuid);
        profile.setId(uuid);
        profile.setRating(BigDecimal.valueOf(5));

        Page<DriverProfile> page = new PageImpl<>(List.of());

        DriverProfilePageRequest pageRequest = createDefaultDriverProfilePageRequest();

        DriverProfilePageResponse expected = DriverProfilePageResponse
                .builder()
                .totalElements(0)
                .pageNumber(1)
                .totalPages(1)
                .profiles(List.of())
                .build();

        doReturn(page).when(repository).findAll(any(PageRequest.class));

        DriverProfilePageResponse pageResponse = service.getProfilePage(pageRequest);

        assertEquals(expected, pageResponse);
        verify(repository, times(1)).findAll(any(PageRequest.class));

    }

    @Test
    public void getDriverProfileAndRecalculateRating_correctRecalculateRatingModel_saveAndReturnProfileWithRecalculatedRating() {

    }

}
