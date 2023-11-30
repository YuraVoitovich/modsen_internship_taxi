package io.voitovich.yura.passengerservice.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfileResponse;
import io.voitovich.yura.passengerservice.validation.annotations.OrderBy;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Schema(name = "PassengerProfilePageRequest"
        , description = "request for obtaining passenger profiles in the form of pages")
@Builder
public record PassengerProfilePageRequest(
        @Schema(name = "page number", description = "The page number", minimum = "1")
        @Min(value = 1, message = "{api.error.min.pageNumber}")
        int pageNumber,
        @Schema(name = "page size", description = "The page size", minimum = "1")
        @Min(value = 1, message = "{api.error.min.pageSize}")
        int pageSize,
        @Schema(name = "orderBy", description = "Sort by Passenger profile response field name")
        @OrderBy(value = PassengerProfileResponse.class, message = "{api.error.orderBy}")
        String orderBy
) {
}
