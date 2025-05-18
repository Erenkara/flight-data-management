package com.wordline.flight_data_management.domain.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ValidationException extends FlightDataManagementException {

    private final Map<String, String> errors;

    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

}