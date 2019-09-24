package com.dude.dms.ui.utils.converters;

import com.dude.dms.ui.dataproviders.DataProviderUtil;
import com.dude.dms.ui.utils.FormattingUtils;
import com.vaadin.flow.templatemodel.ModelEncoder;

import java.time.LocalDateTime;

public class LocalDateTimeConverter implements ModelEncoder<LocalDateTime, String> {


    private static final LocalTimeConverter TIME_FORMATTER = new LocalTimeConverter();

    @Override
    public String encode(LocalDateTime d) {
        return DataProviderUtil.convertIfNotNull(d,
                v -> FormattingUtils.FULL_DATE_FORMATTER.format(v) + ' ' + TIME_FORMATTER.encode(v.toLocalTime()));
    }

    @Override
    public LocalDateTime decode(String e) {
        throw new UnsupportedOperationException();
    }
}
