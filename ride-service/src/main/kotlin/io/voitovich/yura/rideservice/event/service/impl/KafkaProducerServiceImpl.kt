package io.voitovich.yura.rideservice.event.service.impl

import io.voitovich.yura.rideservice.dto.request.SendRatingRequest
import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.event.service.KafkaProducerService
import io.voitovich.yura.rideservice.event.model.SendRatingModel
import io.voitovich.yura.rideservice.event.service.KafkaChannelGateway
import io.voitovich.yura.rideservice.exception.NoSuchRecordException
import io.voitovich.yura.rideservice.repository.RideRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*


@Service
class KafkaProducerServiceImpl(
    val channelGateway: KafkaChannelGateway,
    val repository: RideRepository
) : KafkaProducerService {

    private val log = KotlinLogging.logger { }

    override fun rateDriver(model: SendRatingModel) {
        log.info { "handle rate driver request with model: $model" }


        channelGateway.handleRateDriverRequest(model);
    }

    override fun ratePassenger(model: SendRatingModel) {
        log.info { "handle rate passenger request with model: $model" }

        channelGateway.handleRatePassengerRequest(model);

//        val future = template.send(topicName, "driver", model)
//        future.whenComplete { result: SendResult<String, SendRatingModel?>, ex: Throwable? ->
//            if (ex == null) {
//                log.info {
//                    "Sent message=[$model] with offset=[${result.recordMetadata.offset()}]"
//                }
//            } else {
//                throw KafkaSendingException("Unable to send message=[$model]", ex)
//            }
//        }
    }
    private fun getIfRidePresent(id: UUID) : Ride {
        val rideOptional = repository.findById(id)
        return rideOptional.orElseThrow { NoSuchRecordException(String
            .format("Ride with id: {%s} was not found", id))
        }
    }


}