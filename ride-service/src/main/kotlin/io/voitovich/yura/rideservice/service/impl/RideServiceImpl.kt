package io.voitovich.yura.rideservice.service.impl

import io.voitovich.yura.rideservice.dto.mapper.RideMapper
import io.voitovich.yura.rideservice.dto.request.*
import io.voitovich.yura.rideservice.dto.responce.*
import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.entity.RideStatus
import io.voitovich.yura.rideservice.exception.NoSuchRecordException
import io.voitovich.yura.rideservice.exception.RideAlreadyAccepted
import io.voitovich.yura.rideservice.exception.RideAlreadyCanceled
import io.voitovich.yura.rideservice.exception.RideAlreadyPresented
import io.voitovich.yura.rideservice.repository.RideRepository
import io.voitovich.yura.rideservice.service.RideService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.*

@Service
class RideServiceImpl(val repository: RideRepository, val mapper: RideMapper) : RideService {

    private val log = KotlinLogging.logger {  }
    override fun getRideById(id: UUID): RideResponse {
        log.info { "Retrieving ride information for ride with id: $id" }
        val ride = repository.findById(id)
        return mapper.toRideResponse(ride
            .orElseThrow { NoSuchRecordException(String
            .format("Ride with id: {%s} was not found", id))}
        )
    }

    override fun deleteRideById(id: UUID) {
        log.info{ "Deleting ride with id: $id" }
        val ride = repository.findById(id)
        if (ride.isEmpty) {
            throw NoSuchRecordException(String.format("Ride with id: {%s} was not found", id))
        }
        repository.deleteById(id)
    }

    override fun getRidePage(pageRideRequest: RidePageRequest): RidePageResponse {
        log.info { "Retrieving rides for page ${pageRideRequest.pageNumber} " +
                "with size ${pageRideRequest.pageSize} " +
                "and ordering by ${pageRideRequest.orderBy}" }
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

}