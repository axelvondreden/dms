package com.dude.dms.ui.views;

import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;

import static com.dude.dms.backend.brain.OptionKey.*;

@Route(value = Const.PAGE_OPTIONS, layout = MainView.class)
@PageTitle("Options")
public class OptionsView extends VerticalLayout {

    private final TextField dateFormat;

    private final TextField dateScanFormats;

    private final NumberField imageParserDpi;

    private final NumberField pollingInterval;

    private final NumberField maxUploadFileSize;

    private final TextField docSavePath;

    private final ComboBox<Locale> locale;

    private final Checkbox simpleColors;

    private final TextField githubUser;

    private final PasswordField githubPassword;

    private final NumberField updateCheckInterval;

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
        viewDetails.getElement().getStyle().set("padding", "5px")
                .set("border", "2px solid darkgray").set("borderRadius", "5px")
                .set("width", "100%").set("backgroundColor", "ghostwhite");
        add(viewDetails);

        dateScanFormats = new TextField("Date scan formats", DATE_SCAN_FORMATS.getString(), "");
        imageParserDpi = new NumberField("Image Parser DPI", IMAGE_PARSER_DPI.getDouble(), e -> {});
        pollingInterval = new NumberField("Polling interval (seconds)", POLL_INTERVAL.getDouble(), e -> {});
        maxUploadFileSize = new NumberField("Maximum upload file size (MB)", MAX_UPLOAD_FILE_SIZE.getDouble(), e -> {});
        docSavePath = new TextField("Doc save path (absolute or relative to '" + Paths.get("../").toAbsolutePath() + "'", DOC_SAVE_PATH.getString(), "");
        Details docsDetails = new Details("Docs", new FormLayout(dateScanFormats, imageParserDpi, pollingInterval, maxUploadFileSize, docSavePath));
        docsDetails.setOpened(true);
        docsDetails.getElement().getStyle().set("padding", "5px")
                .set("border", "2px solid darkgray").set("borderRadius", "5px")
                .set("width", "100%").set("backgroundColor", "ghostwhite");
        add(docsDetails);

        githubUser = new TextField("Github User", GITHUB_USER.getString(), "");
        githubPassword = new PasswordField("Github Password");
        githubPassword.setValue(GITHUB_PASSWORD.getString());
        updateCheckInterval = new NumberField("Update check interval (minutes)", UPDATE_CHECK_INTERVAL.getDouble(), e -> {});
        Details updateDetails = new Details("Update", new FormLayout(githubUser, githubPassword, updateCheckInterval));
        updateDetails.setOpened(true);
        updateDetails.getElement().getStyle().set("padding", "5px")
                .set("border", "2px solid darkgray").set("borderRadius", "5px")
                .set("width", "100%").set("backgroundColor", "ghostwhite");
        add(updateDetails);
    }

    private void save() {
        if (!DOC_SAVE_PATH.getString().equals(docSavePath.getValue())) {
            File dir = new File(docSavePath.getValue());
            if (dir.exists() && dir.isDirectory()) {
                new ConfirmDialog("Changing archive path!", "Are you sure you want to change the archive path?\nOld files will be copied over to the new directory.", "Change", event -> {
                    try {
                        FileUtils.moveDirectory(new File(DOC_SAVE_PATH.getString(), "img"), new File(dir, "img"));
                        FileUtils.moveDirectory(new File(DOC_SAVE_PATH.getString(), "pdf"), new File(dir, "pdf"));
                        DOC_SAVE_PATH.setString(docSavePath.getValue());
                        Notification.show("Archive moved!");
                    } catch (IOException e) {
                        Notification.show("Error trying to move folder: " + e.getMessage());
                    }
                }, "Cancel", event -> {}).open();
            } else {
                Notification.show("Directory " + docSavePath.getValue() + " does not exist.");
            }
        }
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
                IMAGE_PARSER_DPI.setDouble(imageParserDpi.getValue());
            } catch (NumberFormatException ignored) {
            }
        }
        if (!pollingInterval.isEmpty() && pollingInterval.getValue() > 0) {
            POLL_INTERVAL.setInt(pollingInterval.getValue().intValue());
        }
        if (!maxUploadFileSize.isEmpty() && maxUploadFileSize.getValue() > 0) {
            MAX_UPLOAD_FILE_SIZE.setInt(maxUploadFileSize.getValue().intValue());
        }
        if (!updateCheckInterval.isEmpty() && updateCheckInterval.getValue() > 0) {
            UPDATE_CHECK_INTERVAL.setInt(updateCheckInterval.getValue().intValue());
        }
        SIMPLE_TAG_COLORS.setBoolean(simpleColors.getValue());
        GITHUB_USER.setString(githubUser.getValue());
        GITHUB_PASSWORD.setString(githubPassword.getValue());

        Notification.show("Settings saved!");
    }
}