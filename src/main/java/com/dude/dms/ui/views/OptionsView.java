package com.dude.dms.ui.views;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.brain.FileManager;
import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.github.appreciated.card.Card;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Paths;
import java.util.Locale;

import static com.dude.dms.backend.brain.OptionKey.*;

@Route(value = Const.PAGE_OPTIONS, layout = MainView.class)
@PageTitle("Options")
public class OptionsView extends VerticalLayout {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(OptionsView.class);

    @Autowired
    public OptionsView(TagService tagService) {
        TextField dateFormat = new TextField("Date format", DATE_FORMAT.getString(), "");
        dateFormat.addValueChangeListener(event -> {
            if (!dateFormat.isEmpty()) {
                DATE_FORMAT.setString(dateFormat.getValue());
                LOGGER.showInfo("Date format saved.");
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
                LOGGER.showInfo("Language changed.");
            }
        });
        Checkbox simpleColors = new Checkbox("Simple tag colors", SIMPLE_TAG_COLORS.getBoolean());
        simpleColors.addValueChangeListener(event -> {
            SIMPLE_TAG_COLORS.setBoolean(simpleColors.getValue());
            LOGGER.showInfo("Simple tag colors saved");
        });
        Checkbox darkMode = new Checkbox("Dark mode", DARK_MODE.getBoolean());
        darkMode.addValueChangeListener(event -> {
            DARK_MODE.setBoolean(darkMode.getValue());
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            themeList.clear();
            themeList.add(event.getValue() ? Lumo.DARK : Lumo.LIGHT);
            LOGGER.showInfo("Dark mode saved.");
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
                LOGGER.showInfo("Notification position saved.");
            }
        });
        Button notifyTest = new Button("Test", e -> LOGGER.showInfo("Hi, I am just a test!"));
        HorizontalLayout notifyWrapper = new HorizontalLayout(notifyPosition, notifyTest);
        notifyWrapper.setWidthFull();
        notifyWrapper.setAlignItems(Alignment.END);
        add(createSection("View", locale, dateFormat, simpleColors, darkMode, notifyWrapper));

        TextField dateScanFormats = new TextField("Date scan formats", DATE_SCAN_FORMATS.getString(), "");
        dateScanFormats.addValueChangeListener(event -> {
            if (!dateScanFormats.isEmpty()) {
                DATE_SCAN_FORMATS.setString(String.join(",", dateScanFormats.getValue()));
                LOGGER.showInfo("Date scan formats saved.");
            }
        });
        NumberField imageParserDpi = new NumberField("Image Parser DPI", IMAGE_PARSER_DPI.getDouble(), e -> {});
        imageParserDpi.addValueChangeListener(event -> {
            if (!imageParserDpi.isEmpty()) {
                try {
                    IMAGE_PARSER_DPI.setDouble(imageParserDpi.getValue());
                    LOGGER.showInfo("Image parser DPI saved.");
                } catch (NumberFormatException ignored) {
                }
            }
        });
        NumberField pollingInterval = new NumberField("Polling interval (seconds)", POLL_INTERVAL.getDouble(), e -> {});
        pollingInterval.addValueChangeListener(event -> {
            if (!pollingInterval.isEmpty() && pollingInterval.getValue() > 0) {
                POLL_INTERVAL.setInt(pollingInterval.getValue().intValue());
                LOGGER.showInfo("Polling interval saved.");
            }
        });
        NumberField maxUploadFileSize = new NumberField("Maximum upload file size (MB)", MAX_UPLOAD_FILE_SIZE.getDouble(), e -> {});
        maxUploadFileSize.addValueChangeListener(event -> {
            if (!maxUploadFileSize.isEmpty() && maxUploadFileSize.getValue() > 0) {
                MAX_UPLOAD_FILE_SIZE.setInt(maxUploadFileSize.getValue().intValue());
                LOGGER.showInfo("Maximum upload file size saved.");
            }
        });
        add(createSection("Docs", dateScanFormats, imageParserDpi, pollingInterval, maxUploadFileSize));

        TextField docSavePath = new TextField("Doc save path (absolute or relative to '" + Paths.get("../").toAbsolutePath() + '\'', DOC_SAVE_PATH.getString(), "");
        docSavePath.addValueChangeListener(event -> {
            if (!docSavePath.isEmpty()) {
                File dir = new File(docSavePath.getValue());
                if (dir.exists() && dir.isDirectory()) {
                    DOC_SAVE_PATH.setString(docSavePath.getValue());
                    LOGGER.showInfo("Doc save path saved.");
                } else {
                    LOGGER.showError("Directory " + docSavePath.getValue() + " does not exist.");
                }
            }
        });

        TextField ftpUrl = new TextField("FTP URL", FTP_URL.getString(), "ftps://");
        ftpUrl.addValueChangeListener(event -> {
            FTP_URL.setString(event.getValue());
            LOGGER.showInfo("FTP URL saved.");
            tryFtp();
        });

        TextField ftpUser = new TextField("FTP User", FTP_USER.getString(), "");
        ftpUser.addValueChangeListener(event -> {
            FTP_USER.setString(event.getValue());
            LOGGER.showInfo("FTP User saved.");
            tryFtp();
        });

        PasswordField ftpPassword = new PasswordField("FTP Password");
        ftpPassword.setValue(FTP_PASSWORD.getString());
        ftpPassword.addValueChangeListener(event -> {
            FTP_PASSWORD.setString(event.getValue());
            LOGGER.showInfo("FTP Password saved.");
            tryFtp();
        });

        NumberField ftpPort = new NumberField("FTP Port", FTP_PORT.getDouble(), e -> {});
        ftpPort.addValueChangeListener(event -> {
            if (!ftpPort.isEmpty() && ftpPort.getValue() > 0) {
                FTP_PORT.setInt(ftpPort.getValue().intValue());
                LOGGER.showInfo("FTP Port saved.");
                tryFtp();
            }
        });

        ComboBox<String> storageType = new ComboBox<>("Storage Type");
        storageType.setItems("local", "ftp");
        storageType.setValue(DOC_SAVE_TYPE.getString());
        storageType.setAllowCustomValue(false);
        storageType.setPreventInvalidInput(true);
        storageType.addValueChangeListener(event -> {
            if (!event.getHasValue().isEmpty()) {
                DOC_SAVE_TYPE.setString(event.getValue());
                LOGGER.showInfo("Storage type saved. Old files need to be copied to the new location.");
                boolean isFtp = event.getValue().equalsIgnoreCase("ftp");
                ftpUrl.setEnabled(isFtp);
                ftpUser.setEnabled(isFtp);
                ftpPassword.setEnabled(isFtp);
                ftpPort.setEnabled(isFtp);
                if (isFtp) {
                    tryFtp();
                }
            }
        });
        add(createSection("Storage", storageType, docSavePath, ftpUrl, ftpUser, ftpPassword, ftpPort));
        boolean isFtp = "ftp".equalsIgnoreCase(DOC_SAVE_TYPE.getString());
        ftpUrl.setEnabled(isFtp);
        ftpUser.setEnabled(isFtp);
        ftpPassword.setEnabled(isFtp);
        ftpPort.setEnabled(isFtp);

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
            LOGGER.showInfo("Auto tag saved.");
        });
        autoTagId.addValueChangeListener(event -> {
            AUTO_TAG_ID.setLong(autoTagId.getValue().getId());
            LOGGER.showInfo("Auto tag saved");
        });
        add(createSection("Tags", autoTag, autoTagId));

        TextField githubUser = new TextField("Github User", GITHUB_USER.getString(), "");
        githubUser.addValueChangeListener(event -> {
            GITHUB_USER.setString(githubUser.getValue());
            LOGGER.showInfo("Github user saved.");
        });
        PasswordField githubPassword = new PasswordField("Github Password");
        githubPassword.setValue(GITHUB_PASSWORD.getString());
        githubPassword.addValueChangeListener(event -> {
            GITHUB_PASSWORD.setString(githubPassword.getValue());
            LOGGER.showInfo("Github password saved.");
        });
        NumberField updateCheckInterval = new NumberField("Update check interval (minutes)", UPDATE_CHECK_INTERVAL.getDouble(), e -> {});
        updateCheckInterval.addValueChangeListener(event -> {
            if (!updateCheckInterval.isEmpty() && updateCheckInterval.getValue() > 0) {
                UPDATE_CHECK_INTERVAL.setInt(updateCheckInterval.getValue().intValue());
                LOGGER.showInfo("Update check interval saved.");
            }
        });
        add(createSection("Update", githubUser, githubPassword, updateCheckInterval));
    }

    private void tryFtp() {
        boolean success = FileManager.testFtp();
        if (success) {
            LOGGER.showInfo("FTP Test: successful!");
        } else {
            LOGGER.showError("FTP Test: failed!");
        }
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