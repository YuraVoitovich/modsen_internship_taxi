package io.voitovich.yura.driverservice.security.service.impl;

import io.voitovich.yura.driverservice.exception.DriverProfileAccessDeniedException;
import io.voitovich.yura.driverservice.security.service.SecurityService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
public class SecurityServiceImpl implements SecurityService {
    @Override
    @Transactional()
    public void checkUserAccess(UUID userSub) {
        var token = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var tokenSub = UUID.fromString(token.getClaim("sub"));
        if (!tokenSub.equals(userSub)) {
            throw new DriverProfileAccessDeniedException(String
                    .format("Access denied, stored sub id: %s and given sub id: %s are not equal",
                            userSub, tokenSub));
        }

    }
}
