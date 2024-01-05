package io.voitovich.yura.passengerservice.integration.controller;


import io.restassured.http.ContentType;
import io.voitovich.yura.passengerservice.dto.request.PassengerProfileUpdateRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerSaveProfileRequest;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfilePageResponse;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfileResponse;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfilesResponse;
import io.voitovich.yura.passengerservice.exceptionhandler.model.ExceptionInfo;
import io.voitovich.yura.passengerservice.exceptionhandler.model.ValidationExceptionInfo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@SqlGroup({
        @Sql(scripts = {
                "classpath:sql/truncate-passenger-profile-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
        @Sql(scripts = {
                "classpath:sql/insert-test-values-in-passenger-profile-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        }
)
@ActiveProfiles("test")
public class PassengerServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");


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
    void getProfileById_profileExists_shouldReturnPassengerProfileResponse() {

        var expected = PassengerProfileResponse.builder()
                .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                .name("John")
                .surname("Doe")
                .rating(BigDecimal.valueOf(5.0))
                .phoneNumber("+375295432551")
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .pathParam("id", "4ba65be8-cd97-4d40-aeae-8eb5a71fa58c")
                .when()
                .get(PASSENGER_SERVICE_BASE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerProfileResponse.class);

        assertEquals(expected, actual);

    }

    @Test
    void getProfileById_profileNotExists_shouldReturnNoSuchRecordErrorResponse() {

        var expected = ExceptionInfo.builder()
                .message("")
                .status(HttpStatus.NOT_FOUND)
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .pathParam("id", UUID.randomUUID().toString())
                .when()
                .get(PASSENGER_SERVICE_BASE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionInfo.class);

        assertEquals(expected.status(), actual.status());

    }


    @Test
    void updateProfile_profileCanBeUpdated_shouldReturnUpdatedPassengerProfileResponse() {

        var updateRequest = PassengerProfileUpdateRequest
                .builder()
                .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                .name("Joe")
                .surname("Fanski")
                .phoneNumber("+375295432555")
                .build();
        var expected = PassengerProfileResponse.builder()
                .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                .name("Joe")
                .surname("Fanski")
                .rating(BigDecimal.valueOf(5.0))
                .phoneNumber("+375295432555")
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .body(updateRequest)
                .when()
                .post(PASSENGER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerProfileResponse.class);

        assertEquals(expected, actual);

    }


    @Test
    void updateProfile_profileNotExists_shouldReturnNoSuchRecordErrorResponse() {

        var updateRequest = PassengerProfileUpdateRequest
                .builder()
                .id(UUID.randomUUID())
                .name("Joe")
                .surname("Fanski")
                .phoneNumber("+375295432555")
                .build();
        var expected = ExceptionInfo.builder()
                .message("")
                .status(HttpStatus.NOT_FOUND)
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .body(updateRequest)
                .when()
                .post(PASSENGER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionInfo.class);

        assertEquals(expected.status(), actual.status());

    }

    @Test
    void updateProfile_profileWithThisPhoneNumberExists_shouldReturnNotUniquePhoneErrorResponse() {

        var updateRequest = PassengerProfileUpdateRequest
                .builder()
                .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                .name("Joe")
                .surname("Fanski")
                .phoneNumber("+375295432552")
                .build();
        var expected = ExceptionInfo.builder()
                .message("")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .body(updateRequest)
                .when()
                .post(PASSENGER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionInfo.class);

        assertEquals(expected.status(), actual.status());

    }

    @Test
    void updateProfile_badPassengerProfileUpdateRequest_shouldReturnValidationErrorResponse() {

        var updateRequest = PassengerProfileUpdateRequest
                .builder()
                .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                .name("")
                .surname("")
                .phoneNumber("+214")
                .build();
        var expected = ValidationExceptionInfo.builder()
                .error("name", "")
                .error("phoneNumber", "")
                .error("surname", "")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .body(updateRequest)
                .when()
                .post(PASSENGER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationExceptionInfo.class);

        expected.errors().keySet()
                .forEach((key) -> assertTrue(actual.errors().containsKey(key)));
        assertEquals(expected.status(), actual.status());
    }


    @Test
    void saveProfile_badPassengerProfileCreateRequest_shouldReturnValidationErrorResponse() {

        var saveRequest = PassengerSaveProfileRequest
                .builder()
                .name("")
                .surname("")
                .phoneNumber("+214")
                .build();
        var expected = ValidationExceptionInfo.builder()
                .error("name", "")
                .error("phoneNumber", "")
                .error("surname", "")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .body(saveRequest)
                .when()
                .put(PASSENGER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationExceptionInfo.class);

        expected.errors().keySet()
                .forEach((key) -> assertTrue(actual.errors().containsKey(key)));
        assertEquals(expected.status(), actual.status());
    }

    @Test
    void saveProfile_profileWithThisPhoneNumberExists_shouldReturnNotUniquePhoneErrorResponse() {

        var saveRequest = PassengerSaveProfileRequest
                .builder()
                .name("Joe")
                .surname("Fanski")
                .phoneNumber("+375295432552")
                .build();
        var expected = ExceptionInfo.builder()
                .message("")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .body(saveRequest)
                .when()
                .put(PASSENGER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionInfo.class);

        assertEquals(expected.status(), actual.status());

    }

    @Test
    void saveProfile_profileCanBeSaved_shouldReturnSavedPassengerProfileResponse() {

        var saveRequest = PassengerSaveProfileRequest
                .builder()
                .name("Joe")
                .surname("Fanski")
                .phoneNumber("+375295432555")
                .build();
        var expected = PassengerProfileResponse.builder()
                .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                .name("Joe")
                .surname("Fanski")
                .rating(BigDecimal.valueOf(5))
                .phoneNumber("+375295432555")
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .body(saveRequest)
                .when()
                .put(PASSENGER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(PassengerProfileResponse.class);

        assertEquals(expected.surname(), actual.surname());
        assertEquals(expected.rating(), actual.rating());
        assertEquals(expected.phoneNumber(), actual.phoneNumber());
        assertEquals(expected.name(), actual.name());

    }

    @Test
    void deleteProfileById_profileExists_shouldDeleteProfile() {

        given()
                .contentType(ContentType.JSON)
                .port(port)
                .pathParam("id", "4ba65be8-cd97-4d40-aeae-8eb5a71fa58c")
                .when()
                .delete(PASSENGER_SERVICE_BASE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

    }

    @Test
    void deleteProfileById_profileNotExists_shouldReturnNoSuchRecordErrorResponse() {

        var expected = ExceptionInfo.builder()
                .message("")
                .status(HttpStatus.NOT_FOUND)
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .pathParam("id", UUID.randomUUID().toString())
                .when()
                .delete(PASSENGER_SERVICE_BASE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionInfo.class);

        assertEquals(expected.status(), actual.status());

    }


    @Test
    void getProfilePage_badParams_shouldReturnConstraintViolationErrorResponse() {

        var expected = ExceptionInfo.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("")
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .params(Map.of(
                        "pageNumber", 0,
                        "pageSize", 0,
                        "orderBy", "ids"))
                .when()
                .get(PASSENGER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionInfo.class);

        assertEquals(expected.status(), actual.status());
    }

    @Test
    void getProfilePage_correctParams_shouldReturnPageResponse() {

        var expected = PassengerProfilePageResponse
                .builder()
                .profiles(getDefaultSortedPassengerProfilesResponseList())
                .pageNumber(1)
                .totalPages(2)
                .totalElements(3)
                .build();

        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .params(Map.of(
                        "pageNumber", 1,
                        "pageSize", 2,
                        "orderBy", "phoneNumber"))
                .when()
                .get(PASSENGER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerProfilePageResponse.class);

        assertEquals(expected, actual);
    }

    @Test
    void getByIds_correctIds_shouldReturnDriverProfiles() {
        var profiles = getDefaultSortedPassengerProfilesResponseList();
        var ids = profiles.stream().map(PassengerProfileResponse::id).toList();
        var expected = PassengerProfilesResponse.builder()
                .profiles(profiles)
                .build();
        var actual = given().contentType(ContentType.JSON)
                .port(port)
                .pathParam("ids", String.join(",", ids.stream().map(UUID::toString).toList()))
                .when()
                .get("api/passenger/profiles/{ids}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerProfilesResponse.class);

        assertEquals(expected, actual);
    }

    private List<PassengerProfileResponse> getDefaultSortedPassengerProfilesResponseList() {
        var first = PassengerProfileResponse.builder()
                .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                .name("John")
                .surname("Doe")
                .rating(BigDecimal.valueOf(5.0))
                .phoneNumber("+375295432551")
                .build();
        var second = PassengerProfileResponse.builder()
                .id(UUID.fromString("025fe6d1-8363-4a1a-925d-d91a8b640b8f"))
                .name("Jane")
                .surname("Smith")
                .rating(BigDecimal.valueOf(5.0))
                .phoneNumber("+375295432552")
                .build();
        return List.of(first, second);
    }

}
