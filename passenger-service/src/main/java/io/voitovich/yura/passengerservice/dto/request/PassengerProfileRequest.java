package io.voitovich.yura.passengerservice.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
public record PassengerProfileRequest (
    UUID id,
    @NotBlank(message = "Phone is mandatory")
    @Pattern(regexp = "^\\+375((17|29|33|44))[0-9]{7}$")
    String phoneNumber,
    @NotBlank(message = "Name is mandatory")
    String name,
    @NotBlank(message = "Surname is mandatory")
    String surname
) {}
