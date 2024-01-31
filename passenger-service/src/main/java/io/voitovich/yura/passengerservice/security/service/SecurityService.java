package io.voitovich.yura.passengerservice.security.service;

import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.UUID;


public interface SecurityService {

    void checkUserAccess(UUID userSub);

}
