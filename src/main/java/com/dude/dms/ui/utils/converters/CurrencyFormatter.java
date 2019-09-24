package com.dude.dms.ui.utils.converters;

import com.dude.dms.ui.dataproviders.DataProviderUtil;
import com.dude.dms.ui.utils.FormattingUtils;
import com.vaadin.flow.templatemodel.ModelEncoder;

public class CurrencyFormatter implements ModelEncoder<Integer, String> {

    @Override
    public String encode(Integer d) {
        return DataProviderUtil.convertIfNotNull(d, FormattingUtils::formatAsCurrency);
    }

    @Override
    public Integer decode(String e) {
        throw new UnsupportedOperationException();
    }
}
