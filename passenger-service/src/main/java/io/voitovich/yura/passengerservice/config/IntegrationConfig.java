package io.voitovich.yura.passengerservice.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.integration.channel.interceptor.ObservationPropagationChannelInterceptor;
import org.springframework.integration.config.EnableIntegrationManagement;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptor;

@Configuration
@EnableIntegrationManagement(observationPatterns = "*")
public class IntegrationConfig {

    @Bean
    @GlobalChannelInterceptor(order = Ordered.HIGHEST_PRECEDENCE)
    public ChannelInterceptor observationPropagationChannelInterceptor(ObservationRegistry observationRegistry) {
        return new ObservationPropagationChannelInterceptor(observationRegistry);
    }
}