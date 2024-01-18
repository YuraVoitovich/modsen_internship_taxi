package io.voitovich.yura.rideservice.integration.util

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class Utils {
    companion object {
        fun setupPassengerWireMock(passengerWireMock: WireMockServer, passengerId: String, responseStatus: HttpStatus, responseBody: String? = null) {
            passengerWireMock.stubFor(
                WireMock.get("/api/passenger/profile/$passengerId")
                    .willReturn(
                        WireMock.aResponse()
                            .withStatus(responseStatus.value())
                            .apply {
                                responseBody?.let {
                                    withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                    withBody(responseBody)
                                }
                            }
                    )
            )
        }

        fun setupPassengersWireMock(
            passengerWireMock: WireMockServer,
            passengerIds: List<String>,
            responseStatus: HttpStatus,
            responseBody: String? = null
        ) {
            passengerWireMock.stubFor(
                WireMock.get("/api/passenger/profiles/${passengerIds.joinToString(",")}")
                    .willReturn(
                        WireMock.aResponse()
                            .withStatus(responseStatus.value())
                            .apply {
                                responseBody?.let {
                                    withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                    withBody(responseBody)
                                }
                            }
                    )
            )
        }

        fun setupDriverWireMock(driverWireMock: WireMockServer, id: String, responseStatus: HttpStatus, responseBody: String? = null) {
            driverWireMock.stubFor(
                WireMock.get("/api/driver/profile/$id")
                    .willReturn(
                        WireMock.aResponse()
                            .withStatus(responseStatus.value())
                            .apply {
                                responseBody?.let {
                                    withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                    withBody(responseBody)
                                }
                            }
                    )
            )
        }

        fun setupDriversWireMock(
            driverWireMock: WireMockServer,
            ids: List<String>,
            responseStatus: HttpStatus,
            responseBody: String? = null
        ) {
            driverWireMock.stubFor(
                WireMock.get("/api/driver/profiles/${ids.joinToString(",")}")
                    .willReturn(
                        WireMock.aResponse()
                            .withStatus(responseStatus.value())
                            .apply {
                                responseBody?.let {
                                    withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                    withBody(responseBody)
                                }
                            }
                    )
            )
        }


        fun givenRequest(port: Int,
                         pathParam: String? = null,
                         pathParamName: String? = null,
                         params: Map<String, *>? = null): RequestSpecification {
            val temp =  RestAssured.given()
                .contentType(ContentType.JSON)
                .port(port)
            if (pathParam != null && pathParamName != null) {
                temp.pathParam(pathParamName, pathParam)
            }
            if (params != null) {
                temp.params(params)
            }
            return temp
        }

        fun RequestSpecification.whenRequest(url: String, method: HttpMethod): ValidatableResponse {

            return when (method) {
                HttpMethod.PUT -> `when`()
                    .put(url)
                    .then()
                HttpMethod.DELETE -> `when`()
                    .delete(url)
                    .then()
                HttpMethod.GET -> `when`()
                    .get(url)
                    .then()
                HttpMethod.POST -> `when`()
                    .post(url)
                    .then()

                else -> { throw NoSuchElementException()
                }
            }
        }

        fun ValidatableResponse.thenExpectStatus(status: HttpStatus): ValidatableResponse {
            return this.statusCode(status.value())
        }

        fun <T> ValidatableResponse.thenExtractAs(clazz: Class<T>): T {
            return this.extract().`as`(clazz)
        }

        inline fun <reified T> executeRequest(
            port: Int?,
            url: String,
            method: HttpMethod,
            body: Any? = null,
            expectedStatus: HttpStatus,
            extractClass: Class<T>,
            pathParam: String? = null,
            pathParamName: String? = null,
            params: Map<String, *>? = null,
        ): T {
            val requestSpecification = if (body != null) {
                givenRequest(
                    port = port!!,
                    pathParam = pathParam,
                    pathParamName = pathParamName,
                    params = params).body(body)
            } else {
                givenRequest(
                    port = port!!,
                    pathParam = pathParam,
                    pathParamName = pathParamName,
                    params = params)
            }

            return requestSpecification
                .whenRequest(url, method)
                .thenExpectStatus(expectedStatus)
                .thenExtractAs(extractClass)
        }

        fun executeRequest(
            port: Int?,
            url: String,
            method: HttpMethod,
            body: Any? = null,
            expectedStatus: HttpStatus,
            pathParam: String? = null,
            pathParamName: String? = null,
            params: Map<String, *>? = null,
        ) {
            val requestSpecification = if (body != null) {
                givenRequest(
                    port = port!!,
                    pathParam = pathParam,
                    pathParamName = pathParamName,
                    params = params).body(body)
            } else {
                givenRequest(
                    port = port!!,
                    pathParam = pathParam,
                    pathParamName = pathParamName,
                    params = params)
            }

            requestSpecification
                .whenRequest(url, method)
                .thenExpectStatus(expectedStatus)
        }
    }
}