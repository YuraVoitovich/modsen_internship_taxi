package io.voitovich.yura.passengerservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "passenger_profile")
public class PassengerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "phone_number", length = 13, nullable = false)
    private String phoneNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @OneToMany(mappedBy = "passengerProfile")
    private List<Rating> ratings;


}
