package com.wordline.flight_data_management.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchCriteria {
    private String departureAirport;
    private String destinationAirport;
    private String airline;
    private ZonedDateTime departureTime;
    private ZonedDateTime arrivalTime;
}
