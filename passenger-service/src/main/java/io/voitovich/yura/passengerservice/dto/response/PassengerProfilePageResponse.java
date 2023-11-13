package io.voitovich.yura.passengerservice.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

@Builder
@Schema(name = "PassengerProfilePageResponse", description = "Response with information about the page")
public record PassengerProfilePageResponse(
        @Schema(name = "profiles", description = "Passenger profiles")
        @NonNull
        List<PassengerProfileResponse> profiles,
        @Schema(name = "pageNumber", description = "The page number")
        @Min(1)
        int pageNumber,
        @Schema(name = "totalElements", description = "The total elements count")
        @Min(0)
        long totalElements,
        @Schema(name = "totalPages", description = "The total pages count")
        @Min(0)
        int totalPages
) {


}
