package com.example.trackingapi.dto;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TrackingNumberResponse {
    private final String tracking_number;
    private final String created_at;

    public TrackingNumberResponse(String tracking_number, ZonedDateTime created_at) {
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
