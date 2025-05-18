package com.wordline.flight_data_management.application.service;

import com.wordline.flight_data_management.application.port.in.FlightService;
import com.wordline.flight_data_management.application.port.out.CrazySupplierClient;
import com.wordline.flight_data_management.application.port.out.FlightRepository;
import com.wordline.flight_data_management.domain.exception.FlightNotFoundException;
import com.wordline.flight_data_management.domain.model.CrazySupplierFlight;
import com.wordline.flight_data_management.domain.model.Flight;
import com.wordline.flight_data_management.domain.model.FlightSearchCriteria;
import com.wordline.flight_data_management.infrastructure.util.TimezoneConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightServiceImpl implements FlightService {

    private static final String CRAZY_SUPPLIER = "CrazySupplier";

    private final FlightRepository flightRepository;
    private final CrazySupplierClient crazySupplierClient;

    @Override
    @Transactional
    public Flight createFlight(Flight flight) {
        log.debug("Creating flight: {}", flight);
        if (flight.getId() == null) {
            flight.setId(UUID.randomUUID());
        }
        return flightRepository.save(flight);
    }

    @Override
    @Transactional
    @CacheEvict(value = "flights", key = "#id")
    public Flight updateFlight(UUID id, Flight flight) {
        log.debug("Updating flight with id: {}", id);
        if (!flightRepository.existsById(id)) {
            throw FlightNotFoundException.withId(id);
        }
        flight.setId(id);
        return flightRepository.save(flight);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "flights", key = "#id")
    public Optional<Flight> getFlightById(UUID id) {
        log.debug("Getting flight with id: {}", id);
        return flightRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Flight> getAllFlights() {
        log.debug("Getting all flights");
        return flightRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "flights", key = "'search:' + #criteria.hashCode()")
    public List<Flight> searchFlights(FlightSearchCriteria criteria) {
        log.debug("Searching flights with criteria: {}", criteria);

        CompletableFuture<List<Flight>> repositoryFlightsFuture = CompletableFuture.supplyAsync(() -> {
            log.debug("Fetching flights from repository");
            return flightRepository.findByCriteria(criteria);
        });

        CompletableFuture<List<Flight>> crazySupplierFlightsFuture = CompletableFuture.supplyAsync(() -> {
            log.debug("Fetching flights from CrazySupplier");
            try {
                List<CrazySupplierFlight> crazySupplierFlights = crazySupplierClient.searchFlights(criteria);
                return convertCrazySupplierFlights(crazySupplierFlights);
            } catch (Exception e) {
                log.error("Error fetching flights from CrazySupplier", e);
                return List.of();
            }
        });

        List<Flight> allFlights = new ArrayList<>();
        try {
            allFlights.addAll(repositoryFlightsFuture.get());
            allFlights.addAll(crazySupplierFlightsFuture.get());
        } catch (Exception e) {
            log.error("Error combining flight results", e);
            // If there's an error, try to get at least the repository flights
            try {
                allFlights.addAll(repositoryFlightsFuture.get());
            } catch (Exception ex) {
                log.error("Error getting repository flights", ex);
            }
        }

        return allFlights;
    }

    @Override
    @Transactional
    @CacheEvict(value = "flights", key = "#id")
    public void deleteFlight(UUID id) {
        log.debug("Deleting flight with id: {}", id);
        if (!flightRepository.existsById(id)) {
            throw FlightNotFoundException.withId(id);
        }
        flightRepository.deleteById(id);
    }

    private List<Flight> convertCrazySupplierFlights(List<CrazySupplierFlight> crazySupplierFlights) {
        if (crazySupplierFlights == null) {
            return new ArrayList<>();
        }

        return crazySupplierFlights.stream()
                .map(this::convertCrazySupplierFlight)
                .collect(Collectors.toList());
    }

    private Flight convertCrazySupplierFlight(CrazySupplierFlight crazySupplierFlight) {
        BigDecimal totalFare = crazySupplierFlight.getBasePrice().add(crazySupplierFlight.getTax());

        ZonedDateTime departureTime = TimezoneConverter.convertLocalDateTimeFromCetToUtc(crazySupplierFlight.getOutboundDateTime())
                .atZone(TimezoneConverter.UTC_ZONE);
        ZonedDateTime arrivalTime = TimezoneConverter.convertLocalDateTimeFromCetToUtc(crazySupplierFlight.getInboundDateTime())
                .atZone(TimezoneConverter.UTC_ZONE);

        return Flight.builder()
                .id(UUID.randomUUID())
                .airline(crazySupplierFlight.getCarrier())
                .supplier(CRAZY_SUPPLIER)
                .fare(totalFare)
                .departureAirport(crazySupplierFlight.getDepartureAirportName())
                .destinationAirport(crazySupplierFlight.getArrivalAirportName())
                .departureTime(departureTime)
                .arrivalTime(arrivalTime)
                .build();
    }
}
