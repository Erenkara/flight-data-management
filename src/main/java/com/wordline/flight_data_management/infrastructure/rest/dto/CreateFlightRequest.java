package com.wordline.flight_data_management.infrastructure.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFlightRequest {

    @NotBlank(message = "Airline is required")
    private String airline;

    @NotBlank(message = "Supplier is required")
    private String supplier;

    @NotNull(message = "Fare is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Fare must be greater than 0")
    private BigDecimal fare;

    @NotBlank(message = "Departure airport is required")
    @Size(min = 3, max = 3, message = "Departure airport must be a 3-letter code")
    @Pattern(regexp = "[A-Z]{3}", message = "Departure airport must be a 3-letter code")
    private String departureAirport;

    @NotBlank(message = "Destination airport is required")
    @Size(min = 3, max = 3, message = "Destination airport must be a 3-letter code")
    @Pattern(regexp = "[A-Z]{3}", message = "Destination airport must be a 3-letter code")
    private String destinationAirport;

    @NotNull(message = "Departure time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private ZonedDateTime departureTime;

    @NotNull(message = "Arrival time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private ZonedDateTime arrivalTime;
}
