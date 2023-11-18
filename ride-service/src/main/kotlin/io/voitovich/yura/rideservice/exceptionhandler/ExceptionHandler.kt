package io.voitovich.yura.rideservice.exceptionhandler

import io.voitovich.yura.rideservice.exception.*
import io.voitovich.yura.rideservice.exceptionhandler.model.ExceptionInfo
import io.voitovich.yura.rideservice.exceptionhandler.model.ValidationExceptionInfo
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class DriverProfileExceptionHandler : ResponseEntityExceptionHandler() {


    private val log = KotlinLogging.logger { }

    @ExceptionHandler(NoSuchRecordException::class)
    fun handleNoSuchRecordException(exception: NoSuchRecordException): ResponseEntity<ExceptionInfo> {
        log.info(String.format("Handled exception - %s", exception), exception)
        val info = ExceptionInfo(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND,
            exception.message!!)
        return ResponseEntity(
            info,
            HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler(RideStartConfirmationException::class)
    fun handleRideStartConfirmationException(exception: RideStartConfirmationException): ResponseEntity<ExceptionInfo> {
        log.info(String.format("Handled exception - %s", exception), exception)
        val info = ExceptionInfo(
            HttpStatus.FORBIDDEN.value(),
            HttpStatus.FORBIDDEN,
            exception.message!!)
        return ResponseEntity(
            info,
            HttpStatus.FORBIDDEN
        )
    }

    @ExceptionHandler(RideAlreadyPresented::class)
    fun handleRideAlreadyPresentedException(exception: RideAlreadyPresented): ResponseEntity<ExceptionInfo> {
        log.info(String.format("Handled exception - %s", exception), exception)
        val info = ExceptionInfo(
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT,
            exception.message!!)

        return ResponseEntity(
            info,
            HttpStatus.CONFLICT
        )
    }

    @ExceptionHandler(RideAlreadyAccepted::class)
    fun handleRideAlreadyAcceptedException(exception: RideAlreadyAccepted): ResponseEntity<ExceptionInfo> {
        log.info(String.format("Handled exception - %s", exception), exception)
        val info = ExceptionInfo(
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT,
            exception.message!!)

        return ResponseEntity(
            info,
            HttpStatus.CONFLICT
        )
    }

    @ExceptionHandler(RideAlreadyCanceled::class)
    fun handleRideAlreadyCanceledException(exception: RideAlreadyCanceled): ResponseEntity<ExceptionInfo> {
        log.info(String.format("Handled exception - %s", exception), exception)
        val info = ExceptionInfo(
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT,
            exception.message!!)

        return ResponseEntity(
            info,
            HttpStatus.CONFLICT
        )
    }

    override fun handleMethodArgumentNotValid(
        exception: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        log.info(String.format("Handled exception - %s", exception), exception)
        val errs = mutableMapOf<String, String?>();
        exception.bindingResult
            .allErrors
            .map { error: ObjectError ->
                errs[(error as FieldError).field] = error.defaultMessage
            }

        val info =
           ValidationExceptionInfo(
               HttpStatus.BAD_REQUEST.value(),
               HttpStatus.BAD_REQUEST,
               errs,
           )

        return ResponseEntity.badRequest().body(info)
    }
}
