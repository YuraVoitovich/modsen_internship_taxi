package io.voitovich.yura.rideservice.security.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.util.stream.Collectors

class JwtAuthConverter : Converter<Jwt, AbstractAuthenticationToken> {

    override fun convert(source: Jwt): AbstractAuthenticationToken {
        val claims = source.claims
        val grantedAuthority = getGrantedAuthority(source)
        return JwtAuthenticationToken(source, grantedAuthority)
    }

    private fun getGrantedAuthority(source: Jwt): Collection<GrantedAuthority> {
        val realmAccess = source.getClaimAsMap("realm_access")
        val roles = realmAccess["roles"] as List<String>?
        return roles!!.stream()
            .map { o: String -> "ROLE_$o" }
            .map { role: String? -> SimpleGrantedAuthority(role) }
            .collect(Collectors.toList())
    }
}