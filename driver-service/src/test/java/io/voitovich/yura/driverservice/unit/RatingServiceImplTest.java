package io.voitovich.yura.driverservice.unit;

import io.voitovich.yura.driverservice.entity.DriverProfile;
import io.voitovich.yura.driverservice.entity.Rating;
import io.voitovich.yura.driverservice.event.model.ReceiveRatingModel;
import io.voitovich.yura.driverservice.model.RecalculateRatingModel;
import io.voitovich.yura.driverservice.repository.RatingRepository;
import io.voitovich.yura.driverservice.service.DriverProfileService;
import io.voitovich.yura.driverservice.service.impl.RatingServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static io.voitovich.yura.driverservice.unit.util.UnitTestsUtils.createDefaultDriverProfileWithId;
import static io.voitovich.yura.driverservice.unit.util.UnitTestsUtils.createDefaultReceiveRatingModel;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceImplTest {

    @Mock
    private RatingRepository repository;

    @Mock
    private DriverProfileService passengerProfileService;

    @InjectMocks
    private RatingServiceImpl service;

    @Test
    public void saveAndRecalculateRating_correctRatingModelReceived_recalculateRatingAndSaveIt() {

        UUID uuid = UUID.randomUUID();
        ReceiveRatingModel model = createDefaultReceiveRatingModel();

        DriverProfile passengerProfile = createDefaultDriverProfileWithId(uuid);

        when(passengerProfileService
                .getPassengerProfileAndRecalculateRating(any(RecalculateRatingModel.class))).thenReturn(passengerProfile);


        service.saveAndRecalculateRating(model);

        verify(passengerProfileService, times(1))
                .getPassengerProfileAndRecalculateRating(any(RecalculateRatingModel.class));
        verify(repository, times(1)).save(any(Rating.class));

    }
}
