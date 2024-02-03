package io.voitovich.yura.passengerservice.security.service;

import java.util.UUID;


public interface SecurityService {

    void checkUserAccess(UUID userSub);

}
