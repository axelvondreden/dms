package com.dude.dms.ui.components.standard;

import com.dude.dms.backend.brain.BrainUtils;
import com.dude.dms.backend.brain.OptionKey;
import com.vaadin.flow.component.datepicker.DatePicker;

import java.util.Locale;

public class DmsDatePicker extends DatePicker {

    public DmsDatePicker() {
        setLocale(Locale.forLanguageTag(BrainUtils.getProperty(OptionKey.LOCALE)));
    }

}
