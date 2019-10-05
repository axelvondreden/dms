package com.dude.dms.ui.converters;

import com.dude.dms.backend.brain.BrainUtils;
import com.dude.dms.backend.brain.OptionKey;

import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;

public final class LocalDateConverter {

    private LocalDateConverter() {
    }

    public static String convert(ChronoLocalDate localDate) {
        return localDate == null ? null : localDate.format(DateTimeFormatter.ofPattern(BrainUtils.getProperty(OptionKey.DATE_FORMAT)));
    }

}
