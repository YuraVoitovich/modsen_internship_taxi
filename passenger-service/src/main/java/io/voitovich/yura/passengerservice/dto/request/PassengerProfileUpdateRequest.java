package io.voitovich.yura.passengerservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(name = "PassengerProfileUpdateRequest", description = "represents passenger update request")
public record PassengerProfileUpdateRequest(
        @NotNull
        @Schema(name = "id", description = "UUID of the user", example = "e80a23a7-71e0-4d82-b042-96fdd6f43bd8")
        UUID id,
        @Schema(name = "phoneNumber", description = "Mobile phone of a resident of Minsk", example = "+375295554433")
        @NotBlank(message = "{api.error.empty.phone}")
        @Pattern(regexp = "^\\+375(17|29|33|44)[0-9]{7}$")
        String phoneNumber,
        @Schema(name = "name", description = "User name", example = "Петя")
        @NotBlank(message = "{api.error.empty.name}")
        String name,
        @Schema(name = "surname", description = "User surname", example = "Петров")
        @NotBlank(message = "{api.error.empty.surname}")
        String surname
) {}