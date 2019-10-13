package com.dude.dms.ui.converters;

import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;

import static com.dude.dms.backend.brain.OptionKey.DATE_FORMAT;

public final class LocalDateConverter {

    private LocalDateConverter() {
    }

    public static String convert(ChronoLocalDate localDate) {
        return localDate == null ? null : localDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT.getString()));
    }
}