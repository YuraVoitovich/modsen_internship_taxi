package io.voitovich.yura.rideservice.client.config

import feign.codec.ErrorDecoder
import io.voitovich.yura.rideservice.client.errordecoder.DriverServiceClientErrorDecoder
import org.springframework.context.annotation.Bean

class DriverServiceClientConfig {

    @Bean
    fun errorDecoder(): ErrorDecoder {
        return DriverServiceClientErrorDecoder()
    }
}