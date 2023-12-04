package io.voitovich.yura.driverservice.dto.request;


import io.voitovich.yura.driverservice.validation.annotations.OrderBy;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record DriverProfilePageRequest(
        @Min(value = 1, message = "Page number must be greater, then 0")
        int pageNumber,
        @Min(value = 1, message = "Page size must be greater, then 0")
        int pageSize,
        @OrderBy(DriverProfileUpdateRequest.class)
        String orderBy
) {
}
