package io.voitovich.yura.passengerservice.dto.response;


import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

@Builder
public record PassengerProfilePageResponse(
        @NonNull
        List<PassengerProfileResponse> profiles,
        @Min(1)
        int pageNumber,
        @Min(0)
        long totalElements,
        @Min(0)
        int totalPages
) {


}
