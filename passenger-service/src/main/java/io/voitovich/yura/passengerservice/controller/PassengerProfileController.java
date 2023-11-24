package io.voitovich.yura.passengerservice.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.voitovich.yura.passengerservice.dto.request.PassengerProfilePageRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerProfileUpdateRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerSaveProfileRequest;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfilePageResponse;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfileResponse;
import io.voitovich.yura.passengerservice.exceptionhandler.model.ExceptionInfo;
import io.voitovich.yura.passengerservice.exceptionhandler.model.ValidationExceptionInfo;
import io.voitovich.yura.passengerservice.service.PassengerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static io.voitovich.yura.passengerservice.controller.utils.UUIDUtils.getUUIDFromString;

@RestController
@Tag(name = "Passenger profile controller", description = "Passenger profile API")
@RequestMapping("api/passenger/profile")
@RequiredArgsConstructor
public class PassengerProfileController {
    private final PassengerProfileService profileService;

    @Operation(description = "Get passenger profile page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Passenger profile page returned"),
            @ApiResponse(responseCode = "404", description = "Bad request body data",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ValidationExceptionInfo.class))
                    })
    })
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    PassengerProfilePageResponse getProfilePage(@Valid @RequestBody PassengerProfilePageRequest request) {
        return profileService.getProfilePage(request);
    }

    @Operation(description = "Get passenger profile by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Passenger profile found",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PassengerProfileResponse.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Passenger profile not found",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionInfo.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid id format",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionInfo.class))
                    })
    })
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    PassengerProfileResponse getProfileById(@PathVariable(name = "id") String id) {
        return profileService.getProfileById(getUUIDFromString(id));
    }


    @Operation(description = "Update passenger profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Passenger profile updated"),
            @ApiResponse(responseCode = "404", description = "Bad request body data",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ValidationExceptionInfo.class))
                    }),
            @ApiResponse(responseCode = "409", description = "Passenger phone number is not unique",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionInfo.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid id format",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionInfo.class))
                    })
    })
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    PassengerProfileResponse updateProfile(@Valid @RequestBody PassengerProfileUpdateRequest request) {
        return profileService.updateProfile(request);
    }


    @Operation(description = "Save passenger profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Passenger profile created"),
            @ApiResponse(responseCode = "404", description = "Bad request body data",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ValidationExceptionInfo.class))
                    }),
            @ApiResponse(responseCode = "409", description = "Passenger phone number is not unique",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionInfo.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid id format",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionInfo.class))
                    })
    })
    @PutMapping()
    @ResponseStatus(HttpStatus.CREATED)
    PassengerProfileResponse saveProfile(@Valid @RequestBody PassengerSaveProfileRequest passengerSaveProfileRequest) {
        return profileService.saveProfile(passengerSaveProfileRequest);
    }


    @Operation(description = "Delete passenger profile by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Passenger profile deleted"),
            @ApiResponse(responseCode = "404", description = "Passenger profile not found",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionInfo.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid id format",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionInfo.class))
                    })
    })
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteProfile(@PathVariable(name = "id") String id) {
        profileService.deleteProfile(getUUIDFromString(id));
    }
}
