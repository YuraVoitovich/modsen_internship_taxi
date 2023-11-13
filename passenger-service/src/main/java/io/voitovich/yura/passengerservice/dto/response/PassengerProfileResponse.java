package io.voitovich.yura.passengerservice.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PassengerProfileResponse(
        @NonNull
        UUID id,
        @NonNull
        String phoneNumber,
        @NonNull
        String name,
        @NonNull
        String surname,
        @NonNull
        BigDecimal rating
) {
}
