package io.voitovich.yura.driverservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.voitovich.yura.driverservice.dto.request.DriverProfileSaveRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfileUpdateRequest;
import io.voitovich.yura.driverservice.dto.response.DriverProfilePageResponse;
import io.voitovich.yura.driverservice.dto.response.DriverProfileResponse;
import io.voitovich.yura.driverservice.dto.response.DriverProfilesResponse;
import io.voitovich.yura.driverservice.exceptionhandler.model.ExceptionInfo;
import io.voitovich.yura.driverservice.exceptionhandler.model.ValidationExceptionInfo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface DriverProfile {

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
    DriverProfileResponse getProfileById(@Parameter(name = "id", description = "Driver profile UUID")
                                @PathVariable(name = "id") String id);

    @Operation(description = "Get driver profile page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver profile page returned"),
            @ApiResponse(responseCode = "404", description = "Bad request body data",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ValidationExceptionInfo.class))
                    })
    })
    DriverProfilePageResponse getProfilePage(@RequestParam(name = "pageNumber") int pageNumber,
                                             @RequestParam(name = "pageSize") int pageSize,
                                             @RequestParam(name = "orderBy") String orderBy);

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
    DriverProfileResponse updateProfile(@Valid @RequestBody DriverProfileUpdateRequest profileDto);

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
                    })
    })
    DriverProfileResponse saveProfile(@Valid @RequestBody DriverProfileSaveRequest request,
                                      Principal principal);

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
    void deleteProfileById(@Parameter(name = "id", description = "Driver profile UUID")
                                   @PathVariable(name = "id") String id);

    DriverProfilesResponse getByIds(@PathVariable(name = "ids") List<UUID> uuids);

}
