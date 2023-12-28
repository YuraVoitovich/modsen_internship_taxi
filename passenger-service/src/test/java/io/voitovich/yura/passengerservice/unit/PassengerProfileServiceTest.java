package io.voitovich.yura.passengerservice.unit;

import io.voitovich.yura.passengerservice.dto.mapper.PassengerProfileMapper;
import io.voitovich.yura.passengerservice.dto.request.PassengerProfilePageRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerProfileUpdateRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerSaveProfileRequest;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfilePageResponse;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfileResponse;
import io.voitovich.yura.passengerservice.entity.PassengerProfile;
import io.voitovich.yura.passengerservice.exception.NoSuchRecordException;
import io.voitovich.yura.passengerservice.exception.NotUniquePhoneException;
import io.voitovich.yura.passengerservice.repository.PassengerProfileRepository;
import io.voitovich.yura.passengerservice.service.impl.PassengerProfileServiceImpl;
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

import static io.voitovich.yura.passengerservice.unit.util.UnitTestsUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PassengerProfileServiceTest {


    @Mock
    private PassengerProfileRepository repository;
    @InjectMocks
    private PassengerProfileServiceImpl service;

    @Test
    public void deletePassengerProfile_passengerProfileNotFound_throwNoSuchRecordException () {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act
        assertThrows(NoSuchRecordException.class, () -> service.deleteProfile(uuid));

        // Assert
        verify(repository, times(1)).getPassengerProfileById(uuid);
    }

    @Test
    public void deletePassengerProfile_passengerWasFound_shouldDeletePassengerProfile () {
        // Arrange
        UUID uuid = UUID.randomUUID();
        PassengerProfile profile = createDefaultPassengerProfileWithId(uuid);
        doReturn(Optional.of(profile)).when(repository).getPassengerProfileById(uuid);

        // Act
        service.deleteProfile(uuid);

        // Assert
        verify(repository, times(1)).getPassengerProfileById(uuid);
        verify(repository, times(1)).deleteById(uuid);
    }

    @Test
    public void savePassengerProfile_correctPassengerProfile_shouldSavePassengerProfile () {
        // Arrange
        UUID uuid = UUID.randomUUID();
        PassengerSaveProfileRequest request = createDefaultPassengerProfileSaveRequest();
        PassengerProfile profile = PassengerProfileMapper.INSTANCE.fromSaveRequestToEntity(request);
        profile.setId(uuid);
        profile.setRating(BigDecimal.valueOf(5));
        doReturn(false).when(repository).existsByPhoneNumber(any());
        doReturn(profile).when(repository).save(any(PassengerProfile.class));

        PassengerProfileResponse expected = PassengerProfileResponse
                .builder()
                .id(uuid)
                .surname(request.surname())
                .name(request.name())
                .rating(BigDecimal.valueOf(5))
                .phoneNumber(request.phoneNumber())
                .build();

        // Act
        PassengerProfileResponse savedProfile = service.saveProfile(request);

        // Assert
        assertEquals(expected, savedProfile);
        verify(repository, times(1)).existsByPhoneNumber(any());
        verify(repository, times(1)).save(any());

    }

    @Test
    public void savePassengerProfile_phoneNumberExists_shouldThrowNotUniquePhoneException () {
        // Arrange
        UUID uuid = UUID.randomUUID();
        PassengerSaveProfileRequest request = createDefaultPassengerProfileSaveRequest();
        PassengerProfile profile = PassengerProfileMapper.INSTANCE.fromSaveRequestToEntity(request);
        profile.setId(uuid);
        profile.setRating(BigDecimal.valueOf(5));
        doReturn(true).when(repository).existsByPhoneNumber(any());

        // Act & Assert
        assertThrows(NotUniquePhoneException.class, () -> service.saveProfile(request));

        verify(repository, times(1)).existsByPhoneNumber(any());

    }

    @Test
    public void updatePassengerProfile_phoneNumberExists_shouldThrowNotUniquePhoneException () {
        // Arrange
        UUID uuid = UUID.randomUUID();
        PassengerProfile profile = createDefaultPassengerProfileWithId(uuid);
        profile.setRating(BigDecimal.valueOf(5));
        profile.setPhoneNumber("+375295432550");
        PassengerProfileUpdateRequest request = createDefaultPassengerProfileUpdateRequest(uuid);

        doReturn(Optional.of(profile)).when(repository).getPassengerProfileById(uuid);
        doReturn(true).when(repository).existsByPhoneNumber(any());

        // Act & Assert
        assertThrows(NotUniquePhoneException.class, () -> service.updateProfile(request));

        verify(repository, times(1)).existsByPhoneNumber(any());

    }

    @Test
    public void updatePassengerProfile_passengerProfileNotExists_shouldThrowNoSuchRecordException () {
        // Arrange
        UUID uuid = UUID.randomUUID();
        PassengerProfile profile = createDefaultPassengerProfileWithId(uuid);
        profile.setRating(BigDecimal.valueOf(5));
        PassengerProfileUpdateRequest request = createDefaultPassengerProfileUpdateRequest(uuid);

        doReturn(Optional.empty()).when(repository).getPassengerProfileById(uuid);

        // Act & Assert
        assertThrows(NoSuchRecordException.class, () -> service.updateProfile(request));

        verify(repository).getPassengerProfileById(uuid);

    }


    @Test
    public void updatePassengerProfile_correctPassengerProfile_shouldUpdatePassengerProfile () {
        // Arrange
        UUID uuid = UUID.randomUUID();
        PassengerProfile profile = createDefaultPassengerProfileWithId(uuid);
        profile.setRating(BigDecimal.valueOf(5));
        PassengerProfileUpdateRequest request = createDefaultPassengerProfileUpdateRequest(uuid);
        PassengerProfileResponse expected = PassengerProfileResponse
                .builder()
                .id(uuid)
                .surname(request.surname())
                .name(request.name())
                .rating(BigDecimal.valueOf(5))
                .phoneNumber(request.phoneNumber())
                .build();

        doReturn(Optional.of(profile)).when(repository).getPassengerProfileById(uuid);
        doReturn(profile).when(repository).save(any(PassengerProfile.class));

        // Act
        PassengerProfileResponse updateProfile = service.updateProfile(request);

        // Assert
        assertEquals(expected, updateProfile);
        verify(repository, times(1)).save(any());

    }


    @Test
    public void getPassengerProfileById_passengerProfileExists_shouldReturnPassengerProfile() {

        // Arrange
        UUID uuid = UUID.randomUUID();
        PassengerProfile profile = createDefaultPassengerProfileWithId(uuid);
        profile.setRating(BigDecimal.valueOf(5));
        PassengerProfileResponse expected = PassengerProfileResponse
                .builder()
                .id(uuid)
                .surname(profile.getSurname())
                .name(profile.getName())
                .rating(BigDecimal.valueOf(5))
                .phoneNumber(profile.getPhoneNumber())
                .build();

        doReturn(Optional.of(profile)).when(repository).getPassengerProfileById(uuid);

        // Act
        PassengerProfileResponse foundProfile = service.getProfileById(uuid);

        // Assert
        assertEquals(expected, foundProfile);
        verify(repository, times(1)).getPassengerProfileById(uuid);

    }


    @Test
    public void getPassengerProfileById_passengerProfileNotExists_shouldThrowNoSuchRecordException() {

        // Arrange
        UUID uuid = UUID.randomUUID();
        PassengerProfile profile = createDefaultPassengerProfileWithId(uuid);
        profile.setRating(BigDecimal.valueOf(5));
        doReturn(Optional.empty()).when(repository).getPassengerProfileById(uuid);

        // Act & Assert
        assertThrows(NoSuchRecordException.class, () -> service.getProfileById(uuid));

        verify(repository, times(1)).getPassengerProfileById(uuid);

    }


    @Test
    public void getPassengerProfilePage_passengerProfilePageEmptyPage_shouldReturnPassengerProfilePage() {

        // Arrange
        UUID uuid = UUID.randomUUID();
        PassengerProfile profile = createDefaultPassengerProfileWithId(uuid);
        profile.setRating(BigDecimal.valueOf(5));
        Page<PassengerProfile> page = new PageImpl<>(List.of());
        PassengerProfilePageRequest pageRequest = createDefaultPassengerProfilePageRequest();
        PassengerProfilePageResponse expected = PassengerProfilePageResponse
                .builder()
                .totalElements(0)
                .pageNumber(1)
                .totalPages(1)
                .profiles(List.of())
                .build();

        doReturn(page).when(repository).findAll(any(PageRequest.class));

        // Act
        PassengerProfilePageResponse pageResponse = service.getProfilePage(pageRequest);

        // Assert
        assertEquals(expected, pageResponse);
        verify(repository, times(1)).findAll(any(PageRequest.class));

    }

}
