package com.wordline.flight_data_management.infrastructure.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimezoneConverter {

    public static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    public static final ZoneId CET_ZONE = ZoneId.of("CET");


    public static LocalDateTime convertZonedDateTimeToLocalDateCet(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return null;
        }
        return zonedDateTime.withZoneSameInstant(CET_ZONE).toLocalDateTime();
    }

    public static LocalDateTime convertLocalDateTimeFromCetToUtc(LocalDateTime cetDateTime) {
        if (cetDateTime == null) {
            return null;
        }
        return cetDateTime.atZone(CET_ZONE)
                .withZoneSameInstant(UTC_ZONE)
                .toLocalDateTime();
    }
}
