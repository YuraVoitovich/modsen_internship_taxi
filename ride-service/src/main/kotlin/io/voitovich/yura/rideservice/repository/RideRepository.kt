package io.voitovich.yura.rideservice.repository

import io.voitovich.yura.rideservice.entity.Ride
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RideRepository: JpaRepository<Ride, UUID> {

 }