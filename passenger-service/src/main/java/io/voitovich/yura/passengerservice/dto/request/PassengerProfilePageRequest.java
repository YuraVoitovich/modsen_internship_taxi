package io.voitovich.yura.passengerservice.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfileResponse;
import io.voitovich.yura.passengerservice.validation.annotations.OrderBy;
import jakarta.validation.constraints.Min;

@Schema(name = "PassengerProfilePageRequest"
        , description = "request for obtaining passenger profiles in the form of pages")
public record PassengerProfilePageRequest(
        @Schema(name = "page number", description = "The page number", minimum = "1")
        @Min(1)
        int pageNumber,
        @Schema(name = "page size", description = "The page size", minimum = "1")
        @Min(1)
        int pageSize,
        @Schema(name = "orderBy", description = "Sort by Passenger profile response field name")
        @OrderBy(PassengerProfileResponse.class)
        String orderBy
) {
}
