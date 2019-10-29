package com.dude.dms.ui.views;

import com.dude.dms.backend.data.tags.Tag;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.github.appreciated.card.Card;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

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

    private final Checkbox darkMode;

    private final Checkbox autoTag;

    private final ComboBox<Tag> autoTagId;

    private final TextField githubUser;

    private final PasswordField githubPassword;

    private final NumberField updateCheckInterval;

    @Autowired
    public OptionsView(TagService tagService) {
        Button save = new Button("Save Settings", VaadinIcon.DISC.create(), e -> save());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(save);

        dateFormat = new TextField("Date format", DATE_FORMAT.getString(), "");
        locale = new ComboBox<>("Locale");
        locale.setItems(Locale.getAvailableLocales());
        locale.setValue(Locale.forLanguageTag(LOCALE.getString()));
        locale.setAllowCustomValue(false);
        locale.setPreventInvalidInput(true);
        simpleColors = new Checkbox("Simple tag colors", SIMPLE_TAG_COLORS.getBoolean());
        darkMode = new Checkbox("Dark mode", DARK_MODE.getBoolean());
        darkMode.addValueChangeListener(event -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            themeList.clear();
            themeList.add(event.getValue() ? Lumo.DARK : Lumo.LIGHT);
        });
        add(createSection("View", locale, dateFormat, simpleColors, darkMode));

        dateScanFormats = new TextField("Date scan formats", DATE_SCAN_FORMATS.getString(), "");
        imageParserDpi = new NumberField("Image Parser DPI", IMAGE_PARSER_DPI.getDouble(), e -> {});
        pollingInterval = new NumberField("Polling interval (seconds)", POLL_INTERVAL.getDouble(), e -> {});
        maxUploadFileSize = new NumberField("Maximum upload file size (MB)", MAX_UPLOAD_FILE_SIZE.getDouble(), e -> {});
        docSavePath = new TextField("Doc save path (absolute or relative to '" + Paths.get("../").toAbsolutePath() + "'", DOC_SAVE_PATH.getString(), "");
        add(createSection("Docs", dateScanFormats, imageParserDpi, pollingInterval, maxUploadFileSize, docSavePath));

        autoTag = new Checkbox("Auto tag", AUTO_TAG.getBoolean());
        autoTagId = new ComboBox<>("Auto tag");
        autoTagId.setPreventInvalidInput(true);
        autoTagId.setAllowCustomValue(false);
        autoTagId.setItems(tagService.findAll());
        autoTagId.setValue(tagService.load(AUTO_TAG_ID.getLong()));
        autoTagId.setReadOnly(!autoTag.getValue());
        autoTagId.setItemLabelGenerator(Tag::getName);
        autoTag.addValueChangeListener(event -> autoTagId.setReadOnly(!event.getValue()));
        add(createSection("Tags", autoTag, autoTagId));

        githubUser = new TextField("Github User", GITHUB_USER.getString(), "");
        githubPassword = new PasswordField("Github Password");
        githubPassword.setValue(GITHUB_PASSWORD.getString());
        updateCheckInterval = new NumberField("Update check interval (minutes)", UPDATE_CHECK_INTERVAL.getDouble(), e -> {});
        add(createSection("Update", githubUser, githubPassword, updateCheckInterval));
    }

    private Card createSection(String title, Component... components) {
        Details details = new Details(title, new FormLayout(components));
        details.setOpened(true);
        details.getElement().getStyle().set("padding", "5px").set("width", "100%");
        Card card = new Card(details);
        card.setWidthFull();
        return card;
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
        AUTO_TAG.setBoolean(autoTag.getValue());
        AUTO_TAG_ID.setLong(autoTagId.getValue().getId());
        DARK_MODE.setBoolean(darkMode.getValue());
        SIMPLE_TAG_COLORS.setBoolean(simpleColors.getValue());
        GITHUB_USER.setString(githubUser.getValue());
        GITHUB_PASSWORD.setString(githubPassword.getValue());

        Notification.show("Settings saved!");
    }
}