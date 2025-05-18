package com.wordline.flight_data_management.infrastructure.persistence.repository;

import com.wordline.flight_data_management.infrastructure.persistence.entity.FlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FlightJpaRepository extends JpaRepository<FlightEntity, UUID>, JpaSpecificationExecutor<FlightEntity> {
}
