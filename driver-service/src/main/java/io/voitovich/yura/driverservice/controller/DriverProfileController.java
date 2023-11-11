package io.voitovich.yura.driverservice.controller;

import io.voitovich.yura.driverservice.controller.utils.UUIDUtils;
import io.voitovich.yura.driverservice.dto.DriverProfileDto;
import io.voitovich.yura.driverservice.dto.DriverProfilePageRequest;
import io.voitovich.yura.driverservice.dto.DriverProfilePageResponse;
import io.voitovich.yura.driverservice.service.DriverProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/driver")
public class DriverProfileController {

    private final DriverProfileService profileService;

    public DriverProfileController(DriverProfileService profileService) {
        this.profileService = profileService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("profile/{id}")
    private DriverProfileDto getProfileById(@PathVariable(name = "id") String id) {
        return profileService.getProfileById(UUIDUtils.getUUIDFromString(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("profile")
    private DriverProfilePageResponse getProfilePage(@Valid @RequestBody DriverProfilePageRequest request) {
        return profileService.getProfilePage(request);
    }
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("profile")
    private DriverProfileDto updateProfile(@Valid @RequestBody DriverProfileDto profileDto) {
        return profileService.updateProfile(profileDto);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("profile")
    private DriverProfileDto saveProfile(@Valid  @RequestBody DriverProfileDto profileDto) {
        return profileService.saveProfile(profileDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("profile/{id}")
    private void deleteProfileById(@PathVariable(name = "id") String id) {
        profileService.deleteProfileById(UUIDUtils.getUUIDFromString(id));
    }

}
