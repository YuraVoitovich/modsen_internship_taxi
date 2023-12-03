package io.voitovich.yura.rideservice.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component


@ConfigurationProperties(prefix = "default.kafka")
@Component
class DefaultKafkaProperties {
    var sendRatingToDriverTopicName = ""
    var sendRatingToPassengerTopicName = ""
    var sendRatingToDriverTopicReplicasAssignments: Short = 1
    var sendRatingToDriverTopicNumPartitions = 1
    var sendRatingToPassengerTopicReplicasAssignments: Short = 1
    var sendRatingToPassengerTopicNumPartitions = 1
}