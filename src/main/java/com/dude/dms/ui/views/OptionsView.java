package com.dude.dms.ui.views;

import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.Notify;
import com.github.appreciated.card.Card;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

    @Autowired
    public OptionsView(TagService tagService) {
        TextField dateFormat = new TextField("Date format", DATE_FORMAT.getString(), "");
        dateFormat.addValueChangeListener(event -> {
            if (!dateFormat.isEmpty()) {
                DATE_FORMAT.setString(dateFormat.getValue());
                Notify.info("Date format saved.");
            }
        });
        ComboBox<Locale> locale = new ComboBox<>("Language");
        locale.setItems(Locale.getAvailableLocales());
        locale.setValue(Locale.forLanguageTag(LOCALE.getString()));
        locale.setAllowCustomValue(false);
        locale.setPreventInvalidInput(true);
        locale.addValueChangeListener(event -> {
            if (!locale.isEmpty()) {
                LOCALE.setString(locale.getValue().toLanguageTag());
                Notify.info("Language changed.");
            }
        });
        Checkbox simpleColors = new Checkbox("Simple tag colors", SIMPLE_TAG_COLORS.getBoolean());
        simpleColors.addValueChangeListener(event -> {
            SIMPLE_TAG_COLORS.setBoolean(simpleColors.getValue());
            Notify.info("Simple tag colors saved");
        });
        Checkbox darkMode = new Checkbox("Dark mode", DARK_MODE.getBoolean());
        darkMode.addValueChangeListener(event -> {
            DARK_MODE.setBoolean(darkMode.getValue());
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            themeList.clear();
            themeList.add(event.getValue() ? Lumo.DARK : Lumo.LIGHT);
            Notify.info("Dark mode saved.");
        });
        ComboBox<Notification.Position> notifyPosition = new ComboBox<>("Notification position");
        notifyPosition.setItems(Notification.Position.values());
        notifyPosition.setValue(Notification.Position.valueOf(NOTIFY_POSITION.getString()));
        notifyPosition.setAllowCustomValue(false);
        notifyPosition.setPreventInvalidInput(true);
        notifyPosition.setWidthFull();
        notifyPosition.addValueChangeListener(event -> {
            if (!notifyPosition.isEmpty()) {
                NOTIFY_POSITION.setString(notifyPosition.getValue().name());
                Notify.info("Notification position saved.");
            }
        });
        Button notifyTest = new Button("Test", e -> Notify.info("Hi, I am just a test!"));
        HorizontalLayout notifyWrapper = new HorizontalLayout(notifyPosition, notifyTest);
        notifyWrapper.setWidthFull();
        notifyWrapper.setAlignItems(Alignment.END);
        add(createSection("View", locale, dateFormat, simpleColors, darkMode, notifyWrapper));

        TextField dateScanFormats = new TextField("Date scan formats", DATE_SCAN_FORMATS.getString(), "");
        dateScanFormats.addValueChangeListener(event -> {
            if (!dateScanFormats.isEmpty()) {
                DATE_SCAN_FORMATS.setString(String.join(",", dateScanFormats.getValue()));
                Notify.info("Date scan formats saved.");
            }
        });
        NumberField imageParserDpi = new NumberField("Image Parser DPI", IMAGE_PARSER_DPI.getDouble(), e -> {});
        imageParserDpi.addValueChangeListener(event -> {
            if (!imageParserDpi.isEmpty()) {
                try {
                    IMAGE_PARSER_DPI.setDouble(imageParserDpi.getValue());
                    Notify.info("Image parser DPI saved.");
                } catch (NumberFormatException ignored) {
                }
            }
        });
        NumberField pollingInterval = new NumberField("Polling interval (seconds)", POLL_INTERVAL.getDouble(), e -> {});
        pollingInterval.addValueChangeListener(event -> {
            if (!pollingInterval.isEmpty() && pollingInterval.getValue() > 0) {
                POLL_INTERVAL.setInt(pollingInterval.getValue().intValue());
                Notify.info("Polling interval saved.");
            }
        });
        NumberField maxUploadFileSize = new NumberField("Maximum upload file size (MB)", MAX_UPLOAD_FILE_SIZE.getDouble(), e -> {});
        maxUploadFileSize.addValueChangeListener(event -> {
            if (!maxUploadFileSize.isEmpty() && maxUploadFileSize.getValue() > 0) {
                MAX_UPLOAD_FILE_SIZE.setInt(maxUploadFileSize.getValue().intValue());
                Notify.info("Maximum upload file size saved.");
            }
        });
        TextField docSavePath = new TextField("Doc save path (absolute or relative to '" + Paths.get("../").toAbsolutePath() + '\'', DOC_SAVE_PATH.getString(), "");
        docSavePath.addValueChangeListener(event -> {
            if (!docSavePath.isEmpty()) {
                File dir = new File(docSavePath.getValue());
                if (dir.exists() && dir.isDirectory()) {
                    Dialog dialog = new Dialog(new Label("Are you sure you want to change the archive path?\nOld files will be copied over to the new directory."));
                    Button button = new Button("Change", VaadinIcon.FOLDER_REMOVE.create(), e -> {
                        try {
                            FileUtils.moveDirectory(new File(DOC_SAVE_PATH.getString(), "img"), new File(dir, "img"));
                            FileUtils.moveDirectory(new File(DOC_SAVE_PATH.getString(), "pdf"), new File(dir, "pdf"));
                            DOC_SAVE_PATH.setString(docSavePath.getValue());
                            Notify.info("Doc save path saved.");
                        } catch (IOException ex) {
                            Notify.error("Error trying to move folder: " + ex.getMessage());
                        }
                    });
                    button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                    dialog.add(button);
                    dialog.open();
                } else {
                    Notify.error("Directory " + docSavePath.getValue() + " does not exist.");
                }
            }
        });

        add(createSection("Docs", dateScanFormats, imageParserDpi, pollingInterval, maxUploadFileSize, docSavePath));

        Checkbox autoTag = new Checkbox("Auto tag", AUTO_TAG.getBoolean());
        ComboBox<Tag> autoTagId = new ComboBox<>("Auto tag");
        autoTagId.setPreventInvalidInput(true);
        autoTagId.setAllowCustomValue(false);
        autoTagId.setItems(tagService.findAll());
        autoTagId.setValue(tagService.load(AUTO_TAG_ID.getLong()));
        autoTagId.setReadOnly(!autoTag.getValue());
        autoTagId.setItemLabelGenerator(Tag::getName);
        autoTag.addValueChangeListener(event -> {
            autoTagId.setReadOnly(!event.getValue());
            AUTO_TAG.setBoolean(autoTag.getValue());
            Notify.info("Auto tag saved.");
        });
        autoTagId.addValueChangeListener(event -> {
            AUTO_TAG_ID.setLong(autoTagId.getValue().getId());
            Notify.info("Auto tag saved");
        });
        add(createSection("Tags", autoTag, autoTagId));

        TextField githubUser = new TextField("Github User", GITHUB_USER.getString(), "");
        githubUser.addValueChangeListener(event -> {
            GITHUB_USER.setString(githubUser.getValue());
            Notify.info("Github user saved.");
        });
        PasswordField githubPassword = new PasswordField("Github Password");
        githubPassword.setValue(GITHUB_PASSWORD.getString());
        githubPassword.addValueChangeListener(event -> {
            GITHUB_PASSWORD.setString(githubPassword.getValue());
            Notify.info("Github password saved.");
        });
        NumberField updateCheckInterval = new NumberField("Update check interval (minutes)", UPDATE_CHECK_INTERVAL.getDouble(), e -> {});
        updateCheckInterval.addValueChangeListener(event -> {
            if (!updateCheckInterval.isEmpty() && updateCheckInterval.getValue() > 0) {
                UPDATE_CHECK_INTERVAL.setInt(updateCheckInterval.getValue().intValue());
                Notify.info("Update check interval saved.");
            }
        });
        add(createSection("Update", githubUser, githubPassword, updateCheckInterval));
    }

    private static Card createSection(String title, Component... components) {
        Details details = new Details(title, new FormLayout(components));
        details.setOpened(true);
        details.getElement().getStyle().set("padding", "5px").set("width", "100%");
        Card card = new Card(details);
        card.setWidthFull();
        return card;
    }
}