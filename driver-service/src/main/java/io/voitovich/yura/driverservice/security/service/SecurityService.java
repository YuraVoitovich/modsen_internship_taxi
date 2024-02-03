package io.voitovich.yura.driverservice.security.service;

import java.util.UUID;


public interface SecurityService {

    void checkUserAccess(UUID userSub);

}
