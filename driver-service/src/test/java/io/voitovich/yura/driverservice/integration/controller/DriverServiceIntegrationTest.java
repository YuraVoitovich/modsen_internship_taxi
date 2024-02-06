package io.voitovich.yura.driverservice.integration.controller;


import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.voitovich.yura.driverservice.dto.request.DriverProfileSaveRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfileUpdateRequest;
import io.voitovich.yura.driverservice.dto.response.DriverProfilePageResponse;
import io.voitovich.yura.driverservice.dto.response.DriverProfileResponse;
import io.voitovich.yura.driverservice.dto.response.DriverProfilesResponse;
import io.voitovich.yura.driverservice.exceptionhandler.model.ExceptionInfo;
import io.voitovich.yura.driverservice.exceptionhandler.model.ValidationExceptionInfo;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
@ActiveProfiles("test")
public class DriverServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @LocalServerPort
    private Integer port;

    private final String DRIVER_SERVICE_BASE_URL = "api/driver/profile";

    @Container
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest")
    );

    private static String userToken;

    private static String adminToken;

    private static String wrongUserToken;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        propertyRegistry.add("spring.datasource.url", postgres::getJdbcUrl);
        propertyRegistry.add("spring.datasource.username", postgres::getUsername);
        propertyRegistry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void setup() throws URISyntaxException {
        URI authorizationURI = new URIBuilder("http://localhost:8070/auth/realms/modsen-realm/protocol/openid-connect/token").build();
        WebClient webclient = WebClient.builder().build();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.put("grant_type", Collections.singletonList("password"));
        formData.put("client_id", Collections.singletonList("modsen-client"));
        formData.put("client_secret", Collections.singletonList("Z9tlxniFFMa1iNtaMa29svkdhFpoCojh"));
        formData.put("username", Collections.singletonList("user"));
        formData.put("password", Collections.singletonList("user"));
        String result = webclient.post()
                .uri(authorizationURI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        userToken = "Bearer " + jsonParser.parseMap(result)
                .get("access_token")
                .toString();

        formData.put("username", Collections.singletonList("admin"));
        formData.put("password", Collections.singletonList("admin"));

        result = webclient.post()
                .uri(authorizationURI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        adminToken = "Bearer " + jsonParser.parseMap(result)
                .get("access_token")
                .toString();

        formData.put("username", Collections.singletonList("user2"));
        formData.put("password", Collections.singletonList("user"));

        result = webclient.post()
                .uri(authorizationURI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        wrongUserToken = "Bearer " + jsonParser.parseMap(result)
                .get("access_token")
                .toString();

    }

    @Nested
    @DisplayName("Test get profile by id")
    public class GetProfileById {
        @Test
        void getProfileById_profileExists_shouldReturnPassengerProfileResponse() {

            var expected = DriverProfileResponse.builder()
                    .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                    .name("John")
                    .experience(3)
                    .surname("Doe")
                    .rating(BigDecimal.valueOf(5.0))
                    .phoneNumber("+375295432551")
                    .build();
            var actual = getUserAuthorizedGivenRequest()
                    .pathParam("id", "4ba65be8-cd97-4d40-aeae-8eb5a71fa58c")
                    .when()
                    .get(DRIVER_SERVICE_BASE_URL + "/{id}")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(DriverProfileResponse.class);

            assertEquals(expected, actual);

        }

        @Test
        void getProfileById_wrongUserSubId_shouldReturnDriverProfileAccessDeniedException() {
            var expected = HttpStatus.FORBIDDEN;

            var actual = getWrongUserAuthorizedGivenRequest()
                    .pathParam("id", "4ba65be8-cd97-4d40-aeae-8eb5a71fa58c")
                    .when()
                    .get(DRIVER_SERVICE_BASE_URL + "/{id}")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .extract()
                    .as(ExceptionInfo.class);

            assertThat(actual.status()).isEqualTo(expected);
        }

        @Test
        void getProfileById_profileNotExists_shouldReturnNoSuchRecordErrorResponse() {

            var expected = ExceptionInfo.builder()
                    .message("")
                    .status(HttpStatus.NOT_FOUND)
                    .build();
            var actual = getUserAuthorizedGivenRequest()
                    .pathParam("id", UUID.randomUUID().toString())
                    .when()
                    .get(DRIVER_SERVICE_BASE_URL + "/{id}")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .extract()
                    .as(ExceptionInfo.class);

            assertEquals(expected.status(), actual.status());

        }

    }

    @Nested
    @DisplayName("Test update profile")
    public class UpdateProfile {
        @Test
        void updateProfile_profileCanBeUpdated_shouldReturnUpdatedPassengerProfileResponse() {

            var updateRequest = DriverProfileUpdateRequest
                    .builder()
                    .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                    .name("Joe")
                    .experience(7)
                    .surname("Fanski")
                    .phoneNumber("+375295432555")
                    .build();
            var expected = DriverProfileResponse.builder()
                    .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                    .name("Joe")
                    .experience(7)
                    .surname("Fanski")
                    .rating(BigDecimal.valueOf(5.0))
                    .phoneNumber("+375295432555")
                    .build();
            var actual = getUserAuthorizedGivenRequest()
                    .body(updateRequest)
                    .when()
                    .post(DRIVER_SERVICE_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(DriverProfileResponse.class);

            assertEquals(expected, actual);

        }


        @Test
        void updateProfile_profileNotExists_shouldReturnNoSuchRecordErrorResponse() {

            var updateRequest = DriverProfileUpdateRequest
                    .builder()
                    .id(UUID.randomUUID())
                    .name("Joe")
                    .experience(4)
                    .surname("Fanski")
                    .phoneNumber("+375295432555")
                    .build();
            var expected = ExceptionInfo.builder()
                    .message("")
                    .status(HttpStatus.NOT_FOUND)
                    .build();
            var actual = getUserAuthorizedGivenRequest()
                    .body(updateRequest)
                    .when()
                    .post(DRIVER_SERVICE_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .extract()
                    .as(ExceptionInfo.class);

            assertEquals(expected.status(), actual.status());

        }

        @Test
        void updateProfile_profileWithThisPhoneNumberExists_shouldReturnNotUniquePhoneErrorResponse() {

            var updateRequest = DriverProfileUpdateRequest
                    .builder()
                    .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                    .name("Joe")
                    .experience(6)
                    .surname("Fanski")
                    .phoneNumber("+375295432552")
                    .build();
            var expected = ExceptionInfo.builder()
                    .message("")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
            var actual = getUserAuthorizedGivenRequest()
                    .body(updateRequest)
                    .when()
                    .post(DRIVER_SERVICE_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .extract()
                    .as(ExceptionInfo.class);

            assertEquals(expected.status(), actual.status());

        }

        @Test
        void updateProfile_badPassengerProfileUpdateRequest_shouldReturnValidationErrorResponse() {

            var updateRequest = DriverProfileUpdateRequest
                    .builder()
                    .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                    .name("")
                    .surname("")
                    .experience(-1)
                    .phoneNumber("+214")
                    .build();
            var expected = ValidationExceptionInfo.builder()
                    .error("name", "")
                    .error("phoneNumber", "")
                    .error("surname", "")
                    .error("experience", "")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
            var actual = getUserAuthorizedGivenRequest()
                    .body(updateRequest)
                    .when()
                    .post(DRIVER_SERVICE_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .extract()
                    .as(ValidationExceptionInfo.class);

            expected.errors().keySet()
                    .forEach((key) -> assertTrue(actual.errors().containsKey(key)));
            assertEquals(expected.status(), actual.status());
        }

    }

    @Nested
    @DisplayName("Test save profile")
    public class SaveProfile {
        @Test
        void saveProfile_badPassengerProfileCreateRequest_shouldReturnValidationErrorResponse() {

            var saveRequest = DriverProfileSaveRequest
                    .builder()
                    .name("")
                    .experience(-1)
                    .surname("")
                    .phoneNumber("+214")
                    .build();
            var expected = ValidationExceptionInfo.builder()
                    .error("name", "")
                    .error("phoneNumber", "")
                    .error("surname", "")
                    .error("experience", "")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
            var actual = getUserAuthorizedGivenRequest()
                    .body(saveRequest)
                    .when()
                    .put(DRIVER_SERVICE_BASE_URL)
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

            var saveRequest = DriverProfileSaveRequest
                    .builder()
                    .name("Joe")
                    .experience(4)
                    .surname("Fanski")
                    .phoneNumber("+375295432552")
                    .build();
            var expected = ExceptionInfo.builder()
                    .message("")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
            var actual = getUserAuthorizedGivenRequest()
                    .body(saveRequest)
                    .when()
                    .put(DRIVER_SERVICE_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .extract()
                    .as(ExceptionInfo.class);

            assertEquals(expected.status(), actual.status());

        }

        @Test
        void saveProfile_profileCanBeSaved_shouldReturnSavedPassengerProfileResponse() {

            var saveRequest = DriverProfileSaveRequest
                    .builder()
                    .name("Joe")
                    .surname("Fanski")
                    .experience(3)
                    .phoneNumber("+375295432555")
                    .build();
            var expected = DriverProfileResponse.builder()
                    .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                    .name("Joe")
                    .experience(3)
                    .surname("Fanski")
                    .rating(BigDecimal.valueOf(5))
                    .phoneNumber("+375295432555")
                    .build();
            var actual = getUserAuthorizedGivenRequest()
                    .body(saveRequest)
                    .when()
                    .put(DRIVER_SERVICE_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract()
                    .as(DriverProfileResponse.class);

            assertEquals(expected.surname(), actual.surname());
            assertEquals(expected.rating(), actual.rating());
            assertEquals(expected.phoneNumber(), actual.phoneNumber());
            assertEquals(expected.name(), actual.name());
            assertEquals(expected.experience(), actual.experience());

        }
    }

    @Nested
    @DisplayName("Test delete profile by id")
    public class DeleteProfileById {
        @Test
        void deleteProfileById_profileExists_shouldDeleteProfile() {

            getUserAuthorizedGivenRequest()
                    .pathParam("id", "4ba65be8-cd97-4d40-aeae-8eb5a71fa58c")
                    .when()
                    .delete(DRIVER_SERVICE_BASE_URL + "/{id}")
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

        }

        @Test
        void deleteProfileById_profileNotExists_shouldReturnNoSuchRecordErrorResponse() {

            var expected = ExceptionInfo.builder()
                    .message("")
                    .status(HttpStatus.NOT_FOUND)
                    .build();
            var actual = getUserAuthorizedGivenRequest()
                    .pathParam("id", UUID.randomUUID().toString())
                    .when()
                    .delete(DRIVER_SERVICE_BASE_URL + "/{id}")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .extract()
                    .as(ExceptionInfo.class);

            assertEquals(expected.status(), actual.status());

        }
    }

    @Nested
    @DisplayName("Test get profile page")
    public class GetProfilePage {

        @Test
        void getProfilePage_badParams_shouldReturnConstraintViolationErrorResponse() {

            var expected = ExceptionInfo.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("")
                    .build();
            var actual = getAdminAuthorizedGivenRequest()
                    .params(Map.of(
                            "pageNumber", 0,
                            "pageSize", 0,
                            "orderBy", "ids"))
                    .when()
                    .get(DRIVER_SERVICE_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .extract()
                    .as(ExceptionInfo.class);

            assertEquals(expected.status(), actual.status());
        }


        @Test
        void getProfilePage_wrongAuthRole_shouldReturnForbiddenStatus() {
            getUserAuthorizedGivenRequest()
                    .params(Map.of(
                            "pageNumber", 1,
                            "pageSize", 2,
                            "orderBy", "phoneNumber"))
                    .when()
                    .get(DRIVER_SERVICE_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        void getProfilePage_correctParams_shouldReturnPageResponse() {

            var expected = DriverProfilePageResponse
                    .builder()
                    .profiles(getDefaultSortedDriverProfilesResponseList())
                    .pageNumber(1)
                    .totalPages(2)
                    .totalElements(3)
                    .build();

            var actual = getAdminAuthorizedGivenRequest()
                    .params(Map.of(
                            "pageNumber", 1,
                            "pageSize", 2,
                            "orderBy", "phoneNumber"))
                    .when()
                    .get(DRIVER_SERVICE_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(DriverProfilePageResponse.class);

            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("Test get by ids")
    public class GetByIds {
        @Test
        void getByIds_correctIds_shouldReturnDriverProfiles() {
            var profiles = getDefaultSortedDriverProfilesResponseList();
            var ids = profiles.stream().map(DriverProfileResponse::id).toList();
            var expected = DriverProfilesResponse.builder()
                    .profiles(profiles)
                    .build();
            var actual = getAdminAuthorizedGivenRequest()
                    .pathParam("ids", String.join(",", ids.stream().map(UUID::toString).toList()))
                    .when()
                    .get("api/driver/profiles/{ids}")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(DriverProfilesResponse.class);

            assertEquals(expected, actual);
        }
    }

    private List<DriverProfileResponse> getDefaultSortedDriverProfilesResponseList() {
        var first = DriverProfileResponse.builder()
                .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                .name("John")
                .experience(3)
                .surname("Doe")
                .rating(BigDecimal.valueOf(5.0))
                .phoneNumber("+375295432551")
                .build();
        var second = DriverProfileResponse.builder()
                .id(UUID.fromString("025fe6d1-8363-4a1a-925d-d91a8b640b8f"))
                .name("Jane")
                .surname("Smith")
                .experience(4)
                .rating(BigDecimal.valueOf(5.0))
                .phoneNumber("+375295432552")
                .build();
        return List.of(first, second);
    }

    private RequestSpecification getUserAuthorizedGivenRequest() {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", userToken)
                .port(port);
    }

    private RequestSpecification getAdminAuthorizedGivenRequest() {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", adminToken)
                .port(port);
    }

    private RequestSpecification getWrongUserAuthorizedGivenRequest() {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", wrongUserToken)
                .port(port);
    }


}
