package io.voitovich.yura.passengerservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "rating")
@Builder
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="rate_value", nullable = false)
    private BigDecimal rateValue;

    @Column(name="driver_id", nullable = false)
    private UUID driverId;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "passenger_profile_id", nullable = false)
    private PassengerProfile passengerProfile;


}
