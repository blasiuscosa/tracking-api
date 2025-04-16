package com.example.tracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@SpringBootApplication
public class TrackingApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrackingApplication.class, args);
    }
}

@RestController
@RequestMapping("/next-tracking-number")
class TrackingController {

    private final TrackingNumberService trackingNumberService;

    public TrackingController(TrackingNumberService trackingNumberService) {
        this.trackingNumberService = trackingNumberService;
    }

    @GetMapping
    public ResponseEntity<TrackingResponse> getNextTrackingNumber(
            @RequestParam String origin_country_id,
            @RequestParam String destination_country_id,
            @RequestParam BigDecimal weight,
            @RequestParam String created_at,
            @RequestParam UUID customer_id,
            @RequestParam String customer_name,
            @RequestParam String customer_slug
    ) {
        ZonedDateTime createdAtParsed = ZonedDateTime.parse(created_at);
        String trackingNumber = trackingNumberService.generateTrackingNumber(
                origin_country_id,
                destination_country_id,
                weight,
                createdAtParsed,
                customer_id,
                customer_name,
                customer_slug
        );
        return ResponseEntity.ok(new TrackingResponse(trackingNumber, ZonedDateTime.now()));
    }
}

class TrackingResponse {
    private final String tracking_number;
    private final String created_at;

    public TrackingResponse(String tracking_number, ZonedDateTime created_at) {
        this.tracking_number = tracking_number;
        this.created_at = created_at.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public String getTracking_number() {
        return tracking_number;
    }

    public String getCreated_at() {
        return created_at;
    }
}

@Service
class TrackingNumberService {

    private final StringRedisTemplate redisTemplate;

    public TrackingNumberService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateTrackingNumber(String origin, String destination, BigDecimal weight,
                                         ZonedDateTime createdAt, UUID customerId,
                                         String customerName, String customerSlug) {

        String datePart = createdAt.format(DateTimeFormatter.ofPattern("yyMMdd"));
        String key = String.format("tracknum:%s:%s%s", datePart, origin, destination);

        // Atomic counter using Redis
        Long sequence = redisTemplate.opsForValue().increment(key);
        String sequencePart = String.format("%04d", sequence % 10000); // Ensure 4 digits max

        String base = (origin + destination + datePart + sequencePart).toUpperCase();

        // Trim or pad to 16 characters
        return base.length() > 16 ? base.substring(0, 16) : String.format("%-16s", base).replace(' ', '0');
    }
}
