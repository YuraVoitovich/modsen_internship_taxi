package io.voitovich.yura.passengerservice.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PassengerProfilesResponse(
        List<PassengerProfileResponse> profiles
) {

}
