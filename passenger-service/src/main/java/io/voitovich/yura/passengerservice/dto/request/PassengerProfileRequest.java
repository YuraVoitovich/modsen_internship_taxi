package io.voitovich.yura.passengerservice.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Schema(name = "PassengerProfileRequest", description = "represents passenger profile")
public record PassengerProfileRequest (
    @Schema(name = "id", description = "UUID of the user", example = "e80a23a7-71e0-4d82-b042-96fdd6f43bd8")
    UUID id,
    @Schema(name = "phoneNumber", description = "Mobile phone of a resident of Minsk", example = "+375295554433")
    @NotBlank(message = "Phone is mandatory")
    @Pattern(regexp = "^\\+375((17|29|33|44))[0-9]{7}$")
    String phoneNumber,
    @Schema(name = "name", description = "User name", example = "Петя")
    @NotBlank(message = "Name is mandatory")
    String name,
    @Schema(name = "surname", description = "User surname", example = "Петров")
    @NotBlank(message = "Surname is mandatory")
    String surname
) {}
