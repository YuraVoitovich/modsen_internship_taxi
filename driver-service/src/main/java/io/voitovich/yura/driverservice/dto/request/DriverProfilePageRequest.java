package io.voitovich.yura.driverservice.dto.request;


import io.voitovich.yura.driverservice.validation.annotations.OrderBy;
import jakarta.validation.constraints.Min;

public record DriverProfilePageRequest(
        @Min(1)
        int pageNumber,
        @Min(1)
        int pageSize,
        @OrderBy(DriverProfileUpdateRequest.class)
        String orderBy
) {
}
