package io.voitovich.yura.driverservice.unit.util;

import io.voitovich.yura.driverservice.dto.request.DriverProfilePageRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfileSaveRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfileUpdateRequest;
import io.voitovich.yura.driverservice.dto.response.DriverProfilePageResponse;
import io.voitovich.yura.driverservice.entity.DriverProfile;
import io.voitovich.yura.driverservice.event.model.ReceiveRatingModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class UnitTestsUtils {


    public static DriverProfile createDefaultDriverProfileWithoutId() {
        return DriverProfile.builder()
                .surname("surname")
                .name("name")
                .phoneNumber("+375295432551")
                .experience(3)
                .build();
    }

    public static DriverProfilePageResponse createDefaultDriverProfilePageResponse() {
        return DriverProfilePageResponse.builder()
                .profiles(List.of())
                .totalPages(1)
                .pageNumber(1)
                .totalElements(0)
                .build();
    }

    public static DriverProfilePageRequest createDefaultDriverProfilePageRequest() {
        return DriverProfilePageRequest.builder()
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

    public static DriverProfileSaveRequest createDefaultDriverProfileSaveRequest() {
        return DriverProfileSaveRequest
                .builder()
                .experience(3)
                .phoneNumber("+375295432551")
                .name("name")
                .surname("surname")
                .build();
    }


    public static DriverProfile createDefaultDriverProfileWithId(UUID uuid) {
        return DriverProfile.builder()
                .id(uuid)
                .surname("Surname")
                .name("name")
                .experience(3)
                .phoneNumber("+375295432551")
                .build();
    }

    public static DriverProfileUpdateRequest createDefaultDriverProfileUpdateRequest(UUID uuid) {
        return DriverProfileUpdateRequest
                .builder()
                .id(uuid)
                .experience(4)
                .surname("surname")
                .name("name")
                .phoneNumber("+375295432551")
                .build();
    }
}
