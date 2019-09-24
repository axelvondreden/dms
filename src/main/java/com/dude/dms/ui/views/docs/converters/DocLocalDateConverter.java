package com.dude.dms.ui.views.docs.converters;

import com.dude.dms.ui.utils.FormattingUtils;
import com.vaadin.flow.templatemodel.ModelEncoder;

import java.time.LocalDate;

/**
 * Date converter specific for the way date is displayed in storefront.
 */
public class DocLocalDateConverter implements ModelEncoder<LocalDate, DocDate> {

    @Override
    public DocDate encode(LocalDate d) {
        DocDate result = null;
        if (d != null) {
            result = new DocDate();
            result.setDay(FormattingUtils.MONTH_AND_DAY_FORMATTER.format(d));
            result.setWeekday(FormattingUtils.WEEKDAY_FULLNAME_FORMATTER.format(d));
            result.setDate(d.toString());
        }
        return result;
    }

    @Override
    public LocalDate decode(DocDate e) {
        throw new UnsupportedOperationException();
    }
}
