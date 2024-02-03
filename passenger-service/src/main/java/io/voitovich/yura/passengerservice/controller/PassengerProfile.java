package io.voitovich.yura.passengerservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.voitovich.yura.passengerservice.dto.request.PassengerProfileUpdateRequest;
import io.voitovich.yura.passengerservice.dto.request.PassengerSaveProfileRequest;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfilePageResponse;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfileResponse;
import io.voitovich.yura.passengerservice.dto.response.PassengerProfilesResponse;
import io.voitovich.yura.passengerservice.exceptionhandler.model.ExceptionInfo;
import io.voitovich.yura.passengerservice.exceptionhandler.model.ValidationExceptionInfo;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface PassengerProfile {

    @Operation(description = "Get passenger profile page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Passenger profile page returned"),
            @ApiResponse(responseCode = "404", description = "Bad request body data",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ValidationExceptionInfo.class))
                    })
    })
    PassengerProfilePageResponse getProfilePage(
            @RequestParam(name = "pageNumber") int pageNumber,
            @RequestParam(name = "pageSize") int pageSize,
            @RequestParam(name = "orderBy") String orderBy);

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
    PassengerProfileResponse getProfileById(@PathVariable(name = "id") String id);


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
    @PostMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    PassengerProfileResponse updateProfile(@Valid @RequestBody PassengerProfileUpdateRequest request);


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
    PassengerProfileResponse saveProfile(@Valid @RequestBody PassengerSaveProfileRequest passengerSaveProfileRequest,
                                         Principal principal);


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
    void deleteProfile(@PathVariable(name = "id") String id);
    PassengerProfilesResponse getByIds(@PathVariable(name = "ids") List<UUID> uuids);

}
