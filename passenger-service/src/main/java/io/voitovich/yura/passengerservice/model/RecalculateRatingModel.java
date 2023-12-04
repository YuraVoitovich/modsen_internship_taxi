package io.voitovich.yura.passengerservice.model;

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
