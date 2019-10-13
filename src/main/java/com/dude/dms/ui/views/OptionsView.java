package com.dude.dms.ui.views;

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
    private final TextField imageParserDpi;
    private final ComboBox<Locale> locale;

    public OptionsView() {
        dateFormat = new TextField("Date format", DATE_FORMAT.getString(), "");
        dateScanFormats = new TextField("Date scan formats", DATE_SCAN_FORMATS.getString(), "");
        imageParserDpi = new TextField("Image Parser DPI", IMAGE_PARSER_DPI.getString(), "");
        locale = new ComboBox<>("Locale");
        locale.setItems(Locale.getAvailableLocales());
        locale.setValue(Locale.forLanguageTag(LOCALE.getString()));
        locale.setAllowCustomValue(false);
        locale.setPreventInvalidInput(true);
        Button save = new Button("Save", e -> save());
        add(dateFormat, dateScanFormats, imageParserDpi, locale, save);
    }

    private void save() {
        if (!dateFormat.isEmpty()) {
            DATE_FORMAT.setString(dateFormat.getValue());
        }
        if (!dateScanFormats.isEmpty()) {
            DATE_SCAN_FORMATS.setString(dateScanFormats.getValue());
        }
        if (!locale.isEmpty()) {
            LOCALE.setString(locale.getValue().toLanguageTag());
        }
        if (!imageParserDpi.isEmpty()) {
            try {
                IMAGE_PARSER_DPI.setFloat(Float.parseFloat(imageParserDpi.getValue()));
            } catch (NumberFormatException ignored) {
            }
        }
    }
}