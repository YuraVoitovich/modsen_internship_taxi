package io.voitovich.yura.driverservice.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DriverProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "phone_number", length = 13, nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "rating", nullable = false)
    private BigDecimal rating;

    @Column(name = "experience")
    private int experience;

    @ToString.Exclude
    @OneToMany(mappedBy = "driverProfile")
    private List<Rating> ratings;

}
