package io.voitovich.yura.driverservice.integration.kafka;


import io.voitovich.yura.driverservice.event.model.ConfirmRatingReceiveModel;
import io.voitovich.yura.driverservice.event.model.ReceiveRatingModel;
import io.voitovich.yura.driverservice.integration.kafka.config.KafkaConfig;
import io.voitovich.yura.driverservice.properties.DefaultKafkaProperties;
import io.voitovich.yura.driverservice.repository.DriverProfileRepository;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@SqlGroup({
        @Sql(scripts = {
                "classpath:sql/truncate-driver-profile-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
        @Sql(scripts = {
                "classpath:sql/insert-test-values-in-driver-profile-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
}
)
@ContextConfiguration(classes = {KafkaConfig.class})
@ActiveProfiles("test")
public class KafkaIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private KafkaTemplate<String, ReceiveRatingModel> kafkaTemplate;

    @Autowired
    private ConsumerFactory<String, ConfirmRatingReceiveModel> consumerFactory;

    @Autowired
    private DefaultKafkaProperties properties;

    @Autowired
    private DriverProfileRepository driverProfilerepository;


    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @LocalServerPort
    private Integer port;

    private final String PASSENGER_SERVICE_BASE_URL = "api/passenger/profile";

    @Container
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest")
    );

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        propertyRegistry.add("spring.datasource.url", postgres::getJdbcUrl);
        propertyRegistry.add("spring.datasource.username", postgres::getUsername);
        propertyRegistry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void consumerRating_correctRequest_ratingConsumedAndConfirmationSent() {
        var model = ReceiveRatingModel
                .builder()
                .rideId(UUID.randomUUID())
                .rating(BigDecimal.valueOf(4.0))
                .raterId(UUID.randomUUID())
                .ratedId(UUID.fromString("f00a8f6f-9294-4e4e-aa4d-42f801b69a95"))
                .build();
        var expectedConfirmRatingReceiveModel = ConfirmRatingReceiveModel.builder()
                .rating(model.rating())
                .rideId(model.rideId())
                .build();
        kafkaTemplate.send(properties.getConsumeRatingTopicName(), model);
        await().
                atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                            var driverProfile = driverProfilerepository.findById(model.ratedId()).get();
                            assertEquals(BigDecimal.valueOf(4.5), driverProfile.getRating());
                        }
                );

        await().
                atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    var consumer = consumerFactory.createConsumer();
                    consumer.subscribe(Collections.singletonList(properties.getConfirmRatingReceiveTopicName()));
                    ConsumerRecords<String, ConfirmRatingReceiveModel> consumerRecords = consumer.poll(Duration.ofSeconds(5));
                    assertEquals(1, consumerRecords.count());
                    var records = consumerRecords.records(properties.getConfirmRatingReceiveTopicName());
                    records.forEach((val) -> {
                        assertEquals(expectedConfirmRatingReceiveModel, val.value());
                    });
                    consumer.unsubscribe();
                }
                );
    }
}
