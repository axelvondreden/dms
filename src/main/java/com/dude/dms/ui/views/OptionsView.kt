package com.dude.dms.ui.views

import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.mail.MailManager
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.extensions.card
import com.dude.dms.ui.Const
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
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
        alignItems = FlexComponent.Alignment.CENTER
        card {
            width = "50vw"

            details(t("view")) {
                isOpened = true
                style["width"] = "100%"
                style["padding"] = "5px"

                content {
                    formLayout {
                        comboBox<Locale>(t("language")) {
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
                        textField("${t("date")} Format") {
                            value = options.view.dateFormat
                            addValueChangeListener {
                                if (it.value.isNotEmpty()) {
                                    options.view.dateFormat = it.value
                                    save()
                                }
                            }
                        }
                        checkBox(t("darkmode")) {
                            value = options.view.darkMode
                            addValueChangeListener { event ->
                                val themeList = UI.getCurrent().element.themeList
                                themeList.clear()
                                themeList.add(if (event.value) Lumo.DARK else Lumo.LIGHT)
                                options.view.darkMode = value
                                save()
                            }
                        }
                        checkBox(t("words.preview.show")) {
                            value = options.view.loadWordsInPreview
                            addValueChangeListener {
                                options.view.loadWordsInPreview = value
                                save()
                            }
                        }
                    }
                }
            }
        }
        card {
            width = "50vw"

            details(t("Popups")) {
                isOpened = true
                style["width"] = "100%"
                style["padding"] = "5px"

                content {
                    formLayout {
                        comboBox<Notification.Position>("Popup Position") {
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
                    }
                }
            }
        }
        card {
            width = "50vw"

            details(t("docs")) {
                isOpened = true
                style["width"] = "100%"
                style["padding"] = "5px"

                content {
                    formLayout {
                        numberField("Import DPI") {
                            value = options.doc.imageParserDpi
                            addValueChangeListener {
                                if (it.value != null && it.value > 0) {
                                    try {
                                        options.doc.imageParserDpi = it.value
                                        save()
                                    } catch (ignored: NumberFormatException) {
                                    }
                                }
                            }
                        }
                        textField("${t("date")} Format") {
                            value = options.view.dateScanFormats.joinToString(",")
                            addValueChangeListener {
                                if (!it.value.isNullOrEmpty()) {
                                    options.view.dateScanFormats = it.value.split(",")
                                    save()
                                }
                            }
                        }
                        comboBox<String>("OCR ${t("language")}") {
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
                    }
                }
            }
        }
        card {
            width = "50vw"

            details(t("mails")) {
                isOpened = true
                style["width"] = "100%"
                style["padding"] = "5px"

                content {
                    formLayout {
                        textField("IMAP Host") {
                            value = options.mail.host
                            addValueChangeListener {
                                if (!it.value.isNullOrEmpty()) {
                                    options.mail.host = it.value
                                    save()
                                }
                            }
                        }
                        integerField("IMAP Port") {
                            value = options.mail.port
                            addValueChangeListener {
                                if (it.value != null && it.value > 0) {
                                    try {
                                        options.mail.port = it.value
                                        save()
                                    } catch (ignored: NumberFormatException) {
                                    }
                                }
                            }
                        }
                        textField("IMAP Login") {
                            value = options.mail.login
                            addValueChangeListener {
                                if (!it.value.isNullOrEmpty()) {
                                    options.mail.login = it.value
                                    save()
                                }
                            }
                        }
                        passwordField("IMAP ${t("password")}") {
                            value = options.mail.password
                            addValueChangeListener {
                                if (!it.value.isNullOrEmpty()) {
                                    options.mail.password = it.value
                                    save()
                                }
                            }
                        }
                        integerField("IMAP Interval (min)") {
                            value = options.mail.pollingInterval
                            addValueChangeListener {
                                if (it.value != null && it.value > 0) {
                                    options.mail.pollingInterval = it.value
                                    save()
                                }
                            }
                        }
                        button(t("connect")) {
                            onLeftClick {
                                try {
                                    mailManager.testConnection()
                                    LOGGER.showInfo(t("settings.saved"), UI.getCurrent())
                                } catch (e: MessagingException) {
                                    LOGGER.showError(t("mail.imap.error", e), UI.getCurrent())
                                }
                            }
                        }
                    }
                }
            }
        }
        card {
            width = "50vw"

            details("Storage") {
                isOpened = true
                style["width"] = "100%"
                style["padding"] = "5px"

                content {
                    formLayout {
                        textField("Doc save path (absolute or relative to '${Paths.get("../").toAbsolutePath()}')") {
                            value = options.doc.savePath
                            addValueChangeListener {
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
                        }
                        integerField("Maximum upload file size (MB)") {
                            value = options.storage.maxUploadFileSize
                            addValueChangeListener {
                                if (it.value != null && it.value > 0) {
                                    options.storage.maxUploadFileSize = it.value
                                    save()
                                }
                            }
                        }
                    }
                }
            }
        }
        card {
            width = "50vw"

            details(t("tags")) {
                isOpened = true
                style["width"] = "100%"
                style["padding"] = "5px"

                content {
                    formLayout {
                        multiSelectListBox<String> {
                            val entries = tagService.findAll().map { it.name }
                            setItems(entries)
                            addComponentAsFirst(Text("Automatic Tags"))
                            select(options.tag.automaticTags.filter { it in entries })
                            addSelectionListener {
                                options.tag.automaticTags = it.allSelectedItems.toList()
                                save()
                            }
                        }
                    }
                }
            }
        }
        card {
            width = "50vw"

            details("Update") {
                isOpened = true
                style["width"] = "100%"
                style["padding"] = "5px"

                content {
                    formLayout {
                        integerField("Update check interval (minutes)") {
                            value = options.update.checkInterval
                            addValueChangeListener {
                                if (it.value != null && it.value > 0) {
                                    options.update.checkInterval = it.value
                                    save()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun save() {
        options.save()
        LOGGER.showInfo(t("settings.saved"), UI.getCurrent())
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(OptionsView::class.java)
    }
}