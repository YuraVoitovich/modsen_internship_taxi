package io.voitovich.yura.rideservice.integration.config

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles


@TestConfiguration
@ActiveProfiles("test")
class WireMockConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    fun mockDriverServiceDriverServiceManagement(): WireMockServer {
        return WireMockServer(80)
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    fun mockPassengerServiceDriverServiceManagement(): WireMockServer {
        return WireMockServer(81)
    }

}