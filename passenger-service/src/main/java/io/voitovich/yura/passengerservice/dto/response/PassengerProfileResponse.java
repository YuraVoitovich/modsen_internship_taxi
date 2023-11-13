package io.voitovich.yura.passengerservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Schema(name = "PassengerProfileResponse")
public record PassengerProfileResponse(
        @Schema(name = "id", description = "UUID of the user",
                example = "e80a23a7-71e0-4d82-b042-96fdd6f43bd8")
        @NonNull
        UUID id,
        @Schema(name = "phoneNumber", description = "Mobile phone of a resident of Minsk", example = "+375295554433")
        @NonNull
        String phoneNumber,
        @Schema(name = "name", description = "User name", example = "Петя")
        @NonNull
        String name,
        @Schema(name = "surname", description = "User surname", example = "Петров")
        @NonNull
        String surname,
        @Schema(name = "rating", description = "User rating", example = "4.7")
        @NonNull
        BigDecimal rating
) {
}
