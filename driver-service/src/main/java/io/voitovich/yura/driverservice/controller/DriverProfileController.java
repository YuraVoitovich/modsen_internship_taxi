package io.voitovich.yura.driverservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.voitovich.yura.driverservice.controller.utils.UUIDUtils;
import io.voitovich.yura.driverservice.dto.request.DriverProfilePageRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfileSaveRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfileUpdateRequest;
import io.voitovich.yura.driverservice.dto.response.DriverProfilePageResponse;
import io.voitovich.yura.driverservice.dto.response.DriverProfileResponse;
import io.voitovich.yura.driverservice.dto.response.DriverProfilesResponse;
import io.voitovich.yura.driverservice.exceptionhandler.model.ExceptionInfo;
import io.voitovich.yura.driverservice.exceptionhandler.model.ValidationExceptionInfo;
import io.voitovich.yura.driverservice.service.DriverProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/driver")
@Tag(name = "Driver profile controller", description = "Driver profile API")
@RequiredArgsConstructor
public class DriverProfileController implements DriverProfile{

    private final DriverProfileService profileService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/profile/{id}")
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    public DriverProfileResponse getProfileById(@Parameter(name = "id", description = "Driver profile UUID")
                                                     @PathVariable(name = "id") String id) {
        return profileService.getProfileById(UUIDUtils.getUUIDFromString(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/profile")
    @PreAuthorize("hasRole('ROLE_modsen-admin')")
    public DriverProfilePageResponse getProfilePage(@RequestParam(name = "pageNumber") int pageNumber,
                                                     @RequestParam(name = "pageSize") int pageSize,
                                                     @RequestParam(name = "orderBy") String orderBy) {
        return profileService.getProfilePage(DriverProfilePageRequest
                .builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .orderBy(orderBy)
                .build());
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/profile")
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    public DriverProfileResponse updateProfile(@Valid @RequestBody DriverProfileUpdateRequest profileDto) {
        return profileService.updateProfile(profileDto);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/profile")
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    public DriverProfileResponse saveProfile(@Valid  @RequestBody DriverProfileSaveRequest request,
                                             Principal principal) {
        String sub = ((Jwt)((Authentication) principal).getPrincipal()).getClaim("sub");
        return profileService.saveProfile(request, UUID.fromString(sub));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/profile/{id}")
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    public void deleteProfileById(@Parameter(name = "id", description = "Driver profile UUID")
                                  @PathVariable(name = "id") String id) {
        profileService.deleteProfileById(UUIDUtils.getUUIDFromString(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/profiles/{ids}")
    @PreAuthorize("hasRole('ROLE_modsen-admin')")
    public DriverProfilesResponse getByIds(@PathVariable(name = "ids") List<UUID> uuids) {
        return profileService.getByIds(uuids);
    }



}
