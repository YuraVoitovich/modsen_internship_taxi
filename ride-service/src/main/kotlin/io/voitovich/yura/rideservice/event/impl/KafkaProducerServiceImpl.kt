package io.voitovich.yura.rideservice.event.impl

import io.voitovich.yura.rideservice.dto.request.SendRatingRequest
import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.event.KafkaProducerService
import io.voitovich.yura.rideservice.event.model.SendRatingModel
import io.voitovich.yura.rideservice.exception.KafkaSendingException
import io.voitovich.yura.rideservice.exception.NoSuchRecordException
import io.voitovich.yura.rideservice.repository.RideRepository
import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import java.util.*


@Service
class KafkaProducerServiceImpl(
    val template: KafkaTemplate<String, SendRatingModel>,
    val repository: RideRepository
) : KafkaProducerService {

    private val log = KotlinLogging.logger { }

    private var topicName = "driver_rating_topic"

    override fun ratePassenger(request: SendRatingRequest) {

        val ride = getIfRidePresent(request.rideId)
        val model = SendRatingModel(
            ride.driverProfileId!!,
            ride.passengerProfileId,
            request.rating
        )

        val future = template.send(topicName, "driver", model)
        future.whenComplete { result: SendResult<String, SendRatingModel?>, ex: Throwable? ->
            if (ex == null) {
                log.info {
                    "Sent message=[$model] with offset=[${result.recordMetadata.offset()}]"
                }
            } else {
                throw KafkaSendingException("Unable to send message=[$model]", ex)
            }
        }
    }
    private fun getIfRidePresent(id: UUID) : Ride {
        val rideOptional = repository.findById(id)
        return rideOptional.orElseThrow { NoSuchRecordException(String
            .format("Ride with id: {%s} was not found", id))
        }
    }
}