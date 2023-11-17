package io.voitovich.yura.rideservice.model

import org.geolatte.geom.Point
import org.geolatte.geom.Position
import java.util.*

interface RideProjection {
    fun getId(): UUID
    fun getPassengerProfileId(): UUID
    fun getStartGeo(): Point<Position>
    fun getEndGeo(): Point<Position>
    fun getDistance(): Double
}