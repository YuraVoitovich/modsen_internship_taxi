package io.voitovich.yura.rideservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class ApplicationConfig {

    @Bean
    fun clock(): Clock {
        // Customize the clock based on your needs, for example, use a fixed time for testing
        return Clock.systemDefaultZone()
    }
}