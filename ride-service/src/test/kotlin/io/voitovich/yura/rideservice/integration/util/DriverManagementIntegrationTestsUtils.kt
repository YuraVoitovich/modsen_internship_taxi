package io.voitovich.yura.rideservice.integration.util

import io.voitovich.yura.rideservice.client.model.DriverProfileModel
import io.voitovich.yura.rideservice.client.model.PassengerProfileModel
import io.voitovich.yura.rideservice.client.model.PassengerProfileModels
import io.voitovich.yura.rideservice.dto.responce.*
import io.voitovich.yura.rideservice.entity.RideStatus
import java.math.BigDecimal
import java.util.*

class DriverManagementIntegrationTestsUtils {
    companion object {
        fun getRidesWithRadius700() : List<AvailableRideResponse> {
            return listOf(
                AvailableRideResponse(
                id = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa53c"),
                    passengerProfileId = UUID.fromString("fe619349-6734-4b2e-b949-0657af63b9d8"),
                    startGeo = ResponsePoint(40.1128, -74.009),
                    endGeo = ResponsePoint(60.0, 70.0),
                    distance = BigDecimal.valueOf(670)
                ),
                AvailableRideResponse(
                    id = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa54c"),
                    passengerProfileId = UUID.fromString("f7d49a65-1b28-4eab-8fc4-4a4d8f5e80cc"),
                    startGeo = ResponsePoint(40.1128, -74.008),
                    endGeo = ResponsePoint(50.0, 60.0),
                    distance = BigDecimal.valueOf(558)
                ),
            )

        }

        fun getRidesWithRadius600() : List<AvailableRideResponse> {
            return listOf(
                AvailableRideResponse(
                    id = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa54c"),
                    passengerProfileId = UUID.fromString("f7d49a65-1b28-4eab-8fc4-4a4d8f5e80cc"),
                    startGeo = ResponsePoint(40.1128, -74.008),
                    endGeo = ResponsePoint(50.0, 60.0),
                    distance = BigDecimal.valueOf(558)
                ),
            )
        }

        fun createResponsePoint(): ResponsePoint {
            return ResponsePoint(40.0, -74.0)
        }

        fun getAllDriverRidesDriverProfileModel(): DriverProfileModel {
            return DriverProfileModel(
                id = UUID.randomUUID(),
                phoneNumber = "",
                name = "Driver1",
                surname = "",
                rating = BigDecimal.ONE,
                experience = 3
            )
        }

        fun getAllDriverRidesPassengerProfileModels(): PassengerProfileModels {
            return PassengerProfileModels(
                models = listOf(
                    PassengerProfileModel(
                        id = UUID.fromString("7e4f5342-cb2b-4e8c-8ab7-1629afcf5d01"),
                        phoneNumber = "",
                        name = "Passenger1",
                        surname = "",
                        rating = BigDecimal.ONE
                    ),
                    PassengerProfileModel(
                        id = UUID.fromString("7e4f5342-cb2b-4e8c-8ab7-1629afcf5d02"),
                        phoneNumber = "",
                        name = "Passenger2",
                        surname = "",
                        rating = BigDecimal.ONE
                    )
                )
            )
        }
        fun getAllDriverRides(): List<RideResponse> {
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
                    startGeo =createResponsePoint(),
                    endGeo = createResponsePoint(),
                    id = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa59c"),
                    status = RideStatus.COMPLETED,
                    startDate = null,
                    endDate = null,
                    driverRating = null,
                    passengerRating = null,
                    passengerPosition = createResponsePoint(),
                    driverPosition = createResponsePoint()
                ),
                RideResponse(
                    passengerProfile = PassengerProfileResponse(
                        name = "Passenger2",
                        rating = BigDecimal.ONE
                    ),
                    driverProfile = DriverProfileResponse(
                        name = "Driver1",
                        rating = BigDecimal.ONE,
                        experience = 3
                    ),
                    startGeo =createResponsePoint(),
                    endGeo = createResponsePoint(),
                    id = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa60c"),
                    status = RideStatus.COMPLETED,
                    startDate = null,
                    endDate = null,
                    driverRating = null,
                    passengerRating = null,
                    passengerPosition = createResponsePoint(),
                    driverPosition = createResponsePoint()
                )
            )
        }




    }
}