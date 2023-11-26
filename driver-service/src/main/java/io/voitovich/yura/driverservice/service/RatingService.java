package io.voitovich.yura.driverservice.service;

import io.voitovich.yura.driverservice.event.model.ReceiveRatingModel;

public interface RatingService {

    void saveAndRecalculateRating(ReceiveRatingModel model);
}
