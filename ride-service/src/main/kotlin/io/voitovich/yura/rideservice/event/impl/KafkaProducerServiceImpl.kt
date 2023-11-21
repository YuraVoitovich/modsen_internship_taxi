package io.voitovich.yura.rideservice.event.impl

import io.voitovich.yura.rideservice.event.KafkaProducerService
import io.voitovich.yura.rideservice.event.model.SendRatingModel
import io.voitovich.yura.rideservice.exception.KafkaSendingException
import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducerServiceImpl(val template: KafkaTemplate<String, SendRatingModel>) : KafkaProducerService {

    private val log = KotlinLogging.logger {  }

    private var topicName = "driver_rating_topic"

    override fun sendRating(model: SendRatingModel) {

        try {
            template.send(topicName, model.raterId.toString(), model)
            log.info(String.format("Sending model: %s to topic: %s", model.toString(), topicName))
        } catch (e: Exception) {
            log.error("Error while sending model: {} to topic: {}", model, topicName, e)
            throw KafkaSendingException(String
                .format("Error while sending model: %s to kafka", model.toString()), e)
        }

    }
}