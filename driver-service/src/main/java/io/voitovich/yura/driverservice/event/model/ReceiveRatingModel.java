package io.voitovich.yura.driverservice.event.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ReceiveRatingModel(
        UUID raterId,
        UUID ratedId,

        UUID rideId,
        BigDecimal rating
) {
}
