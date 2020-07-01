package com.dude.dms.ui.views

import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.mail.MailManager
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.ui.Const
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
class OptionsView(private val tagService: TagService, private val mailManager: MailManager) : VerticalLayout() {

    private val options = Options.get()

    init {
        createViewSection()
        createNotificationSection()
        createDocsSection()
        createMailSection()
        createStorageSection()
        createTagSection()
        createUpdateSection()
    }

    private fun createViewSection() {
        val dateFormat = TextField("${t("date")} Format", options.view.dateFormat) {
            if (it.value.isNotEmpty()) {
                options.view.dateFormat = it.value
                save()
            }
        }
        val locale = ComboBox<Locale>(t("language")).apply {
            setItems(*Const.LOCALES)
            value = Locale.forLanguageTag(options.view.locale)
            isAllowCustomValue = false
            isPreventInvalidInput = true
            addValueChangeListener {
                if (!isEmpty) {
                    options.view.locale = value.toLanguageTag()
                    save()
                }
            }
        }
        val darkMode = Checkbox(t("darkmode"), options.view.darkMode).apply {
            addValueChangeListener { event ->
                val themeList = UI.getCurrent().element.themeList
                themeList.clear()
                themeList.add(if (event.value) Lumo.DARK else Lumo.LIGHT)
                options.view.darkMode = value
                save()
            }
        }
        val loadWordsInPreview = Checkbox(t("words.preview.show"), options.view.loadWordsInPreview).apply {
            addValueChangeListener {
                options.view.loadWordsInPreview = value
                save()
            }
        }

        add(createSection(t("view"), locale, dateFormat, darkMode, loadWordsInPreview))
    }

    private fun createNotificationSection() {
        val notifyPosition = ComboBox<Notification.Position>("Popup Position").apply {
            setItems(*Notification.Position.values())
            value = Notification.Position.valueOf(options.view.notificationPosition)
            isAllowCustomValue = false
            isPreventInvalidInput = true
            setWidthFull()
            addValueChangeListener {
                if (!isEmpty) {
                    options.view.notificationPosition = value.name
                    save()
                }
            }
        }

        add(createSection("Popups", notifyPosition))
    }

    private fun createDocsSection() {
        val imageParserDpi = NumberField("Import DPI", options.doc.imageParserDpi) {
            if (it.value != null && it.value > 0) {
                try {
                    options.doc.imageParserDpi = it.value
                    save()
                } catch (ignored: NumberFormatException) {
                }
            }
        }
        val dateScanFormats = TextField("${t("date")} Format", options.view.dateScanFormats.joinToString(",")) {
            if (!it.value.isNullOrEmpty()) {
                options.view.dateScanFormats = it.value.split(",")
                save()
            }
        }
        val ocrLanguage = ComboBox<String>("OCR ${t("language")}").apply {
            setItems(*Const.OCR_LANGUAGES)
            value = options.doc.ocrLanguage
            isAllowCustomValue = false
            isPreventInvalidInput = true
            addValueChangeListener {
                if (!isEmpty) {
                    options.doc.ocrLanguage = value
                    save()
                }
            }
        }

        add(createSection(t("docs"), imageParserDpi, dateScanFormats, ocrLanguage))
    }


    private fun createMailSection() {
        val imapHost = TextField("IMAP Host", options.mail.host) {
            if (!it.value.isNullOrEmpty()) {
                options.mail.host = it.value
                save()
            }
        }
        val imapPort = IntegerField("IMAP Port", options.mail.port) {
            if (it.value != null && it.value > 0) {
                try {
                    options.mail.port = it.value
                    save()
                } catch (ignored: NumberFormatException) {
                }
            }
        }
        val imapLogin = TextField("IMAP Login", options.mail.login) {
            if (!it.value.isNullOrEmpty()) {
                options.mail.login = it.value
                save()
            }
        }
        val imapPassword = PasswordField("IMAP ${t("password")}", options.mail.password) {
            if (!it.value.isNullOrEmpty()) {
                options.mail.password = it.value
                save()
            }
        }
        val imapPolling = IntegerField("IMAP Interval (min)", options.mail.pollingInterval) {
            if (it.value != null && it.value > 0) {
                options.mail.pollingInterval = it.value
                save()
            }
        }
        val imapTest = Button(t("connect")) {
            try {
                mailManager.testConnection()
                LOGGER.showInfo(t("settings.saved"), UI.getCurrent())
            } catch (e: MessagingException) {
                LOGGER.showError(t("mail.imap.error", e), UI.getCurrent())
            }
        }

        add(createSection(t("mails"), imapHost, imapPort, imapLogin, imapPassword, imapPolling, imapTest))
    }

    private fun createStorageSection() {
        val docSavePath = TextField("Doc save path (absolute or relative to '" + Paths.get("../").toAbsolutePath() + '\'', options.doc.savePath) {
            if (it.value.isNotEmpty()) {
                val dir = File(it.value)
                if (dir.exists() && dir.isDirectory) {
                    options.doc.savePath = it.value
                    save()
                } else {
                    LOGGER.showError("Directory " + it.value + " does not exist.", UI.getCurrent())
                }
            }
        }
        val maxUploadFileSize = IntegerField("Maximum upload file size (MB)", options.storage.maxUploadFileSize) {
            if (it.value != null && it.value > 0) {
                options.storage.maxUploadFileSize = it.value
                save()
            }
        }

        add(createSection("Storage", docSavePath, maxUploadFileSize))
    }

    private fun createTagSection() {
        val autoTag = MultiSelectListBox<String>()
        val entries = tagService.findAll().map { it.name }
        autoTag.setItems(entries)
        autoTag.addComponentAsFirst(Text("Automatic Tags"))
        autoTag.select(options.tag.automaticTags.filter { it in entries })
        autoTag.addSelectionListener {
            options.tag.automaticTags = it.allSelectedItems.toList()
            save()
        }

        add(createSection(t("tags"), autoTag))
    }

    private fun createUpdateSection() {
        val updateCheckInterval = IntegerField("Update check interval (minutes)", options.update.checkInterval) {
            if (it.value != null && it.value > 0) {
                options.update.checkInterval = it.value
                save()
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

    private fun save() {
        options.save()
        LOGGER.showInfo(t("settings.saved"), UI.getCurrent())
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(OptionsView::class.java)
    }
}