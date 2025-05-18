package com.wordline.flight_data_management.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wordline.flight_data_management.application.port.in.FlightService;
import com.wordline.flight_data_management.domain.exception.FlightNotFoundException;
import com.wordline.flight_data_management.domain.model.Flight;
import com.wordline.flight_data_management.domain.model.FlightSearchCriteria;
import com.wordline.flight_data_management.infrastructure.rest.dto.CreateFlightRequest;
import com.wordline.flight_data_management.infrastructure.rest.dto.FlightDto;
import com.wordline.flight_data_management.infrastructure.rest.dto.FlightSearchRequest;
import com.wordline.flight_data_management.infrastructure.rest.dto.UpdateFlightRequest;
import com.wordline.flight_data_management.infrastructure.rest.exception.GlobalExceptionHandler;
import com.wordline.flight_data_management.infrastructure.rest.mapper.FlightDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FlightControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private FlightService flightService;

    @Mock
    private FlightDtoMapper flightDtoMapper;

    @InjectMocks
    private FlightController flightController;

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    private UUID flightId;
    private Flight flight;
    private FlightDto flightDto;
    private CreateFlightRequest createFlightRequest;
    private UpdateFlightRequest updateFlightRequest;
    private FlightSearchRequest flightSearchRequest;
    private FlightSearchCriteria flightSearchCriteria;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(flightController)
                .setControllerAdvice(globalExceptionHandler)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

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

        flightDto = FlightDto.builder()
                .id(flightId)
                .airline("TestAirline")
                .supplier("TestSupplier")
                .fare(new BigDecimal("100.00"))
                .departureAirport("AMS")
                .destinationAirport("IST")
                .departureTime(ZonedDateTime.now(ZoneId.of("UTC")))
                .arrivalTime(ZonedDateTime.now(ZoneId.of("UTC")).plusHours(6))
                .build();

        createFlightRequest = CreateFlightRequest.builder()
                .airline("TestAirline")
                .supplier("TestSupplier")
                .fare(new BigDecimal("100.00"))
                .departureAirport("AMS")
                .destinationAirport("IST")
                .departureTime(ZonedDateTime.now(ZoneId.of("UTC")))
                .arrivalTime(ZonedDateTime.now(ZoneId.of("UTC")).plusHours(6))
                .build();

        updateFlightRequest = UpdateFlightRequest.builder()
                .airline("UpdatedAirline")
                .build();

        flightSearchRequest = FlightSearchRequest.builder()
                .departureAirport("AMS")
                .destinationAirport("IST")
                .departureTime(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();

        flightSearchCriteria = FlightSearchCriteria.builder()
                .departureAirport("AMS")
                .destinationAirport("IST")
                .departureTime(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
    }

    @Test
    void createFlight_ShouldReturnCreatedFlight() throws Exception {
        // Given
        when(flightDtoMapper.toDomain(any(CreateFlightRequest.class))).thenReturn(flight);
        when(flightService.createFlight(any(Flight.class))).thenReturn(flight);
        when(flightDtoMapper.toDto(any(Flight.class))).thenReturn(flightDto);

        // When/Then
        mockMvc.perform(post("/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createFlightRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(flightId.toString())))
                .andExpect(jsonPath("$.airline", is("TestAirline")));

        verify(flightDtoMapper, times(1)).toDomain(any(CreateFlightRequest.class));
        verify(flightService, times(1)).createFlight(any(Flight.class));
        verify(flightDtoMapper, times(1)).toDto(any(Flight.class));
    }

    @Test
    void updateFlight_WhenFlightExists_ShouldReturnUpdatedFlight() throws Exception {
        // Given
        when(flightService.getFlightById(flightId)).thenReturn(Optional.of(flight));
        when(flightDtoMapper.toDomain(eq(flightId), any(UpdateFlightRequest.class), any(Flight.class))).thenReturn(flight);
        when(flightService.updateFlight(eq(flightId), any(Flight.class))).thenReturn(flight);
        when(flightDtoMapper.toDto(any(Flight.class))).thenReturn(flightDto);

        // When/Then
        mockMvc.perform(put("/flights/{id}", flightId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateFlightRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(flightId.toString())))
                .andExpect(jsonPath("$.airline", is("TestAirline")));

        verify(flightService, times(1)).getFlightById(flightId);
        verify(flightDtoMapper, times(1)).toDomain(eq(flightId), any(UpdateFlightRequest.class), any(Flight.class));
        verify(flightService, times(1)).updateFlight(eq(flightId), any(Flight.class));
        verify(flightDtoMapper, times(1)).toDto(any(Flight.class));
    }

    @Test
    void updateFlight_WhenFlightDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        when(flightService.getFlightById(flightId)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(put("/flights/{id}", flightId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateFlightRequest)))
                .andExpect(status().isNotFound());

        verify(flightService, times(1)).getFlightById(flightId);
        verify(flightDtoMapper, never()).toDomain(any(UUID.class), any(UpdateFlightRequest.class), any(Flight.class));
        verify(flightService, never()).updateFlight(any(UUID.class), any(Flight.class));
    }

    @Test
    void getFlightById_WhenFlightExists_ShouldReturnFlight() throws Exception {
        // Given
        when(flightService.getFlightById(flightId)).thenReturn(Optional.of(flight));
        when(flightDtoMapper.toDto(any(Flight.class))).thenReturn(flightDto);

        // When/Then
        mockMvc.perform(get("/flights/{id}", flightId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(flightId.toString())))
                .andExpect(jsonPath("$.airline", is("TestAirline")));

        verify(flightService, times(1)).getFlightById(flightId);
        verify(flightDtoMapper, times(1)).toDto(any(Flight.class));
    }

    @Test
    void getFlightById_WhenFlightDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        when(flightService.getFlightById(flightId)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/flights/{id}", flightId))
                .andExpect(status().isNotFound());

        verify(flightService, times(1)).getFlightById(flightId);
        verify(flightDtoMapper, never()).toDto(any(Flight.class));
    }

    @Test
    void getAllFlights_ShouldReturnAllFlights() throws Exception {
        // Given
        when(flightService.getAllFlights()).thenReturn(List.of(flight));
        when(flightDtoMapper.toDtoList(anyList())).thenReturn(List.of(flightDto));

        // When/Then
        mockMvc.perform(get("/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(flightId.toString())))
                .andExpect(jsonPath("$[0].airline", is("TestAirline")));

        verify(flightService, times(1)).getAllFlights();
        verify(flightDtoMapper, times(1)).toDtoList(anyList());
    }

    @Test
    void deleteFlight_WhenFlightExists_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(flightService).deleteFlight(flightId);

        // When/Then
        mockMvc.perform(delete("/flights/{id}", flightId))
                .andExpect(status().isNoContent());

        verify(flightService, times(1)).deleteFlight(flightId);
    }

    @Test
    void deleteFlight_WhenFlightDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        doThrow(FlightNotFoundException.withId(flightId)).when(flightService).deleteFlight(flightId);

        // When/Then
        mockMvc.perform(delete("/flights/{id}", flightId))
                .andExpect(status().isNotFound());

        verify(flightService, times(1)).deleteFlight(flightId);
    }

    @Test
    void searchFlights_ShouldReturnMatchingFlights() throws Exception {
        // Given
        when(flightDtoMapper.toDomain(any(FlightSearchRequest.class))).thenReturn(flightSearchCriteria);
        when(flightService.searchFlights(any(FlightSearchCriteria.class))).thenReturn(List.of(flight));
        when(flightDtoMapper.toDtoList(anyList())).thenReturn(List.of(flightDto));

        // When/Then
        mockMvc.perform(post("/flights/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flightSearchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(flightId.toString())))
                .andExpect(jsonPath("$[0].airline", is("TestAirline")));

        verify(flightDtoMapper, times(1)).toDomain(any(FlightSearchRequest.class));
        verify(flightService, times(1)).searchFlights(any(FlightSearchCriteria.class));
        verify(flightDtoMapper, times(1)).toDtoList(anyList());
    }

    @Test
    void searchFlights_WhenNoMatches_ShouldReturnEmptyList() throws Exception {
        // Given
        when(flightDtoMapper.toDomain(any(FlightSearchRequest.class))).thenReturn(flightSearchCriteria);
        when(flightService.searchFlights(any(FlightSearchCriteria.class))).thenReturn(List.of());
        when(flightDtoMapper.toDtoList(anyList())).thenReturn(List.of());

        // When/Then
        mockMvc.perform(post("/flights/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flightSearchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(flightDtoMapper, times(1)).toDomain(any(FlightSearchRequest.class));
        verify(flightService, times(1)).searchFlights(any(FlightSearchCriteria.class));
        verify(flightDtoMapper, times(1)).toDtoList(anyList());
    }
}
