package io.voitovich.yura.passengerservice.controller;


import io.voitovich.yura.passengerservice.dto.request.PassengerProfilePageRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerProfileRequest;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfilePageResponse;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfileResponse;
import io.voitovich.yura.passengerservice.service.PassengerProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.voitovich.yura.passengerservice.controller.utils.UUIDUtils.getUUIDFromString;

@RestController
@RequestMapping("api/passenger/profile")
public class PassengerProfileController {
    private final PassengerProfileService profileService;

    public PassengerProfileController(PassengerProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    private ResponseEntity<PassengerProfilePageResponse> getProfilePage(@Valid @RequestBody PassengerProfilePageRequest request) {
        return ResponseEntity.ok(profileService.getProfilePage(request));
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<PassengerProfileResponse> getProfileById(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(profileService
                .getProfileById(getUUIDFromString(id)));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<PassengerProfileResponse> updateProfile(@Valid @RequestBody PassengerProfileRequest passengerProfileRequest) {
        return ResponseEntity.ok(profileService.updateProfile(passengerProfileRequest));
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<PassengerProfileResponse> saveProfile(@Valid @RequestBody PassengerProfileRequest passengerProfileRequest) {
        return ResponseEntity.ok(profileService.saveProfile(passengerProfileRequest));
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteProfile(@PathVariable(name = "id") String id) {
        profileService.deleteProfile(getUUIDFromString(id));
    }
}
