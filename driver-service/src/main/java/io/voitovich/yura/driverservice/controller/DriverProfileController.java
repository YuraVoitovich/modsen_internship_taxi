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
import io.voitovich.yura.driverservice.dto.request.DriverProfileRequest;
import io.voitovich.yura.driverservice.dto.response.DriverProfilePageResponse;
import io.voitovich.yura.driverservice.dto.response.DriverProfileResponse;
import io.voitovich.yura.driverservice.exceptionhandler.model.ExceptionInfo;
import io.voitovich.yura.driverservice.exceptionhandler.model.ValidationExceptionInfo;
import io.voitovich.yura.driverservice.service.DriverProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/driver/profile")
@Tag(name = "Driver profile controller", description = "Driver profile API")
@RequiredArgsConstructor
public class DriverProfileController {

    private final DriverProfileService profileService;

    @Operation(description = "Get driver profile by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver profile found",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DriverProfileResponse.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Driver profile not found",
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
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}")
    private DriverProfileResponse getProfileById(@Parameter(name = "id", description = "Driver profile UUID")
                                                     @PathVariable(name = "id") String id) {
        return profileService.getProfileById(UUIDUtils.getUUIDFromString(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    @Operation(description = "Get driver profile page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver profile page returned"),
            @ApiResponse(responseCode = "404", description = "Bad request body data",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ValidationExceptionInfo.class))
                    })
    })
    private DriverProfilePageResponse getProfilePage(@Valid @RequestBody DriverProfilePageRequest request) {
        return profileService.getProfilePage(request);
    }

    @Operation(description = "Update driver profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver profile updated"),
            @ApiResponse(responseCode = "404", description = "Bad request body data",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ValidationExceptionInfo.class))
                    }),
            @ApiResponse(responseCode = "409", description = "Driver phone number is not unique",
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
    @ResponseStatus(HttpStatus.OK)
    @PostMapping()
    private DriverProfileResponse updateProfile(@Valid @RequestBody DriverProfileRequest profileDto) {
        return profileService.updateProfile(profileDto);
    }


    @Operation(description = "Save driver profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Driver profile created"),
            @ApiResponse(responseCode = "404", description = "Bad request body data",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ValidationExceptionInfo.class))
                    }),
            @ApiResponse(responseCode = "409", description = "Driver phone number is not unique",
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
    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping()
    private DriverProfileResponse saveProfile(@Valid  @RequestBody DriverProfileRequest profileDto) {
        return profileService.saveProfile(profileDto);
    }

    @Operation(description = "Delete driver profile by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Driver profile deleted"),
            @ApiResponse(responseCode = "404", description = "Driver profile not found",
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    private void deleteProfileById(@Parameter(name = "id", description = "Driver profile UUID")
                                       @PathVariable(name = "id") String id) {
        profileService.deleteProfileById(UUIDUtils.getUUIDFromString(id));
    }

}
