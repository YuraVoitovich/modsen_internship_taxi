package io.voitovich.yura.rideservice.integration.util

import io.voitovich.yura.rideservice.client.model.DriverProfileModel
import io.voitovich.yura.rideservice.client.model.DriverProfileModels
import io.voitovich.yura.rideservice.client.model.PassengerProfileModel
import io.voitovich.yura.rideservice.dto.request.CreateRideRequest
import io.voitovich.yura.rideservice.dto.request.RequestPoint
import io.voitovich.yura.rideservice.dto.responce.DriverProfileResponse
import io.voitovich.yura.rideservice.dto.responce.PassengerProfileResponse
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.entity.RideStatus
import java.math.BigDecimal
import java.util.*

class PassengerManagementIntegrationTestsUtils {
    companion object {
        fun getAllPassengerRidesDriverProfilesModels(): DriverProfileModels {
            return DriverProfileModels(
                models = listOf(
                    DriverProfileModel(
                        id = UUID.fromString("d1e4120d-aa71-448c-843f-5a5e801ed287"),
                        phoneNumber = "",
                        name = "Driver1",
                        surname = "",
                        rating = BigDecimal.ONE,
                        experience = 3
                    ),
                    DriverProfileModel(
                        id = UUID.fromString("d1843c0d-aa71-448c-843f-5a5e801ed287"),
                        phoneNumber = "",
                        name = "Driver2",
                        surname = "",
                        rating = BigDecimal.ONE,
                        experience = 3
                    )
                )
            )
        }

        fun createRequestPoint(): RequestPoint {
            return RequestPoint(
                BigDecimal.ONE,
                BigDecimal.ONE
            )
        }

        fun getDefaultCreateRideRequest(passengerId: UUID): CreateRideRequest {
            return CreateRideRequest(
                passengerId = passengerId,
                startGeo = createRequestPoint(),
                endGeo = createRequestPoint()
            )
        }

        fun getAllPassengerRidesPassengerProfileModel(): PassengerProfileModel {
            return PassengerProfileModel(
                id = UUID.randomUUID(),
                phoneNumber = "",
                name = "Passenger1",
                surname = "",
                rating = BigDecimal.ONE,
            )
        }

        fun getAllPassengerRides(): List<RideResponse> {
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
                    startGeo = DriverManagementIntegrationTestsUtils.createResponsePoint(),
                    endGeo = DriverManagementIntegrationTestsUtils.createResponsePoint(),
                    id = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa62c"),
                    status = RideStatus.REQUESTED,
                    startDate = null,
                    endDate = null,
                    driverRating = null,
                    passengerRating = null,
                    passengerPosition = DriverManagementIntegrationTestsUtils.createResponsePoint(),
                    driverPosition = DriverManagementIntegrationTestsUtils.createResponsePoint()
                ),
                RideResponse(
                    passengerProfile = PassengerProfileResponse(
                        name = "Passenger1",
                        rating = BigDecimal.ONE
                    ),
                    driverProfile = DriverProfileResponse(
                        name = "Driver2",
                        rating = BigDecimal.ONE,
                        experience = 3
                    ),
                    startGeo = DriverManagementIntegrationTestsUtils.createResponsePoint(),
                    endGeo = DriverManagementIntegrationTestsUtils.createResponsePoint(),
                    id = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa63c"),
                    status = RideStatus.COMPLETED,
                    startDate = null,
                    endDate = null,
                    driverRating = null,
                    passengerRating = null,
                    passengerPosition = DriverManagementIntegrationTestsUtils.createResponsePoint(),
                    driverPosition = DriverManagementIntegrationTestsUtils.createResponsePoint()
                )
            )
        }
    }
}