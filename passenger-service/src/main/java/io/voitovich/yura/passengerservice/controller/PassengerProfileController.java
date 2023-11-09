package io.voitovich.yura.passengerservice.controller;


import io.voitovich.yura.passengerservice.controller.utils.UUIDUtils;
import io.voitovich.yura.passengerservice.dto.PassengerProfileDto;
import io.voitovich.yura.passengerservice.entity.PassengerProfile;
import io.voitovich.yura.passengerservice.service.PassengerProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    ResponseEntity<PassengerProfileDto> getProfileById(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(profileService
                .getProfileById(getUUIDFromString(id)));
    }

    @PostMapping("profile")
    @ResponseStatus(HttpStatus.OK)
    void updateProfile(@Valid @RequestBody PassengerProfileDto passengerProfileDto) {
        profileService.updateProfile(passengerProfileDto);
    }

    @PutMapping("profile")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<PassengerProfileDto> saveProfile(@Valid @RequestBody PassengerProfileDto passengerProfileDto) {
        return ResponseEntity.ok(profileService.saveProfile(passengerProfileDto));
    }

    @DeleteMapping("profile/{id}")
    @ResponseStatus(HttpStatus.OK)
    void deleteProfile(@PathVariable(name = "id") String id) {
        profileService.deleteProfile(getUUIDFromString(id));
    }
}
