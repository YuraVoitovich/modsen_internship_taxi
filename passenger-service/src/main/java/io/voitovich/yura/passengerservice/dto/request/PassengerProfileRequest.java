package io.voitovich.yura.passengerservice.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerProfileRequest {

    private UUID id;

    @NotBlank(message = "Phone is mandatory")
    @Pattern(regexp = "^\\+375((17|29|33|44))[0-9]{7}$")
    private String phoneNumber;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Surname is mandatory")
    private String surname;
}
