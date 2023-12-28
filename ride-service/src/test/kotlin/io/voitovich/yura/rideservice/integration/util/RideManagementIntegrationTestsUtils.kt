package io.voitovich.yura.rideservice.integration.util

import io.voitovich.yura.rideservice.client.model.DriverProfileModel
import io.voitovich.yura.rideservice.client.model.DriverProfileModels
import io.voitovich.yura.rideservice.client.model.PassengerProfileModel
import io.voitovich.yura.rideservice.client.model.PassengerProfileModels
import io.voitovich.yura.rideservice.dto.responce.DriverProfileResponse
import io.voitovich.yura.rideservice.dto.responce.PassengerProfileResponse
import io.voitovich.yura.rideservice.dto.responce.ResponsePoint
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.entity.RideStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class RideManagementIntegrationTestsUtils {

    companion object {
        fun getAllRides(): List<RideResponse> {
            return listOf(
                RideResponse(
                    passengerProfile = PassengerProfileResponse(
                        name = "Passenger1",
                        rating = BigDecimal.ONE
                    ),
                    driverProfile = DriverProfileResponse(
                        name = "Driver1",
                        rating = BigDecimal.ONE,
                        experience = 3
                    ),
                    startGeo = ResponsePoint(
                        40.7128,
                        -74.0060
                    ),
                    endGeo = ResponsePoint(
                        40.7128,
                        -74.0460
                    ),
                    id = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa50c"),
                    status = RideStatus.ACCEPTED,
                    startDate = null,
                    endDate = null,
                    driverRating = BigDecimal.valueOf(4.5),
                    passengerRating = BigDecimal.valueOf(5.0),
                    passengerPosition = ResponsePoint(
                        15.0,
                        25.0
                    ),
                    driverPosition = ResponsePoint(
                        40.7128,
                        -74.0064
                    )
                ),
                RideResponse(
                    passengerProfile = PassengerProfileResponse(
                        name = "Passenger2",
                        rating = BigDecimal.ONE
                    ),
                    driverProfile = DriverProfileResponse(
                        name = "Driver2",
                        rating = BigDecimal.ONE,
                        experience = 3
                    ),
                    startGeo = ResponsePoint(
                        40.7128,
                        -74.0060
                    ),
                    endGeo = ResponsePoint(
                        40.7128,
                        -74.0460
                    ),
                    id = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa51c"),
                    status = RideStatus.IN_PROGRESS,
                    startDate = null,
                    endDate = null,
                    driverRating = BigDecimal.valueOf(4.2),
                    passengerRating = BigDecimal.valueOf(4.8),
                    passengerPosition = ResponsePoint(
                        15.0,
                        25.0
                    ),
                    driverPosition = ResponsePoint(
                        40.7128,
                        -74.0064
                    )
                )
            )
        }

        fun getAllRidesPassengerProfileModels(): PassengerProfileModels {
            return PassengerProfileModels(
                models = listOf(
                    PassengerProfileModel(
                        id = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"),
                        phoneNumber = "",
                        name = "Passenger1",
                        surname = "",
                        rating = BigDecimal.ONE
                    ),
                    PassengerProfileModel(
                        id = UUID.fromString("f00a8f6f-9294-4e4e-aa4d-42f801b69a95"),
                        phoneNumber = "",
                        name = "Passenger2",
                        surname = "",
                        rating = BigDecimal.ONE
                    )
                )
            )
        }

        fun getAllRidesDriverProfileModels(): DriverProfileModels {
            return DriverProfileModels(
                models = listOf(
                    DriverProfileModel(
                        id = UUID.fromString("025fe6d1-8363-4a1a-925d-d91a8b640b8f"),
                        phoneNumber = "",
                        name = "Driver1",
                        surname = "",
                        rating = BigDecimal.ONE,
                        experience = 3
                    ),
                    DriverProfileModel(
                        id = UUID.fromString("6d8a8f9a-7f9d-4c71-8b4c-2e0e487b3262"),
                        phoneNumber = "",
                        name = "Driver2",
                        surname = "",
                        rating = BigDecimal.ONE,
                        experience = 3
                    )
                )
            )
        }
    }
}