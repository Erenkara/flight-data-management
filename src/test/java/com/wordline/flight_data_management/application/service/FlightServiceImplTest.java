package com.wordline.flight_data_management.application.service;

import com.wordline.flight_data_management.application.port.out.CrazySupplierClient;
import com.wordline.flight_data_management.application.port.out.FlightRepository;
import com.wordline.flight_data_management.domain.exception.ExternalServiceException;
import com.wordline.flight_data_management.domain.exception.FlightNotFoundException;
import com.wordline.flight_data_management.domain.model.CrazySupplierFlight;
import com.wordline.flight_data_management.domain.model.Flight;
import com.wordline.flight_data_management.domain.model.FlightSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private CrazySupplierClient crazySupplierClient;

    @InjectMocks
    private FlightServiceImpl flightService;

    private UUID flightId;
    private Flight flight;
    private CrazySupplierFlight crazySupplierFlight;
    private FlightSearchCriteria searchCriteria;

    @BeforeEach
    void setUp() {
        flightId = UUID.randomUUID();

        flight = Flight.builder()
                .id(flightId)
                .airline("TestAirline")
                .supplier("TestSupplier")
                .fare(new BigDecimal("100.00"))
                .departureAirport("AMS")
                .destinationAirport("IST")
                .departureTime(ZonedDateTime.now(ZoneId.of("UTC")))
                .arrivalTime(ZonedDateTime.now(ZoneId.of("UTC")).plusHours(6))
                .build();

        crazySupplierFlight = CrazySupplierFlight.builder()
                .carrier("CrazyAirline")
                .basePrice(new BigDecimal("80.00"))
                .tax(new BigDecimal("20.00"))
                .departureAirportName("AMS")
                .arrivalAirportName("IST")
                .outboundDateTime(LocalDateTime.now())
                .inboundDateTime(LocalDateTime.now().plusHours(6))
                .build();

        searchCriteria = FlightSearchCriteria.builder()
                .departureAirport("AMS")
                .destinationAirport("IST")
                .departureTime(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
    }

    @Test
    void createFlight_ShouldReturnCreatedFlight() {
        // Given
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        // When
        Flight result = flightService.createFlight(flight);

        // Then
        assertNotNull(result);
        assertEquals(flight.getId(), result.getId());
        assertEquals(flight.getAirline(), result.getAirline());
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    void updateFlight_WhenFlightExists_ShouldReturnUpdatedFlight() {
        // Given
        when(flightRepository.existsById(flightId)).thenReturn(true);
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        // When
        Flight result = flightService.updateFlight(flightId, flight);

        // Then
        assertNotNull(result);
        assertEquals(flight.getId(), result.getId());
        assertEquals(flight.getAirline(), result.getAirline());
        verify(flightRepository, times(1)).existsById(flightId);
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    void updateFlight_WhenFlightDoesNotExist_ShouldThrowException() {
        // Given
        when(flightRepository.existsById(flightId)).thenReturn(false);

        // When/Then
        assertThrows(FlightNotFoundException.class, () -> flightService.updateFlight(flightId, flight));
        verify(flightRepository, times(1)).existsById(flightId);
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void getFlightById_WhenFlightExists_ShouldReturnFlight() {
        // Given
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flight));

        // When
        Optional<Flight> result = flightService.getFlightById(flightId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(flight.getId(), result.get().getId());
        assertEquals(flight.getAirline(), result.get().getAirline());
        verify(flightRepository, times(1)).findById(flightId);
    }

    @Test
    void getFlightById_WhenFlightDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(flightRepository.findById(flightId)).thenReturn(Optional.empty());

        // When
        Optional<Flight> result = flightService.getFlightById(flightId);

        // Then
        assertFalse(result.isPresent());
        verify(flightRepository, times(1)).findById(flightId);
    }

    @Test
    void getAllFlights_ShouldReturnAllFlights() {
        // Given
        when(flightRepository.findAll()).thenReturn(List.of(flight));

        // When
        List<Flight> result = flightService.getAllFlights();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(flight.getId(), result.getFirst().getId());
        verify(flightRepository, times(1)).findAll();
    }

    @Test
    void deleteFlight_WhenFlightExists_ShouldDeleteFlight() {
        // Given
        when(flightRepository.existsById(flightId)).thenReturn(true);
        doNothing().when(flightRepository).deleteById(flightId);

        // When
        flightService.deleteFlight(flightId);

        // Then
        verify(flightRepository, times(1)).existsById(flightId);
        verify(flightRepository, times(1)).deleteById(flightId);
    }

    @Test
    void deleteFlight_WhenFlightDoesNotExist_ShouldThrowException() {
        // Given
        when(flightRepository.existsById(flightId)).thenReturn(false);

        // When/Then
        assertThrows(FlightNotFoundException.class, () -> flightService.deleteFlight(flightId));
        verify(flightRepository, times(1)).existsById(flightId);
        verify(flightRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void searchFlights_ShouldReturnCombinedFlights() {
        // Given
        List<Flight> repositoryFlights = List.of(flight);
        List<CrazySupplierFlight> crazySupplierFlights = List.of(crazySupplierFlight);

        when(flightRepository.findByCriteria(searchCriteria)).thenReturn(repositoryFlights);
        when(crazySupplierClient.searchFlights(searchCriteria)).thenReturn(crazySupplierFlights);

        // When
        List<Flight> result = flightService.searchFlights(searchCriteria);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(flight.getId(), result.getFirst().getId());
        assertEquals("CrazyAirline", result.get(1).getAirline());
        verify(flightRepository, times(1)).findByCriteria(searchCriteria);
        verify(crazySupplierClient, times(1)).searchFlights(searchCriteria);
    }

    @Test
    void searchFlights_WhenCrazySupplierThrowsException_ShouldReturnRepositoryFlightsOnly() {
        // Given
        List<Flight> repositoryFlights = List.of(flight);

        when(flightRepository.findByCriteria(searchCriteria)).thenReturn(repositoryFlights);
        when(crazySupplierClient.searchFlights(searchCriteria)).thenThrow(new ExternalServiceException("Error", "CrazySupplier", 500));

        // When
        List<Flight> result = flightService.searchFlights(searchCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(flight.getId(), result.getFirst().getId());
        verify(flightRepository, times(1)).findByCriteria(searchCriteria);
        verify(crazySupplierClient, times(1)).searchFlights(searchCriteria);
    }

    @Test
    void searchFlights_WhenCrazySupplierReturnsNull_ShouldReturnRepositoryFlightsOnly() {
        // Given
        List<Flight> repositoryFlights = List.of(flight);

        when(flightRepository.findByCriteria(searchCriteria)).thenReturn(repositoryFlights);
        when(crazySupplierClient.searchFlights(searchCriteria)).thenReturn(null);

        // When
        List<Flight> result = flightService.searchFlights(searchCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(flight.getId(), result.getFirst().getId());
        verify(flightRepository, times(1)).findByCriteria(searchCriteria);
        verify(crazySupplierClient, times(1)).searchFlights(searchCriteria);
    }

    @Test
    void searchFlights_WhenCrazySupplierReturnsEmptyList_ShouldReturnRepositoryFlightsOnly() {
        // Given
        List<Flight> repositoryFlights = List.of(flight);

        when(flightRepository.findByCriteria(searchCriteria)).thenReturn(repositoryFlights);
        when(crazySupplierClient.searchFlights(searchCriteria)).thenReturn(Collections.emptyList());

        // When
        List<Flight> result = flightService.searchFlights(searchCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(flight.getId(), result.getFirst().getId());
        verify(flightRepository, times(1)).findByCriteria(searchCriteria);
        verify(crazySupplierClient, times(1)).searchFlights(searchCriteria);
    }
}
