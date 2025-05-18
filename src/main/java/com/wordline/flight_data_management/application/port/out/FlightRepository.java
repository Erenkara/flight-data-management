package com.wordline.flight_data_management.application.port.out;

import com.wordline.flight_data_management.domain.model.Flight;
import com.wordline.flight_data_management.domain.model.FlightSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlightRepository {

    Flight save(Flight flight);

    Optional<Flight> findById(UUID id);

    List<Flight> findAll();

    List<Flight> findByCriteria(FlightSearchCriteria criteria);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
