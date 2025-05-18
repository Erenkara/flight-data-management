package com.wordline.flight_data_management.infrastructure.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchRequest {

    @Size(min = 3, max = 3, message = "Departure airport must be a 3-letter code")
    @Pattern(regexp = "[A-Z]{3}", message = "Departure airport must be a 3-letter code")
    private String departureAirport;

    @Size(min = 3, max = 3, message = "Destination airport must be a 3-letter code")
    @Pattern(regexp = "[A-Z]{3}", message = "Destination airport must be a 3-letter code")
    private String destinationAirport;

    private String airline;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private ZonedDateTime departureTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private ZonedDateTime arrivalTime;
}
