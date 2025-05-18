package com.wordline.flight_data_management.domain.exception;

import java.util.UUID;

public class FlightNotFoundException extends FlightDataManagementException {

    public FlightNotFoundException(String message) {
        super(message);
    }

    public static FlightNotFoundException withId(UUID id) {
        return new FlightNotFoundException("Flight not found with ID: " + id);
    }
}