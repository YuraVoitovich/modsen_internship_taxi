package io.voitovich.yura.rideservice.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfiguration {
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI().info(
            Info()
                .title("Ride service api")
                .description("All operations related to handling rides:\n" +
                        "\n" +
                        "creation, acceptance, cancellation, updating user and driver positions")
                .version("1.0")
        )
    }
}