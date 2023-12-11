package io.voitovich.yura.rideservice.client.config

import feign.codec.ErrorDecoder
import io.voitovich.yura.rideservice.client.errordecoder.PassengerServiceClientErrorDecoder
import org.springframework.context.annotation.Bean

class PassengerServiceClientConfig {

    @Bean
    fun errorDecoder(): ErrorDecoder {
        return PassengerServiceClientErrorDecoder()
    }
}