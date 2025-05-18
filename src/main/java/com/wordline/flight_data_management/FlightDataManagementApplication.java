package com.wordline.flight_data_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableRetry
@EnableAsync
public class FlightDataManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightDataManagementApplication.class, args);
    }

}
