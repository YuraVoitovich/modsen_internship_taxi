package io.voitovich.yura.driverservice.repository;

import io.voitovich.yura.driverservice.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
}
