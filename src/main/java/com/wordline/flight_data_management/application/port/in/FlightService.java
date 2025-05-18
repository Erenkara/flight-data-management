package com.wordline.flight_data_management.application.port.in;

import com.wordline.flight_data_management.domain.model.Flight;
import com.wordline.flight_data_management.domain.model.FlightSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlightService {

    Flight createFlight(Flight flight);

    Flight updateFlight(UUID id, Flight flight);

    Optional<Flight> getFlightById(UUID id);

    List<Flight> getAllFlights();

    List<Flight> searchFlights(FlightSearchCriteria criteria);

    void deleteFlight(UUID id);
}
