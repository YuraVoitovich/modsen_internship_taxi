package io.voitovich.yura.passengerservice.dto.request;


import io.voitovich.yura.passengerservice.dto.response.PassengerProfileResponse;
import io.voitovich.yura.passengerservice.validation.annotations.OrderBy;
import jakarta.validation.constraints.Min;

public record PassengerProfilePageRequest(
        @Min(1)
        int pageNumber,
        @Min(1)
        int pageSize,
        @OrderBy(PassengerProfileResponse.class)
        String orderBy
) {
}
