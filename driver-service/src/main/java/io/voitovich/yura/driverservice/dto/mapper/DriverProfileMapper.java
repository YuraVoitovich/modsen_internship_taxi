package io.voitovich.yura.driverservice.dto.mapper;


import io.voitovich.yura.driverservice.dto.request.DriverProfileRequest;
import io.voitovich.yura.driverservice.dto.response.DriverProfileResponse;
import io.voitovich.yura.driverservice.entity.DriverProfile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DriverProfileMapper {

    DriverProfileMapper INSTANCE = Mappers.getMapper(DriverProfileMapper.class);
    DriverProfileRequest toProfileRequest(DriverProfile profile);
    DriverProfile toProfileEntity(DriverProfileRequest profileDto);

    DriverProfileResponse toProfileResponse(DriverProfile profile);

}
