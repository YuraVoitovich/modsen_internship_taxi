package io.voitovich.yura.driverservice.service.impl;

import io.voitovich.yura.driverservice.entity.DriverProfile;
import io.voitovich.yura.driverservice.entity.Rating;
import io.voitovich.yura.driverservice.event.model.ReceiveRatingModel;
import io.voitovich.yura.driverservice.model.RecalculateRatingModel;
import io.voitovich.yura.driverservice.repository.RatingRepository;
import io.voitovich.yura.driverservice.service.DriverProfileService;
import io.voitovich.yura.driverservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository repository;
    private final DriverProfileService service;

    @Override
    public void saveAndRecalculateRating(ReceiveRatingModel model) {
        log.info("Saving rating with model: {}", model);
        RecalculateRatingModel recalculateRatingModel = RecalculateRatingModel
                .builder()
                .newRating(model.rating())
                .ratingsCount(repository.count())
                .passengerProfileId(model.ratedId())
                .build();
        DriverProfile profile = service.getPassengerProfileAndRecalculateRating(recalculateRatingModel);
        repository.save(Rating
                .builder()
                .driverProfile(profile)
                .rateValue(model.rating())
                .passengerId(model.raterId())
                .build());

    }
}
