package com.dude.dms.ui.views

import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.mail.EmailManager
import com.dude.dms.brain.options.Options
import com.dude.dms.ui.Const
import com.dude.dms.ui.MainView
import com.github.appreciated.card.Card
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.listbox.MultiSelectListBox
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.IntegerField
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

    private val options = Options.get()

    init {
        createViewSection()
        createDocsSection()
        createMailSection()
        createStorageSection()
        createTagSection()
        createUpdateSection()
    }

    private fun createViewSection() {
        val dateFormat = TextField("Date format", options.view.dateFormat) {
            if (it.value.isNotEmpty()) {
                options.view.dateFormat = it.value
                options.save()
                LOGGER.showInfo("Date format saved.")
            }
        }
        val locale = ComboBox<Locale>("Language").apply {
            setItems(*Locale.getAvailableLocales())
            value = Locale.forLanguageTag(options.view.locale)
            isAllowCustomValue = false
            isPreventInvalidInput = true
            addValueChangeListener {
                if (!isEmpty) {
                    options.view.locale = value.toLanguageTag()
                    options.save()
                    LOGGER.showInfo("Language changed.")
                }
            }
        }
        val simpleColors = Checkbox("Simple tag colors", options.tag.simpleColors).apply {
            addValueChangeListener {
                options.tag.simpleColors = value
                options.save()
                LOGGER.showInfo("Simple tag colors saved")
            }
        }
        val darkMode = Checkbox("Dark mode", options.view.darkMode).apply {
            addValueChangeListener { event ->
                val themeList = UI.getCurrent().element.themeList
                themeList.clear()
                themeList.add(if (event.value) Lumo.DARK else Lumo.LIGHT)
                options.view.darkMode = value
                options.save()
                LOGGER.showInfo("Dark mode saved.")
            }
        }
        val notifyPosition = ComboBox<Notification.Position>("Notification position").apply {
            setItems(*Notification.Position.values())
            value = Notification.Position.valueOf(options.view.notificationPosition)
            isAllowCustomValue = false
            isPreventInvalidInput = true
            setWidthFull()
            addValueChangeListener {
                if (!isEmpty) {
                    options.view.notificationPosition = value.name
                    options.save()
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
        val imageParserDpi = NumberField("Image Parser DPI", options.doc.imageParserDpi) {
            if (it.value != null && it.value > 0) {
                try {
                    options.doc.imageParserDpi = it.value
                    options.save()
                    LOGGER.showInfo("Image parser DPI saved.")
                } catch (ignored: NumberFormatException) {
                }
            }
        }
        val pollingInterval = IntegerField("Polling interval (seconds)", options.doc.pollingInterval) {
            if (it.value != null && it.value > 0) {
                options.doc.pollingInterval = it.value
                options.save()
                LOGGER.showInfo("Polling interval saved.")
            }
        }
        val dateScanFormats = TextField("Date scan formats", options.view.dateScanFormats.joinToString(",")) {
            if (!it.value.isNullOrEmpty()) {
                options.view.dateScanFormats = it.value.split(",")
                options.save()
                LOGGER.showInfo("Date scan formats saved..")
            }
        }

        add(createSection("Docs", imageParserDpi, pollingInterval, dateScanFormats))
    }


    private fun createMailSection() {
        val imapHost = TextField("IMAP Host", options.mail.host) {
            if (!it.value.isNullOrEmpty()) {
                options.mail.host = it.value
                options.save()
                LOGGER.showInfo("IMAP Host saved.")
            }
        }
        val imapPort = IntegerField("IMAP Port", options.mail.port) {
            if (it.value != null && it.value > 0) {
                try {
                    options.mail.port = it.value
                    options.save()
                    LOGGER.showInfo("IMAP Port saved.")
                } catch (ignored: NumberFormatException) {
                }
            }
        }
        val imapLogin = TextField("IMAP Login", options.mail.login) {
            if (!it.value.isNullOrEmpty()) {
                options.mail.login = it.value
                options.save()
                LOGGER.showInfo("IMAP Login saved.")
            }
        }
        val imapPassword = PasswordField("IMAP Password", options.mail.password) {
            if (!it.value.isNullOrEmpty()) {
                options.mail.password = it.value
                options.save()
                LOGGER.showInfo("IMAP Password saved.")
            }
        }
        val imapPolling = IntegerField("IMAP Polling interval (min)", options.mail.pollingInterval) {
            if (it.value != null && it.value > 0) {
                options.mail.pollingInterval = it.value
                options.save()
                LOGGER.showInfo("IMAP Polling interval saved.")
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

        add(createSection("Mails", imapHost, imapPort, imapLogin, imapPassword, imapPolling, imapTest))
    }

    private fun createStorageSection() {
        val docSavePath = TextField("Doc save path (absolute or relative to '" + Paths.get("../").toAbsolutePath() + '\'', options.doc.savePath) {
            if (it.value.isNotEmpty()) {
                val dir = File(it.value)
                if (dir.exists() && dir.isDirectory) {
                    options.doc.savePath = it.value
                    options.save()
                    LOGGER.showInfo("Doc save path saved.")
                } else {
                    LOGGER.showError("Directory " + it.value + " does not exist.")
                }
            }
        }
        val maxUploadFileSize = IntegerField("Maximum upload file size (MB)", options.storage.maxUploadFileSize) {
            if (it.value != null && it.value > 0) {
                options.storage.maxUploadFileSize = it.value
                options.save()
                LOGGER.showInfo("Maximum upload file size saved.")
            }
        }

        add(createSection("Storage", docSavePath, maxUploadFileSize))
    }

    private fun createTagSection() {
        val autoTag = MultiSelectListBox<String>()
        autoTag.setItems(tagService.findAll().map { it.name })
        autoTag.addComponentAsFirst(Text("Automatic Tags"))
        autoTag.select(options.tag.automaticTags)
        autoTag.addSelectionListener {
            options.tag.automaticTags = it.allSelectedItems.toList()
            options.save()
            LOGGER.showInfo("Automatic Tags saved..")
        }

        add(createSection("Tags", autoTag))
    }

    private fun createUpdateSection() {
        val updateCheckInterval = IntegerField("Update check interval (minutes)", options.update.checkInterval) {
            if (it.value != null && it.value > 0) {
                options.update.checkInterval = it.value
                options.save()
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