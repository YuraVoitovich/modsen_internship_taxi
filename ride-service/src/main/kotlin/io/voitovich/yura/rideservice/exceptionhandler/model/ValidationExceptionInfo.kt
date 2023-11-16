package io.voitovich.yura.rideservice.exceptionhandler.model

import org.springframework.http.HttpStatus


data class ValidationExceptionInfo(
    val code: Int,
    val status: HttpStatus,
    val errors: Map<String, String>
)

