package io.voitovich.yura.rideservice.exception

class RideAlreadyAcceptedException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}