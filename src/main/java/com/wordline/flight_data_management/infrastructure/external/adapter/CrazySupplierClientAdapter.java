package com.wordline.flight_data_management.infrastructure.external.adapter;

import com.wordline.flight_data_management.application.port.out.CrazySupplierClient;
import com.wordline.flight_data_management.domain.exception.ExternalServiceException;
import com.wordline.flight_data_management.domain.model.CrazySupplierFlight;
import com.wordline.flight_data_management.domain.model.FlightSearchCriteria;
import com.wordline.flight_data_management.infrastructure.external.model.CrazySupplierRequest;
import com.wordline.flight_data_management.infrastructure.external.model.CrazySupplierResponse;
import com.wordline.flight_data_management.infrastructure.util.TimezoneConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CrazySupplierClientAdapter implements CrazySupplierClient {

    private static final String SERVICE_NAME = "CrazySupplier";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final WebClient crazySupplierWebClient;

    @Override
    @Cacheable(value = "flights", key = "'crazySupplier:' + #criteria.toString()")
    @Retryable(
            value = {ExternalServiceException.class, WebClientResponseException.class},
            maxAttemptsExpression = "${crazysupplier.api.retry.max-attempts}",
            backoff = @Backoff(
                    delayExpression = "${crazysupplier.api.retry.delay}"
            )
    )
    public List<CrazySupplierFlight> searchFlights(FlightSearchCriteria criteria) {
        log.debug("Searching flights from CrazySupplier with criteria: {}", criteria);

        if (criteria == null || criteria.getDepartureAirport() == null ||
                criteria.getDestinationAirport() == null || criteria.getDepartureTime() == null) {
            return Collections.emptyList();
        }

        LocalDateTime outboundTime = TimezoneConverter.convertZonedDateTimeToLocalDateCet(criteria.getDepartureTime());

        LocalDateTime inboundTime = TimezoneConverter.convertZonedDateTimeToLocalDateCet(criteria.getArrivalTime());

        CrazySupplierRequest request = CrazySupplierRequest.builder()
                .from(criteria.getDepartureAirport())
                .to(criteria.getDestinationAirport())
                .outboundDate(outboundTime)
                .inboundDate(inboundTime)
                .build();

        try {
            List<CrazySupplierResponse> responses = crazySupplierWebClient.post()
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                        log.error("Error response from CrazySupplier API: {}", response.statusCode());
                        return Mono.error(new ExternalServiceException(
                                "Error response from CrazySupplier API: " + response.statusCode(),
                                SERVICE_NAME,
                                response.statusCode().value()));
                    })
                    .bodyToFlux(CrazySupplierResponse.class)
                    .collectList()
                    .timeout(TIMEOUT)
                    .block();

            if (responses == null) {
                return Collections.emptyList();
            }

            return responses.stream()
                    .map(this::mapToDomainModel)
                    .collect(Collectors.toList());

        } catch (WebClientResponseException e) {
            log.error("WebClientResponseException when calling CrazySupplier API", e);
            throw new ExternalServiceException(
                    "Error calling CrazySupplier API: " + e.getMessage(),
                    SERVICE_NAME,
                    e.getStatusCode().value());
        } catch (Exception e) {
            log.error("Exception when calling CrazySupplier API", e);
            throw new ExternalServiceException(
                    "Error calling CrazySupplier API: " + e.getMessage(),
                    SERVICE_NAME,
                    500);
        }
    }

    private CrazySupplierFlight mapToDomainModel(CrazySupplierResponse response) {

        LocalDateTime outboundDateTime = TimezoneConverter.convertLocalDateTimeFromCetToUtc(response.getOutboundDateTime());

        LocalDateTime inboundDateTime = TimezoneConverter.convertLocalDateTimeFromCetToUtc(response.getInboundDateTime());

        return CrazySupplierFlight.builder()
                .carrier(response.getCarrier())
                .basePrice(response.getBasePrice())
                .tax(response.getTax())
                .departureAirportName(response.getDepartureAirportName())
                .arrivalAirportName(response.getArrivalAirportName())
                .outboundDateTime(outboundDateTime)
                .inboundDateTime(inboundDateTime)
                .build();
    }
}
