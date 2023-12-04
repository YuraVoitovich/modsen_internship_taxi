package io.voitovich.yura.driverservice.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record RecalculateRatingModel(
        UUID passengerProfileId,
        BigDecimal newRating,
        Long ratingsCount
        ) {
}
