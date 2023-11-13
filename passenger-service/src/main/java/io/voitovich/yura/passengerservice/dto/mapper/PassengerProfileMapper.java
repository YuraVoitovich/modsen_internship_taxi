package io.voitovich.yura.passengerservice.dto.mapper;


import io.voitovich.yura.passengerservice.dto.request.PassengerProfileRequest;
import io.voitovich.yura.passengerservice.entity.PassengerProfile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PassengerProfileMapper {
    PassengerProfileMapper INSTANCE = Mappers.getMapper(PassengerProfileMapper.class);

    PassengerProfileRequest toDto(PassengerProfile passengerProfile);

    PassengerProfile toEntity(PassengerProfileRequest passengerProfileRequest);



}
