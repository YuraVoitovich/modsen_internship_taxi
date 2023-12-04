package io.voitovich.yura.passengerservice.service;

import io.voitovich.yura.passengerservice.event.model.ReceiveRatingModel;

public interface RatingService {

    void saveAndRecalculateRating(ReceiveRatingModel model);
}
