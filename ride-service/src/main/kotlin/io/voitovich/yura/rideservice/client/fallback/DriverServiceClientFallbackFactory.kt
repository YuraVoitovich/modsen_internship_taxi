package io.voitovich.yura.rideservice.client.fallback

import io.voitovich.yura.rideservice.client.DriverServiceClient
import mu.KotlinLogging
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.stereotype.Component

@Component
class DriverServiceClientFallbackFactory(): FallbackFactory<DriverServiceClient> {

    val logger = KotlinLogging.logger {  }

    override fun create(cause: Throwable?): DriverServiceClient {
        logger.info { "handle exception: ${cause!!.message}" }
        throw cause!!
    }
}