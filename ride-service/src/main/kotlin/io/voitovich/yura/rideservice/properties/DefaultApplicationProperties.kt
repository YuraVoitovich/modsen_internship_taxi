package io.voitovich.yura.rideservice.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "default")
@Component
class DefaultApplicationProperties {
    var searchRadius: Int = 500
    var maxRadius: Int = 1000
    var minRadius: Int = 100
    var useDefaultRadiusIfRadiusNotInRange: Boolean = false
    var allowedRatingTimeInHours: Long = 1
}