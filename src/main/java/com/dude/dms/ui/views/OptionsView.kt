package com.dude.dms.ui.views

import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.OptionKey
import com.dude.dms.brain.mail.EmailManager
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.TagService
import com.dude.dms.ui.Const
import com.dude.dms.ui.MainView
import com.github.appreciated.card.Card
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ItemLabelGenerator
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.NumberField
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.lumo.Lumo
import java.io.File
import java.nio.file.Paths
import java.util.*
import javax.mail.MessagingException

@Route(value = Const.PAGE_OPTIONS, layout = MainView::class)
@PageTitle("Options")
class OptionsView(
        private val fileManager: FileManager,
        private val tagService: TagService,
        private val emailManager: EmailManager
) : VerticalLayout() {

    init {
        createViewSection()
        createDocsSection()
        createMailSection()
        createStorageSection()
        createTagSection()
        createUpdateSection()
    }

    private fun createViewSection() {
        val dateFormat = TextField("Date format", OptionKey.DATE_FORMAT.string) {
            if (it.value.isNotEmpty()) {
                OptionKey.DATE_FORMAT.string = it.value
                LOGGER.showInfo("Date format saved.")
            }
        }
        val locale = ComboBox<Locale>("Language").apply {
            setItems(*Locale.getAvailableLocales())
            value = Locale.forLanguageTag(OptionKey.LOCALE.string)
            isAllowCustomValue = false
            isPreventInvalidInput = true
            addValueChangeListener {
                if (!isEmpty) {
                    OptionKey.LOCALE.string = value.toLanguageTag()
                    LOGGER.showInfo("Language changed.")
                }
            }
        }
        val simpleColors = Checkbox("Simple tag colors", OptionKey.SIMPLE_TAG_COLORS.boolean).apply {
            addValueChangeListener {
                OptionKey.SIMPLE_TAG_COLORS.boolean = value
                LOGGER.showInfo("Simple tag colors saved")
            }
        }
        val darkMode = Checkbox("Dark mode", OptionKey.DARK_MODE.boolean).apply {
            addValueChangeListener { event ->
                OptionKey.DARK_MODE.boolean = value
                val themeList = UI.getCurrent().element.themeList
                themeList.clear()
                themeList.add(if (event.value) Lumo.DARK else Lumo.LIGHT)
                LOGGER.showInfo("Dark mode saved.")
            }
        }
        val notifyPosition = ComboBox<Notification.Position>("Notification position").apply {
            setItems(*Notification.Position.values())
            value = Notification.Position.valueOf(OptionKey.NOTIFY_POSITION.string)
            isAllowCustomValue = false
            isPreventInvalidInput = true
            setWidthFull()
            addValueChangeListener {
                if (!isEmpty) {
                    OptionKey.NOTIFY_POSITION.string = value.name
                    LOGGER.showInfo("Notification position saved.")
                }
            }
        }
        val notifyWrapper = HorizontalLayout(notifyPosition, Button("Test") { LOGGER.showInfo("Hi, I am just a test!") }).apply {
            setWidthFull()
            alignItems = FlexComponent.Alignment.END
        }

        add(createSection("View", locale, dateFormat, simpleColors, darkMode, notifyWrapper))
    }

    private fun createDocsSection() {
        val dateScanFormats = TextField("Date scan formats", OptionKey.DATE_SCAN_FORMATS.string) {
            if (it.value.isNotEmpty()) {
                OptionKey.DATE_SCAN_FORMATS.string = java.lang.String.join(",", it.value)
                LOGGER.showInfo("Date scan formats saved.")
            }
        }
        val imageParserDpi = NumberField("Image Parser DPI", OptionKey.IMAGE_PARSER_DPI.double) {
            if (it.value != null) {
                try {
                    OptionKey.IMAGE_PARSER_DPI.double = it.value
                    LOGGER.showInfo("Image parser DPI saved.")
                } catch (ignored: NumberFormatException) {
                }
            }
        }
        val pollingInterval = NumberField("Polling interval (seconds)", OptionKey.POLL_INTERVAL.double) {
            if (it.value != null && it.value > 0) {
                OptionKey.POLL_INTERVAL.int = it.value.toInt()
                LOGGER.showInfo("Polling interval saved.")
            }
        }
        val maxUploadFileSize = NumberField("Maximum upload file size (MB)", OptionKey.MAX_UPLOAD_FILE_SIZE.double) {
            if (it.value != null && it.value > 0) {
                OptionKey.MAX_UPLOAD_FILE_SIZE.int = it.value.toInt()
                LOGGER.showInfo("Maximum upload file size saved.")
            }
        }

        add(createSection("Docs", dateScanFormats, imageParserDpi, pollingInterval, maxUploadFileSize))
    }

    private fun createMailSection() {
        val imapHost = TextField("IMAP Host", OptionKey.IMAP_HOST.string) {
            if (!it.value.isNullOrEmpty()) {
                OptionKey.IMAP_HOST.string = it.value
                LOGGER.showInfo("IMAP Host saved.")
            }
        }
        val imapPort = NumberField("IMAP Port", OptionKey.IMAP_PORT.double) {
            if (it.value != null) {
                try {
                    OptionKey.IMAP_PORT.int = it.value.toInt()
                    LOGGER.showInfo("IMAP Port saved.")
                } catch (ignored: NumberFormatException) {
                }
            }
        }
        val imapLogin = TextField("IMAP Login", OptionKey.IMAP_LOGIN.string) {
            if (!it.value.isNullOrEmpty()) {
                OptionKey.IMAP_LOGIN.string = it.value
                LOGGER.showInfo("IMAP Login saved.")
            }
        }
        val imapPassword = PasswordField("IMAP Password", OptionKey.IMAP_PASSWORD.string) {
            if (!it.value.isNullOrEmpty()) {
                OptionKey.IMAP_PASSWORD.string = it.value
                LOGGER.showInfo("IMAP Password saved.")
            }
        }
        val imapTest = Button("Test Connection") {
            try {
                emailManager.testConnection()
                LOGGER.showInfo("Test Successfull.")
            } catch (e: MessagingException) {
                LOGGER.showError("Test Failed: ${e.message}")
            }
        }

        add(createSection("Mails", imapHost, imapPort, imapLogin, imapPassword, imapTest))
    }

    private fun createStorageSection() {
        val docSavePath = TextField("Doc save path (absolute or relative to '" + Paths.get("../").toAbsolutePath() + '\'', OptionKey.DOC_SAVE_PATH.string) {
            if (it.value.isNotEmpty()) {
                val dir = File(it.value)
                if (dir.exists() && dir.isDirectory) {
                    OptionKey.DOC_SAVE_PATH.string = it.value
                    LOGGER.showInfo("Doc save path saved.")
                } else {
                    LOGGER.showError("Directory " + it.value + " does not exist.")
                }
            }
        }

        add(createSection("Storage", docSavePath))
    }

    private fun createTagSection() {
        val autoTagId = ComboBox<Tag>("Auto tag")
        val autoTag = Checkbox("Auto tag", OptionKey.AUTO_TAG.boolean).apply {
            addValueChangeListener { event ->
                autoTagId.isReadOnly = !event.value!!
                OptionKey.AUTO_TAG.boolean = value
                LOGGER.showInfo("Auto tag saved.")
            }
        }

        autoTagId.isPreventInvalidInput = true
        autoTagId.isAllowCustomValue = false
        autoTagId.setItems(tagService.findAll())
        autoTagId.value = tagService.load(OptionKey.AUTO_TAG_ID.long)
        autoTagId.isReadOnly = !autoTag.value
        autoTagId.itemLabelGenerator = ItemLabelGenerator { obj: Tag -> obj.name }
        autoTagId.addValueChangeListener {
            OptionKey.AUTO_TAG_ID.long = autoTagId.value.id
            LOGGER.showInfo("Auto tag saved")
        }

        add(createSection("Tags", autoTag, autoTagId))
    }

    private fun createUpdateSection() {
        val updateCheckInterval = NumberField("Update check interval (minutes)", OptionKey.UPDATE_CHECK_INTERVAL.double) {
            if (it.value != null && it.value > 0) {
                OptionKey.UPDATE_CHECK_INTERVAL.int = it.value.toInt()
                LOGGER.showInfo("Update check interval saved.")
            }
        }

        add(createSection("Update", updateCheckInterval))
    }

    private fun createSection(title: String, vararg components: Component): Card {
        val details = Details(title, FormLayout(*components)).apply {
            isOpened = true
            element.style.set("padding", "5px")["width"] = "100%"
        }
        return Card(details).apply { setWidthFull() }
    }

    private fun tryFtp() {
        val success = fileManager.testFtp()
        if (success) {
            LOGGER.showInfo("FTP Test: successful!")
        } else {
            LOGGER.showError("FTP Test: failed!")
        }
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(OptionsView::class.java)
    }

    /*TextField ftpUrl = new TextField("FTP URL", FTP_URL.getString(), "ftps://");
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

        ComboBox<String> backupMethod = new ComboBox<>("Method");
        backupMethod.setItems("ftp");
        backupMethod.setValue(BACKUP_METHOD.getString());
        backupMethod.setAllowCustomValue(false);
        backupMethod.setPreventInvalidInput(true);
        backupMethod.addValueChangeListener(event -> {
            if (!event.getHasValue().isEmpty()) {
                BACKUP_METHOD.setString(event.getValue());
                LOGGER.showInfo("Backup method saved.");
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
        add(createSection("Backup", backupMethod, ));
        boolean isFtp = "ftp".equalsIgnoreCase(DOC_SAVE_TYPE.getString());
        ftpUrl.setEnabled(isFtp);
        ftpUser.setEnabled(isFtp);
        ftpPassword.setEnabled(isFtp);
        ftpPort.setEnabled(isFtp);*/
}