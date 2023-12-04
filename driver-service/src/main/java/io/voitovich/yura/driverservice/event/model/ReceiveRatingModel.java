package io.voitovich.yura.driverservice.event.model;

import java.math.BigDecimal;
import java.util.UUID;

public record ReceiveRatingModel(
        UUID raterId,
        UUID ratedId,
        BigDecimal rating
) {
}
