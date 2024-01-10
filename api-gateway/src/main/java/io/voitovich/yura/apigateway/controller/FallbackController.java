package io.voitovich.yura.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/driver")
    public ResponseEntity<String> driverFallback() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fallback for Driver Service");
    }

    @GetMapping("/fallback/passenger")
    public ResponseEntity<String> passengerFallback() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fallback for Passenger Service");
    }

    @GetMapping("/fallback/ride")
    public ResponseEntity<String> rideFallback() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fallback for Ride Service");
    }
}