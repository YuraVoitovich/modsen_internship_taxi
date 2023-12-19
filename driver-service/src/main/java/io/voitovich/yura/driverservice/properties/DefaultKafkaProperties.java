package io.voitovich.yura.driverservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "default.kafka")
@Component
@Data
public class DefaultKafkaProperties{
    private String confirmRatingReceiveTopicName;
    private String consumeRatingTopicName;
}
