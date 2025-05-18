package com.wordline.flight_data_management.domain.exception;

public class FlightDataManagementException extends RuntimeException {

    public FlightDataManagementException(String message) {
        super(message);
    }

    public FlightDataManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}