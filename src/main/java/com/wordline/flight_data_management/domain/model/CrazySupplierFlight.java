package com.wordline.flight_data_management.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrazySupplierFlight {
    private String carrier;
    private BigDecimal basePrice;
    private BigDecimal tax;
    private String departureAirportName;
    private String arrivalAirportName;
    private LocalDateTime outboundDateTime;
    private LocalDateTime inboundDateTime;
}
