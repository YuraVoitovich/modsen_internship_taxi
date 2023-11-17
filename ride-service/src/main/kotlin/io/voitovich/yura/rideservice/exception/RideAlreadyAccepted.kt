package io.voitovich.yura.rideservice.exception

class RideAlreadyAccepted : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}