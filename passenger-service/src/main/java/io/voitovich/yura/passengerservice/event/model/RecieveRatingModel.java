package io.voitovich.yura.passengerservice.event.model;

import java.math.BigDecimal;
import java.util.UUID;

public record RecieveRatingModel(
        UUID raterId,
        UUID ratedId,
        BigDecimal rating
) {
}
