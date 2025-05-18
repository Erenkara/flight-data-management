package com.wordline.flight_data_management.application.port.out;

import com.wordline.flight_data_management.domain.model.CrazySupplierFlight;
import com.wordline.flight_data_management.domain.model.FlightSearchCriteria;

import java.util.List;

public interface CrazySupplierClient {

    List<CrazySupplierFlight> searchFlights(FlightSearchCriteria criteria);
}
