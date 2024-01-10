package io.voitovich.yura.passengerservice.unit.util;

import io.voitovich.yura.passengerservice.dto.request.PassengerProfilePageRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerProfileUpdateRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerSaveProfileRequest;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfilePageResponse;
import io.voitovich.yura.passengerservice.entity.PassengerProfile;
import io.voitovich.yura.passengerservice.event.model.ReceiveRatingModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class UnitTestsUtils {


    public static PassengerProfile createDefaultPassengerProfileWithoutId() {
        return PassengerProfile.builder()
                .surname("surname")
                .name("name")
                .phoneNumber("+375295432551")
                .build();
    }

    public static PassengerProfilePageResponse createDefaultPassengerProfilePageResponse() {
        return PassengerProfilePageResponse.builder()
                .profiles(List.of())
                .totalPages(1)
                .pageNumber(1)
                .totalElements(0)
                .build();
    }

    public static PassengerProfilePageRequest createDefaultPassengerProfilePageRequest() {
        return PassengerProfilePageRequest.builder()
                .pageNumber(1)
                .pageSize(1)
                .orderBy("id")
                .build();
    }


    public static ReceiveRatingModel createDefaultReceiveRatingModel() {
        return ReceiveRatingModel
                .builder()
                .ratedId(UUID.randomUUID())
                .raterId(UUID.randomUUID())
                .rideId(UUID.randomUUID())
                .rating(BigDecimal.valueOf(5))
                .build();
    }

    public static PassengerSaveProfileRequest createDefaultPassengerProfileSaveRequest() {
        return PassengerSaveProfileRequest
                .builder()
                .phoneNumber("+375295432551")
                .name("name")
                .surname("surname")
                .build();
    }


    public static PassengerProfile createDefaultPassengerProfileWithId(UUID uuid) {
        return PassengerProfile.builder()
                .id(uuid)
                .surname("Surname")
                .name("name")
                .phoneNumber("+375295432551")
                .build();
    }

    public static PassengerProfileUpdateRequest createDefaultPassengerProfileUpdateRequest(UUID uuid) {
        return PassengerProfileUpdateRequest
                .builder()
                .id(uuid)
                .surname("surname")
                .name("name")
                .phoneNumber("+375295432551")
                .build();
    }
}
