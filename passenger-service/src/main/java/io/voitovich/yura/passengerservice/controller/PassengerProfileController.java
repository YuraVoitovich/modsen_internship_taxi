package io.voitovich.yura.passengerservice.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import io.voitovich.yura.passengerservice.dto.request.PassengerProfilePageRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerProfileUpdateRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerSaveProfileRequest;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfilePageResponse;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfileResponse;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfilesResponse;
import io.voitovich.yura.passengerservice.service.PassengerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.voitovich.yura.passengerservice.controller.utils.UUIDUtils.getUUIDFromString;

@RestController
@Tag(name = "Passenger profile controller", description = "Passenger profile API")
@RequestMapping("api/passenger")
@RequiredArgsConstructor
public class PassengerProfileController implements PassengerProfile{
    private final PassengerProfileService profileService;

    @PreAuthorize("hasRole('ROLE_modsen-admin')")
    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public PassengerProfilePageResponse getProfilePage(
                                                @RequestParam(name = "pageNumber") int pageNumber,
                                                @RequestParam(name = "pageSize") int pageSize,
                                                @RequestParam(name = "orderBy") String orderBy) {
        return profileService.getProfilePage(PassengerProfilePageRequest
                .builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .orderBy(orderBy)
                .build());
    }

    @GetMapping("/profile/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    @PostAuthorize("returnObject.id() == authentication.principal.claims['sub']")
    public PassengerProfileResponse getProfileById(@PathVariable(name = "id") String id) {
        return profileService.getProfileById(getUUIDFromString(id));
    }

    @PostMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    public PassengerProfileResponse updateProfile(@Valid @RequestBody PassengerProfileUpdateRequest request) {
        return profileService.updateProfile(request);
    }

    @PutMapping("/profile")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_modsen-admin')")
    public PassengerProfileResponse saveProfile(@Valid @RequestBody PassengerSaveProfileRequest passengerSaveProfileRequest) {
        return profileService.saveProfile(passengerSaveProfileRequest);
    }

    @DeleteMapping("/profile/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    public void deleteProfile(@PathVariable(name = "id") String id) {
        profileService.deleteProfile(getUUIDFromString(id));
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/profiles/{ids}")
    @PreAuthorize("hasRole('ROLE_modsen-admin')")
    public PassengerProfilesResponse getByIds(@PathVariable(name = "ids") List<UUID> uuids) {
        return profileService.getByIds(uuids);
    }
}
