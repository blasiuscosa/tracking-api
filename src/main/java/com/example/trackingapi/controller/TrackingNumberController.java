package com.example.trackingapi.controller;

import com.example.trackingapi.service.TrackingNumberService;
import com.example.trackingapi.dto.TrackingNumberResponse;
import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/next-tracking-number")
public class TrackingNumberController {

    private static final Logger logger = LoggerFactory.getLogger(com.example.trackingapi.controller.TrackingNumberController.class);

    private final com.example.trackingapi.service.TrackingNumberService trackingNumberService;

    public TrackingNumberController(TrackingNumberService trackingNumberService) {
        this.trackingNumberService = trackingNumberService;
    }

    @GetMapping
    public ResponseEntity<TrackingNumberResponse> getNextTrackingNumber(
            @RequestParam @NotBlank @Pattern(regexp = "^[A-Z]{2}$", message = "Origin country code must be 2 uppercase letters") String origin_country_id,
            @RequestParam @NotBlank @Pattern(regexp = "^[A-Z]{2}$", message = "Destination country code must be 2 uppercase letters") String destination_country_id,
            @RequestParam @NotNull @DecimalMin(value = "0.001") @Digits(integer = 5, fraction = 3) BigDecimal weight,
            @RequestParam @NotBlank String created_at,
            @RequestParam @NotNull UUID customer_id,
            @RequestParam @NotBlank String customer_name,
            @RequestParam @NotBlank String customer_slug
    ) {
        String trackingNumber = trackingNumberService.generateTrackingNumber(
                origin_country_id,
                destination_country_id,
                weight,
                customer_id,
                customer_name,
                customer_slug
        );
        ZonedDateTime createdAtParsed = ZonedDateTime.parse(created_at);
        logger.info("Received request to generate tracking number | origin={} destination={} weight={} created_at={}",
                origin_country_id, destination_country_id, weight, createdAtParsed);

        return ResponseEntity.ok(new TrackingNumberResponse(trackingNumber, ZonedDateTime.now()));
    }
}
