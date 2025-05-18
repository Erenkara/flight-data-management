package com.wordline.flight_data_management.infrastructure.rest.exception;

import com.wordline.flight_data_management.domain.exception.ExternalServiceException;
import com.wordline.flight_data_management.domain.exception.FlightDataManagementException;
import com.wordline.flight_data_management.domain.exception.FlightNotFoundException;
import com.wordline.flight_data_management.domain.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private UUID flightId;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        flightId = UUID.randomUUID();
    }

    @Test
    void handleFlightNotFoundException_ShouldReturnNotFoundStatus() {
        // Given
        FlightNotFoundException exception = FlightNotFoundException.withId(flightId);

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleFlightNotFoundException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains(flightId.toString()));
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleValidationException_ShouldReturnBadRequestStatus() {
        // Given
        Map<String, String> errors = new HashMap<>();
        errors.put("airline", "Airline is required");
        ValidationException exception = new ValidationException("Validation error", errors);

        // When
        ResponseEntity<GlobalExceptionHandler.ValidationErrorResponse> response =
                globalExceptionHandler.handleValidationException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals("Validation error", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals("Airline is required", response.getBody().getErrors().get("airline"));
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequestStatus() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("flight", "airline", "Airline is required");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        // When
        ResponseEntity<GlobalExceptionHandler.ValidationErrorResponse> response =
                globalExceptionHandler.handleMethodArgumentNotValidException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals("Validation error", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals("Airline is required", response.getBody().getErrors().get("airline"));
    }

    @Test
    void handleExternalServiceException_ShouldReturnServiceUnavailableStatus() {
        // Given
        ExternalServiceException exception = new ExternalServiceException(
                "Error calling CrazySupplier API", "CrazySupplier", 500);

        // When
        ResponseEntity<GlobalExceptionHandler.ExternalServiceErrorResponse> response =
                globalExceptionHandler.handleExternalServiceException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("CrazySupplier"));
        assertNotNull(response.getBody().getTimestamp());
        assertEquals("CrazySupplier", response.getBody().getServiceName());
        assertEquals(500, response.getBody().getServiceStatusCode());
    }

    @Test
    void handleFlightDataManagementException_ShouldReturnInternalServerErrorStatus() {
        // Given
        FlightDataManagementException exception = new FlightDataManagementException("Internal error");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleFlightDataManagementException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertEquals("Internal error", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleException_ShouldReturnInternalServerErrorStatus() {
        // Given
        Exception exception = new RuntimeException("Unexpected error");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }
}
