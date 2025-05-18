package com.wordline.flight_data_management.domain.exception;

import lombok.Getter;

@Getter
public class ExternalServiceException extends FlightDataManagementException {

    private final String serviceName;
    private final int statusCode;

    public ExternalServiceException(String message, String serviceName, int statusCode) {
        super(message);
        this.serviceName = serviceName;
        this.statusCode = statusCode;
    }
}
