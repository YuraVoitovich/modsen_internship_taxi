package io.voitovich.yura.rideservice.security

import io.voitovich.yura.rideservice.exception.PassengerProfileAccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.util.*

@Service
class SecurityServiceImpl : SecurityService {

    companion object {
        const val PASSENGER_PROFILE_ACCESS_DENIED_EXCEPTION_MESSAGE = "Access denied, stored sub id: %s and given sub id: %s are not equal"
    }

    override fun checkUserAccess(userSub: UUID) {
        val token = SecurityContextHolder.getContext().authentication.principal as Jwt
        val tokenSub = UUID.fromString(token.getClaim("sub"))
        if (tokenSub != userSub) {
            throw PassengerProfileAccessDeniedException(
                PASSENGER_PROFILE_ACCESS_DENIED_EXCEPTION_MESSAGE.format(
                    userSub, tokenSub
                )
            )
        }
    }
}
