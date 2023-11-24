package io.voitovich.yura.driverservice.dto.mapper;


import io.voitovich.yura.driverservice.dto.request.DriverProfileSaveRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfileUpdateRequest;
import io.voitovich.yura.driverservice.dto.response.DriverProfileResponse;
import io.voitovich.yura.driverservice.entity.DriverProfile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DriverProfileMapper {

    DriverProfileMapper INSTANCE = Mappers.getMapper(DriverProfileMapper.class);
    void updateProfileEntity(DriverProfileUpdateRequest request, @MappingTarget DriverProfile profile);

    DriverProfileResponse toProfileResponse(DriverProfile profile);

    DriverProfile toProfileFromSaveRequest(DriverProfileSaveRequest request);

}
