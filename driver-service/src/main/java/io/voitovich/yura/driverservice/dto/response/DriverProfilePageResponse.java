package io.voitovich.yura.driverservice.dto.response;


import io.voitovich.yura.driverservice.dto.request.DriverProfileUpdateRequest;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

@Builder
public record DriverProfilePageResponse(
        @NonNull
        List<DriverProfileResponse> profiles,
        @Min(1)
        int pageNumber,
        @Min(0)
        long totalElements,
        @Min(0)
        int totalPages
) {


}
