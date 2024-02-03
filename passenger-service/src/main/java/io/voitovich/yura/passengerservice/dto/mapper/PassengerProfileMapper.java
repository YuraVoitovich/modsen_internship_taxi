package io.voitovich.yura.passengerservice.dto.mapper;


import io.voitovich.yura.passengerservice.dto.request.PassengerProfileUpdateRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerSaveProfileRequest;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfileResponse;
import io.voitovich.yura.passengerservice.entity.PassengerProfile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PassengerProfileMapper {
    PassengerProfile fromSaveRequestToEntity(PassengerSaveProfileRequest passengerSaveProfileRequest);

    PassengerProfileResponse toProfileResponse(PassengerProfile profile);

    void updateEntityFromUpdateRequest(PassengerProfileUpdateRequest request, @MappingTarget PassengerProfile target);


}
