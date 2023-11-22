package io.voitovich.yura.passengerservice.repository;

import io.voitovich.yura.passengerservice.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
}
