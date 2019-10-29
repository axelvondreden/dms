package com.dude.dms.ui.views;

import com.dude.dms.backend.data.tags.Tag;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.github.appreciated.card.Card;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
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
                Notification.show("Date format saved.");
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
                Notification.show("Language changed.");
            }
        });
        Checkbox simpleColors = new Checkbox("Simple tag colors", SIMPLE_TAG_COLORS.getBoolean());
        simpleColors.addValueChangeListener(event -> {
            SIMPLE_TAG_COLORS.setBoolean(simpleColors.getValue());
            Notification.show("Simple tag colors saved");
        });
        Checkbox darkMode = new Checkbox("Dark mode", DARK_MODE.getBoolean());
        darkMode.addValueChangeListener(event -> {
            DARK_MODE.setBoolean(darkMode.getValue());
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            themeList.clear();
            themeList.add(event.getValue() ? Lumo.DARK : Lumo.LIGHT);
            Notification.show("Dark mode saved.");
        });
        add(createSection("View", locale, dateFormat, simpleColors, darkMode));

        TextField dateScanFormats = new TextField("Date scan formats", DATE_SCAN_FORMATS.getString(), "");
        dateScanFormats.addValueChangeListener(event -> {
            if (!dateScanFormats.isEmpty()) {
                DATE_SCAN_FORMATS.setString(String.join(",", dateScanFormats.getValue()));
                Notification.show("Date scan formats saved.");
            }
        });
        NumberField imageParserDpi = new NumberField("Image Parser DPI", IMAGE_PARSER_DPI.getDouble(), e -> {});
        imageParserDpi.addValueChangeListener(event -> {
            if (!imageParserDpi.isEmpty()) {
                try {
                    IMAGE_PARSER_DPI.setDouble(imageParserDpi.getValue());
                    Notification.show("Image parser DPI saved.");
                } catch (NumberFormatException ignored) {
                }
            }
        });
        NumberField pollingInterval = new NumberField("Polling interval (seconds)", POLL_INTERVAL.getDouble(), e -> {});
        pollingInterval.addValueChangeListener(event -> {
            if (!pollingInterval.isEmpty() && pollingInterval.getValue() > 0) {
                POLL_INTERVAL.setInt(pollingInterval.getValue().intValue());
                Notification.show("Polling interval saved.");
            }
        });
        NumberField maxUploadFileSize = new NumberField("Maximum upload file size (MB)", MAX_UPLOAD_FILE_SIZE.getDouble(), e -> {});
        maxUploadFileSize.addValueChangeListener(event -> {
            if (!maxUploadFileSize.isEmpty() && maxUploadFileSize.getValue() > 0) {
                MAX_UPLOAD_FILE_SIZE.setInt(maxUploadFileSize.getValue().intValue());
                Notification.show("Maximum upload file size saved.");
            }
        });
        TextField docSavePath = new TextField("Doc save path (absolute or relative to '" + Paths.get("../").toAbsolutePath() + '\'', DOC_SAVE_PATH.getString(), "");
        docSavePath.addValueChangeListener(event -> {
            if (!docSavePath.isEmpty()) {
                File dir = new File(docSavePath.getValue());
                if (dir.exists() && dir.isDirectory()) {
                    new ConfirmDialog("Changing archive path!", "Are you sure you want to change the archive path?\nOld files will be copied over to the new directory.", "Change", e -> {
                        try {
                            FileUtils.moveDirectory(new File(DOC_SAVE_PATH.getString(), "img"), new File(dir, "img"));
                            FileUtils.moveDirectory(new File(DOC_SAVE_PATH.getString(), "pdf"), new File(dir, "pdf"));
                            DOC_SAVE_PATH.setString(docSavePath.getValue());
                            Notification.show("Doc save path saved.");
                        } catch (IOException ex) {
                            Notification.show("Error trying to move folder: " + ex.getMessage());
                        }
                    }, "Cancel", e -> {}).open();
                } else {
                    Notification.show("Directory " + docSavePath.getValue() + " does not exist.");
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
            Notification.show("Auto tag saved.");
        });
        autoTagId.addValueChangeListener(event -> {
            AUTO_TAG_ID.setLong(autoTagId.getValue().getId());
            Notification.show("Auto tag saved");
        });
        add(createSection("Tags", autoTag, autoTagId));

        TextField githubUser = new TextField("Github User", GITHUB_USER.getString(), "");
        githubUser.addValueChangeListener(event -> {
            GITHUB_USER.setString(githubUser.getValue());
            Notification.show("Github user saved.");
        });
        PasswordField githubPassword = new PasswordField("Github Password");
        githubPassword.setValue(GITHUB_PASSWORD.getString());
        githubPassword.addValueChangeListener(event -> {
            GITHUB_PASSWORD.setString(githubPassword.getValue());
            Notification.show("Github password saved.");
        });
        NumberField updateCheckInterval = new NumberField("Update check interval (minutes)", UPDATE_CHECK_INTERVAL.getDouble(), e -> {});
        updateCheckInterval.addValueChangeListener(event -> {
            if (!updateCheckInterval.isEmpty() && updateCheckInterval.getValue() > 0) {
                UPDATE_CHECK_INTERVAL.setInt(updateCheckInterval.getValue().intValue());
                Notification.show("Update check interval saved.");
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