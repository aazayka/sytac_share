package com.aazayka.services;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class DateConverter {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss Z yyyy", Locale.ENGLISH);

    public long convertToEpochSeconds(String dateTimeStr) {
        return ZonedDateTime.parse(dateTimeStr, DATE_FORMATTER).toEpochSecond();
    }
}
