package io.voitovich.yura.driverservice.dto;


import io.voitovich.yura.driverservice.validation.annotations.OrderBy;
import jakarta.validation.constraints.Min;

public record DriverProfilePageRequest(
        @Min(1)
        int pageNumber,
        @Min(1)
        int pageSize,
        @OrderBy(DriverProfileDto.class)
        String orderBy
) {
}
