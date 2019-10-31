package com.dude.dms.ui.converters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.dude.dms.backend.brain.OptionKey.DATE_FORMAT;

public final class LocalDateTimeConverter {

    private LocalDateTimeConverter() {
    }

    public static String convert(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT.getString() + " HH:mm:ss"));
    }
}