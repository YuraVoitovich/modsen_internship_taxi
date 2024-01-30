package io.voitovich.yura.apigateway.model;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ServiceFallbackResponse(
        String message,
        HttpStatus status
) {
}
