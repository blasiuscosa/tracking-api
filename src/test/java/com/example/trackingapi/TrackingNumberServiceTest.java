package com.example.trackingapi;

import com.example.trackingapi.service.TrackingNumberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@SpringBootTest
public class TrackingNumberServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    private TrackingNumberService trackingNumberService;

    @BeforeEach
    void setUp() {
        trackingNumberService = new TrackingNumberService(redisTemplate);
    }

    @Test
    void testGenerateTrackingNumber() {
        // Arrange
        String origin = "MY";
        String destination = "ID";
        BigDecimal weight = BigDecimal.valueOf(1.234);
        UUID customerId = UUID.randomUUID();
        String customerName = "RedBox Logistics";
        String customerSlug = "redbox-logistics";

        // Simulate Redis increment behavior
        when(redisTemplate.opsForValue().increment(anyString())).thenReturn(1L);

        // Act
        String trackingNumber = trackingNumberService.generateTrackingNumber(
                origin, destination, weight, customerId, customerName, customerSlug);

        // Assert
        assertNotNull(trackingNumber);
        assertTrue(trackingNumber.matches("^[A-Z0-9]{1,16}$"));
        verify(redisTemplate, times(1)).opsForValue().increment(anyString());
    }
}