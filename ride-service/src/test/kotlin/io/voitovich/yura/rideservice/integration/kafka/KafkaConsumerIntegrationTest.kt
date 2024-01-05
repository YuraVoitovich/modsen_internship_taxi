package io.voitovich.yura.rideservice.integration.kafka

import io.voitovich.yura.rideservice.event.model.ConfirmRatingReceiveModel
import io.voitovich.yura.rideservice.integration.kafka.config.KafkaConsumerTestConfig
import io.voitovich.yura.rideservice.properties.DefaultKafkaProperties
import io.voitovich.yura.rideservice.repository.RideRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgisContainerProvider
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.shaded.org.awaitility.Awaitility
import org.testcontainers.utility.DockerImageName
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.TimeUnit


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@SqlGroup(
    Sql(
        scripts = ["classpath:sql/truncate-ride-table.sql"],
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    ),
    Sql(
        scripts = ["classpath:sql/insert-test-values-in-ride-table.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = [KafkaConsumerTestConfig::class])
@ActiveProfiles("test")
class KafkaConsumerIntegrationTest {


    @Autowired
    lateinit var properties: DefaultKafkaProperties

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, ConfirmRatingReceiveModel>

    @Autowired
    lateinit var rideRepository: RideRepository


    companion object {

        @Container
        @JvmStatic
        val kafka = KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest")
        )

        @Container
        @JvmStatic
        private val postgres = PostgisContainerProvider()
            .newInstance()
            .withDatabaseName("test")
            .withUsername("postgres")
            .withPassword("postgres")

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(propertyRegistry: DynamicPropertyRegistry) {
            propertyRegistry.add("spring.kafka.bootstrap-servers") { kafka.bootstrapServers }
            propertyRegistry.add("spring.datasource.url") { postgres.jdbcUrl }
            propertyRegistry.add("spring.datasource.username") { postgres.username }
            propertyRegistry.add("spring.datasource.password") { postgres.password }
        }
    }

    @Test
    fun handleDriverRatingReceiveConfirmation_correctRequest_confirmRatingReceive() {
        val model =
            ConfirmRatingReceiveModel(
                rideId = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa64c"),
                rating = BigDecimal(4.0)
            )

        kafkaTemplate.send(properties.confirmDriverRatingReceiveTopicName, model)
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted {
                val ride = rideRepository.findById(model.rideId).get()
                Assertions.assertEquals(model.rating, ride.driverRating)
            }
    }

    @Test
    fun handlePassengerRatingReceiveConfirmation_correctRequest_confirmRatingReceive() {
        val model =
            ConfirmRatingReceiveModel(
                rideId = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa65c"),
                rating = BigDecimal(4.0)
            )

        kafkaTemplate.send(properties.confirmPassengerRatingReceiveTopicName, model)
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted {
                val ride = rideRepository.findById(model.rideId).get()
                Assertions.assertEquals(model.rating, ride.passengerRating)
            }
    }
}