package com.dude.dms.ui.utils.converters;

import com.dude.dms.ui.dataproviders.DataProviderUtil;
import com.dude.dms.ui.utils.FormattingUtils;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.templatemodel.ModelEncoder;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class LocalTimeConverter implements ModelEncoder<LocalTime, String>, Converter<String, LocalTime> {

    @Override
    public String encode(LocalTime d) {
        return DataProviderUtil.convertIfNotNull(d, FormattingUtils.HOUR_FORMATTER::format);
    }

    @Override
    public LocalTime decode(String e) {
        return DataProviderUtil.convertIfNotNull(e, p -> LocalTime.parse(p, FormattingUtils.HOUR_FORMATTER));
    }

    @Override
    public Result<LocalTime> convertToModel(String presentation, ValueContext valueContext) {
        try {
            return Result.ok(decode(presentation));
        } catch (DateTimeParseException e) {
            return Result.error("Invalid time");
        }
    }

    @Override
    public String convertToPresentation(LocalTime model, ValueContext valueContext) {
        return encode(model);
    }

}
