package io.voitovich.yura.passengerservice.event.service.impl;

import io.voitovich.yura.passengerservice.event.model.ConfirmRatingReceiveModel;
import io.voitovich.yura.passengerservice.event.service.KafkaChannelGateway;
import io.voitovich.yura.passengerservice.event.service.KafkaConfirmRatingReceiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConfirmRatingReceiveServiceImpl implements KafkaConfirmRatingReceiveService {

    private final KafkaChannelGateway gateway;

    @Override
    public void confirmRatingReceive(ConfirmRatingReceiveModel model) {
        log.info(String.format("Send confirm rating receive message with model: %s", model));
        gateway.handleRatingReceiveMessage(model);
    }
}
