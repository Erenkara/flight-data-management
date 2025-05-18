package com.wordline.flight_data_management.infrastructure.persistence.specification;

import com.wordline.flight_data_management.domain.model.FlightSearchCriteria;
import com.wordline.flight_data_management.infrastructure.persistence.entity.FlightEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class FlightSpecification {

    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");


    public Specification<FlightEntity> byCriteria(FlightSearchCriteria criteria) {
        return (Root<FlightEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getDepartureAirport() != null && !criteria.getDepartureAirport().isEmpty()) {
                predicates.add(cb.equal(root.get("departureAirport"), criteria.getDepartureAirport()));
            }

            if (criteria.getDestinationAirport() != null && !criteria.getDestinationAirport().isEmpty()) {
                predicates.add(cb.equal(root.get("destinationAirport"), criteria.getDestinationAirport()));
            }

            if (criteria.getAirline() != null && !criteria.getAirline().isEmpty()) {
                predicates.add(cb.equal(root.get("airline"), criteria.getAirline()));
            }

            if (criteria.getDepartureTime() != null) {
                ZonedDateTime departureDateTime = criteria.getDepartureTime();
                ZonedDateTime departureStart = departureDateTime.toLocalDate().atStartOfDay(UTC_ZONE);
                ZonedDateTime departureEnd = departureDateTime.toLocalDate().plusDays(1).atStartOfDay(UTC_ZONE);

                predicates.add(cb.greaterThanOrEqualTo(root.get("departureTime"), departureStart));
                predicates.add(cb.lessThan(root.get("departureTime"), departureEnd));
            }

            if (criteria.getArrivalTime() != null) {
                ZonedDateTime arrivalDateTime = criteria.getArrivalTime();
                ZonedDateTime arrivalStart = arrivalDateTime.toLocalDate().atStartOfDay(UTC_ZONE);
                ZonedDateTime arrivalEnd = arrivalDateTime.toLocalDate().plusDays(1).atStartOfDay(UTC_ZONE);

                predicates.add(cb.greaterThanOrEqualTo(root.get("arrivalTime"), arrivalStart));
                predicates.add(cb.lessThan(root.get("arrivalTime"), arrivalEnd));
            }

            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
