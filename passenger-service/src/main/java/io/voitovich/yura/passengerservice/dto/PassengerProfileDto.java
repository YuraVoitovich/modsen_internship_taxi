package io.voitovich.yura.passengerservice.dto;


import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerProfileDto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "phone_number", length = 13, nullable = false)
    private String phoneNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;
}
