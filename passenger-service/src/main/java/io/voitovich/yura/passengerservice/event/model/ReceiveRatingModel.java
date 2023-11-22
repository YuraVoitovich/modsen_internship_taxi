package io.voitovich.yura.passengerservice.event.model;

import java.math.BigDecimal;
import java.util.UUID;

public record ReceiveRatingModel(
        UUID raterId,
        UUID ratedId,
        BigDecimal rating
) {
}
