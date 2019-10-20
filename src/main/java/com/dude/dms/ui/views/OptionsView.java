package com.dude.dms.ui.views;

import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
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

    private final Checkbox simpleColors;

    private final TextField githubUser;

    private final PasswordField githubPassword;

    public OptionsView() {
        Button save = new Button("Save Settings", e -> save());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(save);

        dateFormat = new TextField("Date format", DATE_FORMAT.getString(), "");
        locale = new ComboBox<>("Locale");
        locale.setItems(Locale.getAvailableLocales());
        locale.setValue(Locale.forLanguageTag(LOCALE.getString()));
        locale.setAllowCustomValue(false);
        locale.setPreventInvalidInput(true);
        simpleColors = new Checkbox("Simple tag colors", SIMPLE_TAG_COLORS.getBoolean());
        Details viewDetails = new Details("View", new FormLayout(locale, dateFormat, simpleColors));
        viewDetails.setOpened(true);
        add(viewDetails);

        dateScanFormats = new TextField("Date scan formats", DATE_SCAN_FORMATS.getString());
        imageParserDpi = new TextField("Image Parser DPI", IMAGE_PARSER_DPI.getString(), "");
        Details docsDetails = new Details("Docs", new FormLayout(dateScanFormats, imageParserDpi));
        docsDetails.setOpened(true);
        add(docsDetails);

        githubUser = new TextField("Github User", GITHUB_USER.getString(), "");
        githubPassword = new PasswordField("Github Password");
        githubPassword.setValue(GITHUB_PASSWORD.getString());
        Details updateDetails = new Details("Update", new FormLayout(githubUser, githubPassword));
        updateDetails.setOpened(true);
        add(updateDetails);
    }

    private void save() {
        if (!dateFormat.isEmpty()) {
            DATE_FORMAT.setString(dateFormat.getValue());
        }
        if (!dateScanFormats.isEmpty()) {
            DATE_SCAN_FORMATS.setString(String.join(",", dateScanFormats.getValue()));
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
        SIMPLE_TAG_COLORS.setBoolean(simpleColors.getValue());
        GITHUB_USER.setString(githubUser.getValue());
        GITHUB_PASSWORD.setString(githubPassword.getValue());

        Notification.show("Settings saved!");
    }
}