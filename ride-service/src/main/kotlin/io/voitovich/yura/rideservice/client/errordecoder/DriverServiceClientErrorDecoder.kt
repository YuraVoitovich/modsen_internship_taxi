package io.voitovich.yura.rideservice.client.errordecoder

import feign.Response
import feign.codec.ErrorDecoder
import io.voitovich.yura.rideservice.exception.NoSuchRecordException
import org.springframework.http.HttpStatus


class DriverServiceClientErrorDecoder : ErrorDecoder {
    override fun decode(methodKey: String?, response: Response?): Exception {
        if (response!!.status() == HttpStatus.NOT_FOUND.value()) {
            return NoSuchRecordException("Driver profile was not found")
        }
        return Exception()
    }
}