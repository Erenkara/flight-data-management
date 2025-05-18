package com.wordline.flight_data_management.infrastructure.persistence.adapter;

import com.wordline.flight_data_management.application.port.out.FlightRepository;
import com.wordline.flight_data_management.domain.model.Flight;
import com.wordline.flight_data_management.domain.model.FlightSearchCriteria;
import com.wordline.flight_data_management.infrastructure.persistence.entity.FlightEntity;
import com.wordline.flight_data_management.infrastructure.persistence.mapper.FlightMapper;
import com.wordline.flight_data_management.infrastructure.persistence.repository.FlightJpaRepository;
import com.wordline.flight_data_management.infrastructure.persistence.specification.FlightSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FlightRepositoryAdapter implements FlightRepository {

    private final FlightJpaRepository flightJpaRepository;
    private final FlightMapper flightMapper;
    private final FlightSpecification flightSpecification;

    @Override
    public Flight save(Flight flight) {
        log.debug("Saving flight: {}", flight);
        FlightEntity entity = flightMapper.toEntity(flight);
        FlightEntity savedEntity = flightJpaRepository.save(entity);
        return flightMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Flight> findById(UUID id) {
        log.debug("Finding flight by id: {}", id);
        return flightJpaRepository.findById(id)
                .map(flightMapper::toDomain);
    }

    @Override
    public List<Flight> findAll() {
        log.debug("Finding all flights");
        List<FlightEntity> entities = flightJpaRepository.findAll();
        return flightMapper.toDomainList(entities);
    }

    @Override
    public List<Flight> findByCriteria(FlightSearchCriteria criteria) {
        log.debug("Finding flights by criteria: {}", criteria);

        if (criteria == null) {
            return findAll();
        }

        List<FlightEntity> entities = flightJpaRepository.findAll(
                flightSpecification.byCriteria(criteria),
                Sort.by(Sort.Direction.ASC, "departureTime")
        );

        return flightMapper.toDomainList(entities);
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting flight by id: {}", id);
        flightJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        log.debug("Checking if flight exists by id: {}", id);
        return flightJpaRepository.existsById(id);
    }
}
