package io.voitovich.yura.passengerservice.event.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ConfirmRatingReceiveModel(
        UUID rideId,
        BigDecimal rating
) {
}
