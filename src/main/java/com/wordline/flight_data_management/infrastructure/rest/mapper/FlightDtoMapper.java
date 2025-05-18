package com.wordline.flight_data_management.infrastructure.rest.mapper;

import com.wordline.flight_data_management.domain.model.Flight;
import com.wordline.flight_data_management.domain.model.FlightSearchCriteria;
import com.wordline.flight_data_management.infrastructure.rest.dto.CreateFlightRequest;
import com.wordline.flight_data_management.infrastructure.rest.dto.FlightDto;
import com.wordline.flight_data_management.infrastructure.rest.dto.FlightSearchRequest;
import com.wordline.flight_data_management.infrastructure.rest.dto.UpdateFlightRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class FlightDtoMapper {

    public FlightDto toDto(Flight flight) {
        if (flight == null) {
            return null;
        }

        return FlightDto.builder()
                .id(flight.getId())
                .airline(flight.getAirline())
                .supplier(flight.getSupplier())
                .fare(flight.getFare())
                .departureAirport(flight.getDepartureAirport())
                .destinationAirport(flight.getDestinationAirport())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .build();
    }

    public List<FlightDto> toDtoList(List<Flight> flights) {
        if (flights == null) {
            return List.of();
        }

        return flights.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Flight toDomain(CreateFlightRequest request) {
        if (request == null) {
            return null;
        }

        return Flight.builder()
                .airline(request.getAirline())
                .supplier(request.getSupplier())
                .fare(request.getFare())
                .departureAirport(request.getDepartureAirport())
                .destinationAirport(request.getDestinationAirport())
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .build();
    }

    public Flight toDomain(UUID id, UpdateFlightRequest request, Flight existingFlight) {
        if (request == null || existingFlight == null) {
            return existingFlight;
        }

        return Flight.builder()
                .id(id)
                .airline(request.getAirline() != null ? request.getAirline() : existingFlight.getAirline())
                .supplier(request.getSupplier() != null ? request.getSupplier() : existingFlight.getSupplier())
                .fare(request.getFare() != null ? request.getFare() : existingFlight.getFare())
                .departureAirport(request.getDepartureAirport() != null ? request.getDepartureAirport() : existingFlight.getDepartureAirport())
                .destinationAirport(request.getDestinationAirport() != null ? request.getDestinationAirport() : existingFlight.getDestinationAirport())
                .departureTime(request.getDepartureTime() != null ? request.getDepartureTime() : existingFlight.getDepartureTime())
                .arrivalTime(request.getArrivalTime() != null ? request.getArrivalTime() : existingFlight.getArrivalTime())
                .build();
    }

    public FlightSearchCriteria toDomain(FlightSearchRequest request) {
        if (request == null) {
            return null;
        }

        return FlightSearchCriteria.builder()
                .departureAirport(request.getDepartureAirport())
                .destinationAirport(request.getDestinationAirport())
                .airline(request.getAirline())
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .build();
    }
}
