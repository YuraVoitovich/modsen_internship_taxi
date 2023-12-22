package io.voitovich.yura.rideservice.unit.util

import io.voitovich.yura.rideservice.client.model.PassengerProfileModel
import io.voitovich.yura.rideservice.dto.mapper.RideMapper
import io.voitovich.yura.rideservice.dto.request.*
import io.voitovich.yura.rideservice.event.model.ConfirmRatingReceiveModel
import org.locationtech.jts.geom.Point
import java.math.BigDecimal
import java.util.*

class UnitTestsUtils {

    companion object {
        fun createDefaultGetAvailableRidesRequest(): GetAvailableRidesRequest {
            return GetAvailableRidesRequest(id = UUID.randomUUID(), currentLocation = createLocation(), radius = 300)
        }

        fun createDefaultGetAvailableRidesRequestWithoutRadius(): GetAvailableRidesRequest {
            return GetAvailableRidesRequest(id = UUID.randomUUID(), currentLocation = createLocation(), radius = null)
        }

        fun createDefaultGetAvailableRidesRequestWithInvalidRadius(): GetAvailableRidesRequest {
            return GetAvailableRidesRequest(id = UUID.randomUUID(), currentLocation = createLocation(), radius = 100)
        }

        fun createDefaultUpdatePositionRequest(): UpdatePositionRequest {
            return UpdatePositionRequest(rideId = UUID.randomUUID(), id = UUID.randomUUID(), location = createLocation())
        }

        fun createDefaultRidePageRequest(): RidePageRequest {
            return RidePageRequest(pageNumber = 1, pageSize = 1, orderBy = "id")
        }

        fun createDefaultConfirmRatingReceiveModel(): ConfirmRatingReceiveModel {
            return ConfirmRatingReceiveModel(rideId = UUID.randomUUID(), rating = BigDecimal.ONE)
        }

        fun createDefaultCreateRideRequest(): CreateRideRequest {
            return CreateRideRequest(passengerId = UUID.randomUUID(), startGeo = createLocation(), endGeo = createLocation())
        }

        fun createDefaultPassengerProfileModel(): PassengerProfileModel {
            return PassengerProfileModel(
                id = UUID.randomUUID(),
                phoneNumber = "+375295432551",
                name = "Name",
                surname = "Surname",
                rating = BigDecimal(5))
        }

        fun createDefaultCancelRequest(): CancelRequest {
            return CancelRequest(passengerId = UUID.randomUUID(), rideId = UUID.randomUUID())
        }

        fun createDefaultAcceptRideRequest(): AcceptRideRequest {
            return AcceptRideRequest(rideId = UUID.randomUUID(), driverId = UUID.randomUUID(), location = createLocation())
        }

        fun createDefaultPoint(mapper: RideMapper): Point {
            return mapper.fromRequestPointToPoint(createLocation())
        }

        fun createDefaultSendRatingRequest(): SendRatingRequest {
            return SendRatingRequest(rideId = UUID.randomUUID(), rating = BigDecimal.ONE)
        }

        fun createLocation(): RequestPoint {
            return RequestPoint(latitude = BigDecimal.TEN, longitude = BigDecimal.TEN)
        }
    }

}