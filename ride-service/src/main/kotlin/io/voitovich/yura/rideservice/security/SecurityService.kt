package io.voitovich.yura.rideservice.security

import java.util.*

interface SecurityService {
    fun checkUserAccess(userSub: UUID)
}
