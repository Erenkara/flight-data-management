package com.wordline.flight_data_management.infrastructure.persistence.adapter;

import com.wordline.flight_data_management.domain.model.Flight;
import com.wordline.flight_data_management.domain.model.FlightSearchCriteria;
import com.wordline.flight_data_management.infrastructure.persistence.entity.FlightEntity;
import com.wordline.flight_data_management.infrastructure.persistence.mapper.FlightMapper;
import com.wordline.flight_data_management.infrastructure.persistence.repository.FlightJpaRepository;
import com.wordline.flight_data_management.infrastructure.persistence.specification.FlightSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightRepositoryAdapterTest {

    @Mock
    private FlightJpaRepository flightJpaRepository;

    @Mock
    private FlightMapper flightMapper;

    @Mock
    private FlightSpecification flightSpecification;

    @InjectMocks
    private FlightRepositoryAdapter flightRepositoryAdapter;

    private UUID flightId;
    private Flight flight;
    private FlightEntity flightEntity;

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

        flightEntity = new FlightEntity();
        flightEntity.setId(flightId);
        flightEntity.setAirline("TestAirline");
        flightEntity.setSupplier("TestSupplier");
        flightEntity.setFare(new BigDecimal("100.00"));
        flightEntity.setDepartureAirport("AMS");
        flightEntity.setDestinationAirport("IST");
        flightEntity.setDepartureTime(ZonedDateTime.now(ZoneId.of("UTC")));
        flightEntity.setArrivalTime(ZonedDateTime.now(ZoneId.of("UTC")).plusHours(6));

    }

    @Test
    void save_ShouldReturnSavedFlight() {
        // Given
        when(flightMapper.toEntity(flight)).thenReturn(flightEntity);
        when(flightJpaRepository.save(flightEntity)).thenReturn(flightEntity);
        when(flightMapper.toDomain(flightEntity)).thenReturn(flight);

        // When
        Flight result = flightRepositoryAdapter.save(flight);

        // Then
        assertNotNull(result);
        assertEquals(flight.getId(), result.getId());
        assertEquals(flight.getAirline(), result.getAirline());
        verify(flightMapper, times(1)).toEntity(flight);
        verify(flightJpaRepository, times(1)).save(flightEntity);
        verify(flightMapper, times(1)).toDomain(flightEntity);
    }

    @Test
    void findById_WhenFlightExists_ShouldReturnFlight() {
        // Given
        when(flightJpaRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));
        when(flightMapper.toDomain(flightEntity)).thenReturn(flight);

        // When
        Optional<Flight> result = flightRepositoryAdapter.findById(flightId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(flight.getId(), result.get().getId());
        assertEquals(flight.getAirline(), result.get().getAirline());
        verify(flightJpaRepository, times(1)).findById(flightId);
        verify(flightMapper, times(1)).toDomain(flightEntity);
    }

    @Test
    void findById_WhenFlightDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(flightJpaRepository.findById(flightId)).thenReturn(Optional.empty());

        // When
        Optional<Flight> result = flightRepositoryAdapter.findById(flightId);

        // Then
        assertFalse(result.isPresent());
        verify(flightJpaRepository, times(1)).findById(flightId);
        verify(flightMapper, never()).toDomain(any(FlightEntity.class));
    }

    @Test
    void findAll_ShouldReturnAllFlights() {
        // Given
        List<FlightEntity> entities = List.of(flightEntity);
        List<Flight> flights = List.of(flight);
        when(flightJpaRepository.findAll()).thenReturn(entities);
        when(flightMapper.toDomainList(entities)).thenReturn(flights);

        // When
        List<Flight> result = flightRepositoryAdapter.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(flight.getId(), result.getFirst().getId());
        assertEquals(flight.getAirline(), result.getFirst().getAirline());
        verify(flightJpaRepository, times(1)).findAll();
        verify(flightMapper, times(1)).toDomainList(entities);
    }

    @Test
    void findByCriteria_WithNullCriteria_ShouldReturnAllFlights() {
        // Given
        List<FlightEntity> entities = List.of(flightEntity);
        List<Flight> flights = List.of(flight);
        when(flightJpaRepository.findAll()).thenReturn(entities);
        when(flightMapper.toDomainList(entities)).thenReturn(flights);

        // When
        List<Flight> result = flightRepositoryAdapter.findByCriteria(null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(flight.getId(), result.getFirst().getId());
        verify(flightJpaRepository, times(1)).findAll();
        verify(flightMapper, times(1)).toDomainList(entities);
    }

    @Test
    void findByCriteria_WithAirlineOnly_ShouldReturnMatchingFlights() {
        // Given
        FlightSearchCriteria criteria = FlightSearchCriteria.builder()
                .airline("TestAirline")
                .build();
        List<FlightEntity> entities = List.of(flightEntity);
        List<Flight> flights = List.of(flight);

        Specification<FlightEntity> spec = mock(Specification.class);
        when(flightSpecification.byCriteria(criteria)).thenReturn(spec);
        when(flightJpaRepository.findAll(eq(spec), any(Sort.class))).thenReturn(entities);
        when(flightMapper.toDomainList(entities)).thenReturn(flights);

        // When
        List<Flight> result = flightRepositoryAdapter.findByCriteria(criteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(flight.getId(), result.getFirst().getId());
        verify(flightSpecification, times(1)).byCriteria(criteria);
        verify(flightJpaRepository, times(1)).findAll(eq(spec), any(Sort.class));
        verify(flightMapper, times(1)).toDomainList(entities);
    }

    @Test
    void findByCriteria_WithDepartureAndDestinationAirports_ShouldReturnMatchingFlights() {
        // Given
        FlightSearchCriteria criteria = FlightSearchCriteria.builder()
                .departureAirport("AMS")
                .destinationAirport("IST")
                .build();
        List<FlightEntity> entities = List.of(flightEntity);
        List<Flight> flights = List.of(flight);

        Specification<FlightEntity> spec = mock(Specification.class);
        when(flightSpecification.byCriteria(criteria)).thenReturn(spec);
        when(flightJpaRepository.findAll(eq(spec), any(Sort.class))).thenReturn(entities);
        when(flightMapper.toDomainList(entities)).thenReturn(flights);

        // When
        List<Flight> result = flightRepositoryAdapter.findByCriteria(criteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(flight.getId(), result.getFirst().getId());
        verify(flightSpecification, times(1)).byCriteria(criteria);
        verify(flightJpaRepository, times(1)).findAll(eq(spec), any(Sort.class));
        verify(flightMapper, times(1)).toDomainList(entities);
    }

    @Test
    void findByCriteria_WithDepartureDestinationAndDate_ShouldReturnMatchingFlights() {
        // Given
        FlightSearchCriteria criteria = FlightSearchCriteria.builder()
                .departureAirport("AMS")
                .destinationAirport("IST")
                .departureTime(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
        List<FlightEntity> entities = List.of(flightEntity);
        List<Flight> flights = List.of(flight);

        Specification<FlightEntity> spec = mock(Specification.class);
        when(flightSpecification.byCriteria(criteria)).thenReturn(spec);
        when(flightJpaRepository.findAll(eq(spec), any(Sort.class))).thenReturn(entities);
        when(flightMapper.toDomainList(entities)).thenReturn(flights);

        // When
        List<Flight> result = flightRepositoryAdapter.findByCriteria(criteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(flight.getId(), result.getFirst().getId());
        verify(flightSpecification, times(1)).byCriteria(criteria);
        verify(flightJpaRepository, times(1)).findAll(eq(spec), any(Sort.class));
        verify(flightMapper, times(1)).toDomainList(entities);
    }

    @Test
    void findByCriteria_WithAllCriteria_ShouldReturnMatchingFlights() {
        // Given
        FlightSearchCriteria criteria = FlightSearchCriteria.builder()
                .departureAirport("AMS")
                .destinationAirport("IST")
                .airline("TestAirline")
                .departureTime(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
        List<FlightEntity> entities = List.of(flightEntity);
        List<Flight> flights = List.of(flight);

        Specification<FlightEntity> spec = mock(Specification.class);
        when(flightSpecification.byCriteria(criteria)).thenReturn(spec);
        when(flightJpaRepository.findAll(eq(spec), any(Sort.class))).thenReturn(entities);
        when(flightMapper.toDomainList(entities)).thenReturn(flights);

        // When
        List<Flight> result = flightRepositoryAdapter.findByCriteria(criteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(flight.getId(), result.getFirst().getId());
        verify(flightSpecification, times(1)).byCriteria(criteria);
        verify(flightJpaRepository, times(1)).findAll(eq(spec), any(Sort.class));
        verify(flightMapper, times(1)).toDomainList(entities);
    }

    @Test
    void findByCriteria_WithUnmatchedCriteria_ShouldReturnEmptyList() {
        // Given
        FlightSearchCriteria criteria = FlightSearchCriteria.builder()
                .departureAirport("AMS")
                .build();

        Specification<FlightEntity> spec = mock(Specification.class);
        when(flightSpecification.byCriteria(criteria)).thenReturn(spec);
        when(flightJpaRepository.findAll(eq(spec), any(Sort.class))).thenReturn(List.of());

        // When
        List<Flight> result = flightRepositoryAdapter.findByCriteria(criteria);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(flightSpecification, times(1)).byCriteria(criteria);
        verify(flightJpaRepository, times(1)).findAll(eq(spec), any(Sort.class));
        verify(flightMapper, times(1)).toDomainList(List.of());
    }

    @Test
    void deleteById_ShouldDeleteFlight() {
        // Given
        doNothing().when(flightJpaRepository).deleteById(flightId);

        // When
        flightRepositoryAdapter.deleteById(flightId);

        // Then
        verify(flightJpaRepository, times(1)).deleteById(flightId);
    }

    @Test
    void existsById_WhenFlightExists_ShouldReturnTrue() {
        // Given
        when(flightJpaRepository.existsById(flightId)).thenReturn(true);

        // When
        boolean result = flightRepositoryAdapter.existsById(flightId);

        // Then
        assertTrue(result);
        verify(flightJpaRepository, times(1)).existsById(flightId);
    }

    @Test
    void existsById_WhenFlightDoesNotExist_ShouldReturnFalse() {
        // Given
        when(flightJpaRepository.existsById(flightId)).thenReturn(false);

        // When
        boolean result = flightRepositoryAdapter.existsById(flightId);

        // Then
        assertFalse(result);
        verify(flightJpaRepository, times(1)).existsById(flightId);
    }
}
