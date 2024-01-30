package io.voitovich.yura.passengerservice.security.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        Map<String, Object> claims = source.getClaims();
        Collection<GrantedAuthority> grantedAuthority = getGrantedAuthority(source);
        return new JwtAuthenticationToken(source, grantedAuthority);
    }

    public Collection<GrantedAuthority> getGrantedAuthority(Jwt source) {
        Map<String, Object> realmAccess = source.getClaimAsMap("realm_access");
        List<String> roles = (List<String>) realmAccess.get("roles");

        return roles.stream()
                .map(o -> "ROLE_" + o)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}