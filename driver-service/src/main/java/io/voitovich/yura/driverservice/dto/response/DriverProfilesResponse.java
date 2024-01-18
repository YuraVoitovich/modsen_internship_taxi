package io.voitovich.yura.driverservice.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record DriverProfilesResponse(
        List<DriverProfileResponse> profiles
) {

}
