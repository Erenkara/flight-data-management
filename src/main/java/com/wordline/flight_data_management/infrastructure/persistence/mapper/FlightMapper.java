package com.wordline.flight_data_management.infrastructure.persistence.mapper;

import com.wordline.flight_data_management.domain.model.Flight;
import com.wordline.flight_data_management.infrastructure.persistence.entity.FlightEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FlightMapper {

    public FlightEntity toEntity(Flight flight) {
        if (flight == null) {
            return null;
        }

        return FlightEntity.builder()
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

    public Flight toDomain(FlightEntity entity) {
        if (entity == null) {
            return null;
        }

        return Flight.builder()
                .id(entity.getId())
                .airline(entity.getAirline())
                .supplier(entity.getSupplier())
                .fare(entity.getFare())
                .departureAirport(entity.getDepartureAirport())
                .destinationAirport(entity.getDestinationAirport())
                .departureTime(entity.getDepartureTime())
                .arrivalTime(entity.getArrivalTime())
                .build();
    }

    public List<Flight> toDomainList(List<FlightEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}
