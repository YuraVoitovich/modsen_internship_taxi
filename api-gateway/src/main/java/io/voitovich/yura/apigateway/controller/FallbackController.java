package io.voitovich.yura.apigateway.controller;

import io.voitovich.yura.apigateway.model.ServiceFallbackResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/driver")
    public ServiceFallbackResponse driverFallback() {
        return ServiceFallbackResponse.builder()
                .message("Driver service is not available, please try again later")
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .build();
    }

    @GetMapping("/fallback/passenger")
    public ServiceFallbackResponse passengerFallback() {
        return ServiceFallbackResponse.builder()
                .message("Passenger service is not available, please try again later")
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .build();
    }

    @GetMapping("/fallback/ride")
    public ServiceFallbackResponse rideFallback() {
        return ServiceFallbackResponse.builder()
                .message("Ride service is not available, please try again later")
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .build();
    }
}