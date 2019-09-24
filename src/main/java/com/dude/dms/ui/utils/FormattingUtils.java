package com.dude.dms.ui.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;

public class FormattingUtils {

    private static final String DECIMAL_ZERO = "0.00";

    /**
     * 3 letter month name + day number E.g: Nov 20
     */
    public static final DateTimeFormatter MONTH_AND_DAY_FORMATTER = DateTimeFormatter.ofPattern("MMM d", Const.APP_LOCALE);

    /**
     * Full day name. E.g: Monday.
     */
    public static final DateTimeFormatter WEEKDAY_FULLNAME_FORMATTER = DateTimeFormatter.ofPattern("EEEE", Const.APP_LOCALE);

    private FormattingUtils() {
    }

    public static DecimalFormat getUiPriceFormatter() {
        DecimalFormat formatter = new DecimalFormat('#' + DECIMAL_ZERO, DecimalFormatSymbols.getInstance(Const.APP_LOCALE));
        formatter.setGroupingUsed(false);
        return formatter;
    }
}
