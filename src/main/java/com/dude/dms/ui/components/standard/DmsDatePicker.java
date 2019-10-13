package com.dude.dms.ui.components.standard;

import com.vaadin.flow.component.datepicker.DatePicker;

import java.util.Locale;

import static com.dude.dms.backend.brain.OptionKey.LOCALE;

public class DmsDatePicker extends DatePicker {

    public DmsDatePicker() {
        this("");
    }

    public DmsDatePicker(String label) {
        super(label);
        setLocale(Locale.forLanguageTag(LOCALE.getString()));
    }
}