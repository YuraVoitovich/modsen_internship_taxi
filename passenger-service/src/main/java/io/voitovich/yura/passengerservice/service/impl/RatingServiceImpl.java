package io.voitovich.yura.passengerservice.service.impl;

import io.voitovich.yura.passengerservice.entity.PassengerProfile;
import io.voitovich.yura.passengerservice.entity.Rating;
import io.voitovich.yura.passengerservice.event.model.ReceiveRatingModel;
import io.voitovich.yura.passengerservice.model.RecalculateRatingModel;
import io.voitovich.yura.passengerservice.repository.RatingRepository;
import io.voitovich.yura.passengerservice.service.PassengerProfileService;
import io.voitovich.yura.passengerservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository repository;
    private final PassengerProfileService service;

    @Override
    public void saveAndRecalculateRating(ReceiveRatingModel model) {
        log.info("Saving rating with model: {}", model);
        RecalculateRatingModel recalculateRatingModel = RecalculateRatingModel
                .builder()
                .newRating(model.rating())
                .ratingsCount(repository.count())
                .passengerProfileId(model.ratedId())
                .build();
        PassengerProfile profile = service.getPassengerProfileAndRecalculateRating(recalculateRatingModel);
        repository.save(Rating
                .builder()
                .passengerProfile(profile)
                .rateValue(model.rating())
                .driverId(model.raterId())
                .build());

    }
}
