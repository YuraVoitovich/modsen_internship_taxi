package io.voitovich.yura.rideservice.exceptionhandler.model

import org.springframework.http.HttpStatus


data class ExceptionInfo(val code: Int, val status: HttpStatus, val message: String)
