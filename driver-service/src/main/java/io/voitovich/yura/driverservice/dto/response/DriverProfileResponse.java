package io.voitovich.yura.driverservice.dto.response;

import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record DriverProfileResponse(
        @NonNull
        UUID id,
        @NonNull
        String phoneNumber,
        @NonNull
        String name,
        @NonNull
        String surname,
        @NonNull
        BigDecimal rating,
        int experience
) { }