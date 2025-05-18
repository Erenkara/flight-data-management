package com.wordline.flight_data_management.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    private UUID id;
    private String airline;
    private String supplier;
    private BigDecimal fare;
    private String departureAirport;
    private String destinationAirport;
    private ZonedDateTime departureTime;
    private ZonedDateTime arrivalTime;
}
