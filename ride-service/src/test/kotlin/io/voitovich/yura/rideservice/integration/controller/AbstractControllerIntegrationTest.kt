package io.voitovich.yura.rideservice.integration.controller

import io.voitovich.yura.rideservice.integration.config.WireMockConfig
import org.apache.http.client.utils.URIBuilder
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.json.JacksonJsonParser
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgisContainerProvider
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


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
@ContextConfiguration(classes = [WireMockConfig::class])
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractControllerIntegrationTest {

    @LocalServerPort
    val port: Int? = null

    companion object {

        lateinit var adminToken: String
        lateinit var userToken: String
        lateinit var wrongUserToken: String

        @Container
        @JvmStatic
        val kafka = KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest")
        )

        @Container
        @JvmStatic
        val postgres = PostgisContainerProvider()
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

        @JvmStatic
        @BeforeAll
        fun setup() {

            val restTemplate = RestTemplate()

            val authorizationURI =
                URIBuilder("http://localhost:8070/auth/realms/modsen-realm/protocol/openid-connect/token").build()


            val formData: MultiValueMap<String, String> = LinkedMultiValueMap()
            formData["grant_type"] = listOf("password")
            formData["client_id"] = listOf("modsen-client")
            formData["client_secret"] = listOf("Z9tlxniFFMa1iNtaMa29svkdhFpoCojh")
            formData["username"] = listOf("user")
            formData["password"] = listOf("user")

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
            var request = HttpEntity(formData, headers)


            var result = restTemplate.exchange(
                authorizationURI,
                HttpMethod.POST,
                request,
                String::class.java
            ).body

            val jsonParser = JacksonJsonParser()
            userToken = "Bearer " + jsonParser.parseMap(result)["access_token"]
                .toString()

            formData["username"] = listOf("admin")
            formData["password"] = listOf("admin")

            request = HttpEntity(formData, headers)

            result = restTemplate.exchange(
                authorizationURI,
                HttpMethod.POST,
                request,
                String::class.java
            ).body

            adminToken = "Bearer " + jsonParser.parseMap(result)["access_token"]
                .toString()

            formData["username"] = listOf("user2")
            formData["password"] = listOf("user")

            request = HttpEntity(formData, headers)

            result = restTemplate.exchange(
                authorizationURI,
                HttpMethod.POST,
                request,
                String::class.java
            ).body

            wrongUserToken = "Bearer " + jsonParser.parseMap(result)["access_token"]
                .toString()

        }
    }

}