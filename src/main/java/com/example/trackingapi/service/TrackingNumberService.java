package com.example.trackingapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

@Service
public class TrackingNumberService {

    private static final Logger logger = LoggerFactory.getLogger(TrackingNumberService.class);

    private final StringRedisTemplate redisTemplate;

    public TrackingNumberService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateTrackingNumber(String origin, String destination, BigDecimal weight,
                                         UUID customerId, String customerName, String customerSlug) {

        String key = String.format("tracknum:%s%s%s%s%s", weight, customerId, customerSlug, origin, destination);

        logger.debug("Generating tracking number sequence | key={}", key);

        // Atomic counter using Redis
        Long sequence = redisTemplate.opsForValue().increment(key);

        if (sequence == null) {
            logger.error("Failed to increment sequence counter in Redis for key={}", key);
            throw new IllegalStateException("Could not generate tracking number sequence");
        }

        String sequencePart = String.format("%04d", sequence % 10000); // Ensure 4 digits max

        String base = (origin + destination + sequencePart).toUpperCase();

        // Trim or pad to 16 characters
        String trackingNumber = base.length() > 16 ? base.substring(0, 16) : String.format("%-16s", base).replace(' ', '0');

        logger.debug("Tracking number generated internally | trackingNumber={} sequence={}", trackingNumber, sequence);

        return trackingNumber;
    }
}
