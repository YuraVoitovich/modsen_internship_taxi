package io.voitovich.yura.passengerservice.controller;


import io.voitovich.yura.passengerservice.dto.request.PassengerProfileRequest;
import io.voitovich.yura.passengerservice.service.PassengerProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.voitovich.yura.passengerservice.controller.utils.UUIDUtils.getUUIDFromString;

@RestController
@RequestMapping("api/passenger")
public class PassengerProfileController {
    private final PassengerProfileService profileService;

    public PassengerProfileController(PassengerProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("profile/{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<PassengerProfileRequest> getProfileById(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(profileService
                .getProfileById(getUUIDFromString(id)));
    }

    @PostMapping("profile")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<PassengerProfileRequest> updateProfile(@Valid @RequestBody PassengerProfileRequest passengerProfileRequest) {
        return ResponseEntity.ok(profileService.updateProfile(passengerProfileRequest));
    }

    @PutMapping("profile")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<PassengerProfileRequest> saveProfile(@Valid @RequestBody PassengerProfileRequest passengerProfileRequest) {
        return ResponseEntity.ok(profileService.saveProfile(passengerProfileRequest));
    }

    @DeleteMapping("profile/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteProfile(@PathVariable(name = "id") String id) {
        profileService.deleteProfile(getUUIDFromString(id));
    }
}
