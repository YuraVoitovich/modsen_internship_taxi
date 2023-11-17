package io.voitovich.yura.rideservice.service.impl

import io.voitovich.yura.rideservice.dto.mapper.RideMapper
import io.voitovich.yura.rideservice.dto.request.CreateRideRequest
import io.voitovich.yura.rideservice.dto.request.RidePageRequest
import io.voitovich.yura.rideservice.dto.responce.CreateRideResponse
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.exception.NoSuchRecordException
import io.voitovich.yura.rideservice.repository.RideRepository
import io.voitovich.yura.rideservice.service.RideService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.*

@Service
class RideServiceImpl(val repository: RideRepository, val mapper: RideMapper) : RideService {





    override fun getRideById(id: UUID): RideResponse {
        val ride = repository.findById(id)

        return mapper.toRideResponse(ride
            .orElseThrow { NoSuchRecordException(String
            .format("Driver profile with id: {%s} not found", id))}
        )
    }

    override fun deleteRideById(id: UUID) {
        val ride = repository.findById(id);
        if (ride.isEmpty) {
            throw NoSuchRecordException(String.format("", id))
        }
        repository.deleteById(id)
    }

    override fun getRidePage(pageRideRequest: RidePageRequest): RidePageResponse {
        val page = repository.findAll(PageRequest
            .of(pageRideRequest.pageNumber - 1,
                pageRideRequest.pageSize,
                Sort.by(pageRideRequest.orderBy)))
        return RidePageResponse(page
            .content.stream()
            .map {t-> mapper.toRideResponse(t)}.toList(),
            pageRideRequest.pageNumber,
            page.totalElements,
            page.totalPages)
    }

    override fun createRide(request: CreateRideRequest): CreateRideResponse {
        val ride = mapper.fromCreateRequestToEntity(request)
        val savedRide = repository.save(ride)
        return CreateRideResponse(request.passengerId, savedRide.id!!)
    }
}