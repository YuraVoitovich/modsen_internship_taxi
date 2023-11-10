package io.voitovich.yura.driverservice.dto.mapper;


import io.voitovich.yura.driverservice.dto.DriverProfileDto;
import io.voitovich.yura.driverservice.entity.DriverProfile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DriverProfileMapper {

    DriverProfileMapper INSTANCE = Mappers.getMapper(DriverProfileMapper.class);
    DriverProfileDto toDto(DriverProfile profile);
    DriverProfile toEntity(DriverProfileDto profileDto);
}
