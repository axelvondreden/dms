package com.dude.dms.ui.views;

import com.dude.dms.backend.brain.BrainUtils;
import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Locale;

import static com.dude.dms.backend.brain.OptionKey.*;

@Route(value = Const.PAGE_OPTIONS, layout = MainView.class)
@PageTitle("Options")
public class OptionsView extends VerticalLayout {

    private final TextField dateFormat;
    private final TextField dateScanFormats;
    private final ComboBox<Locale> locale;

    public OptionsView() {
        dateFormat = new TextField("Date format", BrainUtils.getProperty(DATE_FORMAT), "");
        dateScanFormats = new TextField("Date scan formats", BrainUtils.getProperty(DATE_SCAN_FORMATS), "");
        locale = new ComboBox<>("Locale");
        locale.setItems(Locale.getAvailableLocales());
        locale.setValue(Locale.forLanguageTag(BrainUtils.getProperty(LOCALE)));
        locale.setAllowCustomValue(false);
        locale.setPreventInvalidInput(true);
        Button save = new Button("Save", e -> save());
        add(dateFormat, dateScanFormats, locale, save);
    }

    private void save() {
        if (!dateFormat.isEmpty()) {
            BrainUtils.setProperty(DATE_FORMAT, dateFormat.getValue());
        }
        if (!dateScanFormats.isEmpty()) {
            BrainUtils.setProperty(DATE_SCAN_FORMATS, dateScanFormats.getValue());
        }
        if (!locale.isEmpty()) {
            BrainUtils.setProperty(LOCALE, locale.getValue().toLanguageTag());
        }
    }
}