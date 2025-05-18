package com.wordline.flight_data_management.infrastructure.external.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrazySupplierRequest {
    private String from;
    private String to;
    private LocalDateTime outboundDate;
    private LocalDateTime inboundDate;
}
