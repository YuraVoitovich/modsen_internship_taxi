package io.voitovich.yura.passengerservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "passenger_profile")
public class PassengerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "phone_number", length = 13, nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @ToString.Exclude
    @OneToMany(mappedBy = "passengerProfile")
    private List<Rating> ratings;


}
