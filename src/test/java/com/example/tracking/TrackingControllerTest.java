package com.example.tracking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest
public class TrackingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TrackingNumberService trackingNumberService;

    @InjectMocks
    private TrackingController trackingController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trackingController).build();
    }

    @Test
    void testGetNextTrackingNumber() throws Exception {
        // Arrange
        String expectedTrackingNumber = "MYID230400001";
        UUID customerId = UUID.randomUUID();
        BigDecimal weight = BigDecimal.valueOf(1.234);

        when(trackingNumberService.generateTrackingNumber(
                "MY", "ID", weight,
                any(), customerId, "RedBox Logistics", "redbox-logistics"))
                .thenReturn(expectedTrackingNumber);

        // Act & Assert
        mockMvc.perform(get("/next-tracking-number")
                        .param("origin_country_id", "MY")
                        .param("destination_country_id", "ID")
                        .param("weight", "1.234")
                        .param("created_at", "2023-04-20T14:30:00+08:00")
                        .param("customer_id", customerId.toString())
                        .param("customer_name", "RedBox Logistics")
                        .param("customer_slug", "redbox-logistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tracking_number").value(expectedTrackingNumber))
                .andExpect(jsonPath("$.created_at").exists());

        verify(trackingNumberService, times(1)).generateTrackingNumber(
                "MY", "ID", weight,
                any(), customerId, "RedBox Logistics", "redbox-logistics");
    }
}