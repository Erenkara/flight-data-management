package com.wordline.flight_data_management.infrastructure.rest.mapper;

import com.wordline.flight_data_management.domain.model.Flight;
import com.wordline.flight_data_management.domain.model.FlightSearchCriteria;
import com.wordline.flight_data_management.infrastructure.rest.dto.CreateFlightRequest;
import com.wordline.flight_data_management.infrastructure.rest.dto.FlightDto;
import com.wordline.flight_data_management.infrastructure.rest.dto.FlightSearchRequest;
import com.wordline.flight_data_management.infrastructure.rest.dto.UpdateFlightRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FlightDtoMapperTest {

    private FlightDtoMapper flightDtoMapper;
    private UUID flightId;
    private Flight flight;
    private FlightDto flightDto;
    private CreateFlightRequest createFlightRequest;
    private UpdateFlightRequest updateFlightRequest;
    private FlightSearchRequest flightSearchRequest;

    @BeforeEach
    void setUp() {
        flightDtoMapper = new FlightDtoMapper();
        flightId = UUID.randomUUID();

        ZonedDateTime departureTime = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime arrivalTime = departureTime.plusHours(6);

        flight = Flight.builder()
                .id(flightId)
                .airline("TestAirline")
                .supplier("TestSupplier")
                .fare(new BigDecimal("100.00"))
                .departureAirport("AMS")
                .destinationAirport("IST")
                .departureTime(departureTime)
                .arrivalTime(arrivalTime)
                .build();

        flightDto = FlightDto.builder()
                .id(flightId)
                .airline("TestAirline")
                .supplier("TestSupplier")
                .fare(new BigDecimal("100.00"))
                .departureAirport("AMS")
                .destinationAirport("IST")
                .departureTime(departureTime)
                .arrivalTime(arrivalTime)
                .build();

        createFlightRequest = CreateFlightRequest.builder()
                .airline("TestAirline")
                .supplier("TestSupplier")
                .fare(new BigDecimal("100.00"))
                .departureAirport("AMS")
                .destinationAirport("IST")
                .departureTime(departureTime)
                .arrivalTime(arrivalTime)
                .build();

        updateFlightRequest = UpdateFlightRequest.builder()
                .airline("UpdatedAirline")
                .supplier("UpdatedSupplier")
                .fare(new BigDecimal("150.00"))
                .departureAirport("SFO")
                .destinationAirport("ORD")
                .departureTime(departureTime.plusDays(1))
                .arrivalTime(arrivalTime.plusDays(1))
                .build();

        flightSearchRequest = FlightSearchRequest.builder()
                .departureAirport("AMS")
                .destinationAirport("IST")
                .airline("TestAirline")
                .departureTime(ZonedDateTime.now(ZoneId.of("UTC")))
                .arrivalTime(ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1))
                .build();
    }

    @Test
    void toDto_WithValidFlight_ShouldReturnFlightDto() {
        // When
        FlightDto result = flightDtoMapper.toDto(flight);

        // Then
        assertNotNull(result);
        assertEquals(flight.getId(), result.getId());
        assertEquals(flight.getAirline(), result.getAirline());
        assertEquals(flight.getSupplier(), result.getSupplier());
        assertEquals(flight.getFare(), result.getFare());
        assertEquals(flight.getDepartureAirport(), result.getDepartureAirport());
        assertEquals(flight.getDestinationAirport(), result.getDestinationAirport());

        // Check that the times are the same instant but might have different zone representations
        assertEquals(flight.getDepartureTime().toInstant(), result.getDepartureTime().toInstant());
        assertEquals(flight.getArrivalTime().toInstant(), result.getArrivalTime().toInstant());
    }

    @Test
    void toDto_WithNullFlight_ShouldReturnNull() {
        // When
        FlightDto result = flightDtoMapper.toDto(null);

        // Then
        assertNull(result);
    }

    @Test
    void toDtoList_WithValidFlights_ShouldReturnFlightDtoList() {
        // Given
        List<Flight> flights = List.of(flight);

        // When
        List<FlightDto> result = flightDtoMapper.toDtoList(flights);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(flight.getId(), result.getFirst().getId());
        assertEquals(flight.getAirline(), result.getFirst().getAirline());
    }

    @Test
    void toDtoList_WithNullFlights_ShouldReturnEmptyList() {
        // When
        List<FlightDto> result = flightDtoMapper.toDtoList(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDtoList_WithEmptyFlights_ShouldReturnEmptyList() {
        // When
        List<FlightDto> result = flightDtoMapper.toDtoList(Collections.emptyList());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDomain_WithValidCreateFlightRequest_ShouldReturnFlight() {
        // When
        Flight result = flightDtoMapper.toDomain(createFlightRequest);

        // Then
        assertNotNull(result);
        assertNull(result.getId()); // ID should be null for new flights
        assertEquals(createFlightRequest.getAirline(), result.getAirline());
        assertEquals(createFlightRequest.getSupplier(), result.getSupplier());
        assertEquals(createFlightRequest.getFare(), result.getFare());
        assertEquals(createFlightRequest.getDepartureAirport(), result.getDepartureAirport());
        assertEquals(createFlightRequest.getDestinationAirport(), result.getDestinationAirport());
        assertEquals(createFlightRequest.getDepartureTime(), result.getDepartureTime());
        assertEquals(createFlightRequest.getArrivalTime(), result.getArrivalTime());
    }

    @Test
    void toDomain_WithNullCreateFlightRequest_ShouldReturnNull() {
        // When
        Flight result = flightDtoMapper.toDomain((CreateFlightRequest) null);

        // Then
        assertNull(result);
    }

    @Test
    void toDomain_WithValidUpdateFlightRequestAndExistingFlight_ShouldReturnUpdatedFlight() {
        // When
        Flight result = flightDtoMapper.toDomain(flightId, updateFlightRequest, flight);

        // Then
        assertNotNull(result);
        assertEquals(flightId, result.getId());
        assertEquals(updateFlightRequest.getAirline(), result.getAirline());
        assertEquals(updateFlightRequest.getSupplier(), result.getSupplier());
        assertEquals(updateFlightRequest.getFare(), result.getFare());
        assertEquals(updateFlightRequest.getDepartureAirport(), result.getDepartureAirport());
        assertEquals(updateFlightRequest.getDestinationAirport(), result.getDestinationAirport());
        assertEquals(updateFlightRequest.getDepartureTime(), result.getDepartureTime());
        assertEquals(updateFlightRequest.getArrivalTime(), result.getArrivalTime());
    }

    @Test
    void toDomain_WithPartialUpdateFlightRequestAndExistingFlight_ShouldReturnPartiallyUpdatedFlight() {
        // Given
        UpdateFlightRequest partialUpdateRequest = UpdateFlightRequest.builder()
                .airline("UpdatedAirline")
                .build();

        // When
        Flight result = flightDtoMapper.toDomain(flightId, partialUpdateRequest, flight);

        // Then
        assertNotNull(result);
        assertEquals(flightId, result.getId());
        assertEquals("UpdatedAirline", result.getAirline());
        assertEquals(flight.getSupplier(), result.getSupplier());
        assertEquals(flight.getFare(), result.getFare());
        assertEquals(flight.getDepartureAirport(), result.getDepartureAirport());
        assertEquals(flight.getDestinationAirport(), result.getDestinationAirport());
        assertEquals(flight.getDepartureTime(), result.getDepartureTime());
        assertEquals(flight.getArrivalTime(), result.getArrivalTime());
    }

    @Test
    void toDomain_WithNullUpdateFlightRequest_ShouldReturnExistingFlight() {
        // When
        Flight result = flightDtoMapper.toDomain(flightId, null, flight);

        // Then
        assertNotNull(result);
        assertEquals(flight, result);
    }

    @Test
    void toDomain_WithNullExistingFlight_ShouldReturnNull() {
        // When
        Flight result = flightDtoMapper.toDomain(flightId, updateFlightRequest, null);

        // Then
        assertNull(result);
    }

    @Test
    void toDomain_WithValidFlightSearchRequest_ShouldReturnFlightSearchCriteria() {
        // When
        FlightSearchCriteria result = flightDtoMapper.toDomain(flightSearchRequest);

        // Then
        assertNotNull(result);
        assertEquals(flightSearchRequest.getDepartureAirport(), result.getDepartureAirport());
        assertEquals(flightSearchRequest.getDestinationAirport(), result.getDestinationAirport());
        assertEquals(flightSearchRequest.getAirline(), result.getAirline());
        assertEquals(flightSearchRequest.getDepartureTime(), result.getDepartureTime());
        assertEquals(flightSearchRequest.getArrivalTime(), result.getArrivalTime());
    }

    @Test
    void toDomain_WithNullFlightSearchRequest_ShouldReturnNull() {
        // When
        FlightSearchCriteria result = flightDtoMapper.toDomain((FlightSearchRequest) null);

        // Then
        assertNull(result);
    }
}
