package io.voitovich.yura.driverservice.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="rate_value")
    private BigDecimal rateValue;

    @Column(name="passenger_id")
    private UUID passengerId;

    @ManyToOne
    @JoinColumn(name = "driver_profile_id", nullable = false)
    private DriverProfile driverProfile;
}
