package com.wordline.flight_data_management.infrastructure.rest.controller;

import com.wordline.flight_data_management.application.port.in.FlightService;
import com.wordline.flight_data_management.domain.model.Flight;
import com.wordline.flight_data_management.domain.model.FlightSearchCriteria;
import com.wordline.flight_data_management.infrastructure.rest.dto.CreateFlightRequest;
import com.wordline.flight_data_management.infrastructure.rest.dto.FlightDto;
import com.wordline.flight_data_management.infrastructure.rest.dto.FlightSearchRequest;
import com.wordline.flight_data_management.infrastructure.rest.dto.UpdateFlightRequest;
import com.wordline.flight_data_management.infrastructure.rest.mapper.FlightDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/flights")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Flight", description = "Flight management API")
public class FlightController {

    private final FlightService flightService;
    private final FlightDtoMapper flightDtoMapper;

    @PostMapping
    @Operation(summary = "Create a new flight")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Flight created",
                    content = @Content(schema = @Schema(implementation = FlightDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<FlightDto> createFlight(
            @Valid @RequestBody CreateFlightRequest request) {
        log.debug("Creating flight: {}", request);
        Flight flight = flightDtoMapper.toDomain(request);
        Flight createdFlight = flightService.createFlight(flight);
        FlightDto response = flightDtoMapper.toDto(createdFlight);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing flight")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight updated",
                    content = @Content(schema = @Schema(implementation = FlightDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    public ResponseEntity<FlightDto> updateFlight(
            @Parameter(description = "Flight ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateFlightRequest request) {
        log.debug("Updating flight with id: {}", id);
        return flightService.getFlightById(id)
                .map(existingFlight -> {
                    Flight flight = flightDtoMapper.toDomain(id, request, existingFlight);
                    Flight updatedFlight = flightService.updateFlight(id, flight);
                    FlightDto response = flightDtoMapper.toDto(updatedFlight);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a flight by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight found",
                    content = @Content(schema = @Schema(implementation = FlightDto.class))),
            @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    public ResponseEntity<FlightDto> getFlightById(
            @Parameter(description = "Flight ID") @PathVariable UUID id) {
        log.debug("Getting flight with id: {}", id);
        return flightService.getFlightById(id)
                .map(flight -> {
                    FlightDto response = flightDtoMapper.toDto(flight);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all flights")
    @ApiResponse(responseCode = "200", description = "Flights found",
            content = @Content(schema = @Schema(implementation = FlightDto.class)))
    public ResponseEntity<List<FlightDto>> getAllFlights() {
        log.debug("Getting all flights");
        List<Flight> flights = flightService.getAllFlights();
        List<FlightDto> response = flightDtoMapper.toDtoList(flights);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    @Operation(summary = "Search for flights")
    @ApiResponse(responseCode = "200", description = "Search results",
            content = @Content(schema = @Schema(implementation = FlightDto.class)))
    public ResponseEntity<List<FlightDto>> searchFlights(
            @Valid @RequestBody FlightSearchRequest request) {
        log.debug("Searching flights with criteria: {}", request);
        FlightSearchCriteria criteria = flightDtoMapper.toDomain(request);
        List<Flight> flights = flightService.searchFlights(criteria);
        List<FlightDto> response = flightDtoMapper.toDtoList(flights);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a flight by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Flight deleted"),
            @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    public ResponseEntity<Void> deleteFlight(
            @Parameter(description = "Flight ID") @PathVariable UUID id) {
        log.debug("Deleting flight with id: {}", id);
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }
}
